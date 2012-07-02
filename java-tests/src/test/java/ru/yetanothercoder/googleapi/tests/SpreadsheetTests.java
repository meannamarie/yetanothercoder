package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin.Response;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;
import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

public class SpreadsheetTests {

	public static final String FILE_NAME = "fintest";
	
	public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
	public static final String GDATA_VERSION_HTTP_HEADER = "GData-Version";
	public static final String CONTENT_LENGTH_HTTP_HEADER = "Content-Length";
	public static final String CONTENT_TYPE_HTTP_HEADER = "Content-Type";
	
	private static Response  authResp;
	private static ApacheHttpTransport transport;
	
	private static Entry testSpreadsheetEntry;
	private static Entry worksheetEntry;

	private static final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
	    .set("", "http://www.w3.org/2005/Atom")
	    .set("gd", "http://schemas.google.com/g/2005")
	    .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
	
	private static final XmlNamespaceDictionary WORKSHEET_NAMESPACE = new XmlNamespaceDictionary()
	    .set("", "http://www.w3.org/2005/Atom")
	    .set("gd", "http://schemas.google.com/g/2005")
	    .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
	    .set("gs", "http://schemas.google.com/spreadsheets/2006")
	    .set("gsx", "http://schemas.google.com/spreadsheets/2006/extended");
	
	private static final XmlNamespaceDictionary ROW_NAMESPACE = new XmlNamespaceDictionary()
	    .set("", "http://www.w3.org/2005/Atom")
	    .set("gd", "http://schemas.google.com/g/2005")
	    .set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
	    .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
	
	public static final XmlNamespaceDictionary CELL_NAMESPACE = new XmlNamespaceDictionary()
	    .set("", "http://www.w3.org/2005/Atom")
	    .set("batch", "http://schemas.google.com/gdata/batch")
	    .set("gd", "http://schemas.google.com/g/2005")
	    .set("gs", "http://schemas.google.com/spreadsheets/2006")
	    .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");
	
	@BeforeClass
	public static void authAndGetSpreadsheet() throws HttpResponseException, IOException {
		// auth
		transport = new ApacheHttpTransport();

		ClientLogin authenticator = new ClientLogin();
		authenticator.authTokenType = "wise";
		authenticator.transport = transport;
		authenticator.username = System.getProperty("user");
		authenticator.password = System.getProperty("pass");
		
		System.err.println("logining:");
		authResp = authenticator.authenticate();
		System.err.println("auth token:" + authResp.auth);
		
		// getting the sreadsheet
		GoogleUrl sUrl = new GoogleUrl(String.format(
				"https://spreadsheets.google.com/feeds/spreadsheets/private/full?title=%s&title-exact=true", 
				URLEncoder.encode(FILE_NAME, "UTF-8")));
		sUrl.setPrettyPrint(true);
		
		HttpRequest request = createGetRequest(sUrl);
		
		request.setParser(new XmlObjectParser(SPREADSHEET_NAMESPACE));
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 200);
		
		Feed f = resp.parseAs(Feed.class);
		Assert.assertTrue(f.getEntries().size() == 1);
		
		testSpreadsheetEntry = f.getEntries().get(0);
	}

	/**
	 * add worksheet and getting worksheet feed for the spreadsheet
	 * @throws IOException
	 */
	@Before
	public void addWorksheet() throws IOException {
		// adding new worksheet
		GoogleUrl url = new GoogleUrl(testSpreadsheetEntry.getContent().getSrc());
        url.setPrettyPrint(true);
		
		String newWorksheetName = "test" + Math.random();
		Entry newWorksheet = new Entry(newWorksheetName, 7, 17);
		
		AtomContent requestContent = AtomContent.forEntry(WORKSHEET_NAMESPACE, newWorksheet);
		HttpRequest request = transport.createRequestFactory().buildPostRequest(url, requestContent);
        request.setParser(new XmlObjectParser(WORKSHEET_NAMESPACE));
		addGoogleHeaders(request);
		
		System.err.println("trying adding:");
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 201);
		Entry added = resp.parseAs(Entry.class);
		System.err.println("added worksheet entry: " + added);
		worksheetEntry = added;
	}

    @After
    public void deleteWorksheet() throws IOException {
        GoogleUrl deleteUrl = new GoogleUrl(worksheetEntry.findEditLink().getHref());
        HttpRequest request = transport.createRequestFactory().buildDeleteRequest(deleteUrl);
        addGoogleHeaders(request);
        request.getHeaders().setIfMatch("*");
        //request.getHeaders().setIfMatch(worksheetEntry.getEtag());

        HttpResponse resp = request.execute(); // no content in return
        Assert.assertTrue(resp.getStatusCode() == 200);
    }

	@Test
	public void editWorksheet() throws IOException {
		GoogleUrl url = new GoogleUrl(worksheetEntry.findEditLink().getHref());
        url.setPrettyPrint(true);
		
		worksheetEntry.setTitle(worksheetEntry.getTitle() + " editted!");
		
		AtomContent requestContent = AtomContent.forEntry(WORKSHEET_NAMESPACE, worksheetEntry);
		HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
        request.setParser(new XmlObjectParser(WORKSHEET_NAMESPACE));
		addGoogleHeaders(request);
		
		System.err.println("trying editting:");
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 200);
		//System.err.println(resp.parseAsString());
		worksheetEntry = resp.parseAs(Entry.class);
		System.err.println("editted worksheet entry: " + worksheetEntry);
	}
	
	@Test
	public void updateCell() throws IOException {
		updateCell(1, 1, "id");
	}
	
	
	@Test
	public void addAndSelectTransactions() throws IOException {
		addOrUpdateHeader(Entry.ID_COL, Entry.NAME_COL, Entry.DATE_COL);
		
		Feed rowFeed = getRowFeed();
		
		Entry first = addTransaction(Entry.createTransaction(1, "first", new Date(1000)), rowFeed);
		Entry second = addTransaction(Entry.createTransaction(2, "второй", new Date(2000)), rowFeed);
		Entry third = addTransaction(Entry.createTransaction(3, "третий", new Date(3000)), rowFeed);
		Entry fourth = addTransaction(Entry.createTransaction(4, "четверный", new Date(4000)), rowFeed);
		
		// select third and second
		GoogleUrl url = new GoogleUrl(worksheetEntry.getContent().getSrc());
        url.setPrettyPrint(true);
		url.set("sq", "id>1 and id <=3");
		url.set("orderby", "id");
		url.set("reverse", "true");
		
		HttpRequest request = createGetRequest(url);
        request.setParser(new XmlObjectParser(ROW_NAMESPACE));
		HttpResponse resp = request.execute();
		
		Assert.assertTrue(resp.getStatusCode() == 200);
		
		Feed transactFeed = resp.parseAs(Feed.class);
		Assert.assertTrue(transactFeed.getEntries().size() == 2);
		Entry firstReturned = transactFeed.getEntries().get(0);
		Assert.assertEquals(firstReturned.getTitle(), third.getTransactionId().toString());
		Assert.assertEquals(firstReturned.getName(), third.getName());
		
		Entry secondReturned = transactFeed.getEntries().get(1);
		Assert.assertEquals(secondReturned.getDate(), second.getDate());
		
//		System.err.println(resp.parseAsString());
	}
	
	@Test
	public void addAndDeleteTransaction() throws IOException {
		addOrUpdateHeader(Entry.ID_COL, Entry.NAME_COL, Entry.DATE_COL);
		
		int id = 1;
		String name = "test transaction";
		Date date = new Date();
		Entry newTransaction = Entry.createTransaction(id, name, date);
		
		Entry added = addTransaction(newTransaction, null);
		
		Assert.assertTrue(id == added.getTransactionId());
		Assert.assertEquals(name, added.getName());
		Assert.assertEquals(date.getTime(), added.getDate().longValue());
		
		// now, delete it
		GoogleUrl url = new GoogleUrl(added.findEditLink().getHref());
        url.setPrettyPrint(true);
		
		HttpRequest request = transport.createRequestFactory().buildDeleteRequest(url);
		addGoogleHeaders(request);
		request.getHeaders().setIfMatch(added.getEtag());
		
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 200);
	}
	
	@Test
	public void addAndEditTransaction() throws IOException {
		addOrUpdateHeader(Entry.ID_COL, Entry.NAME_COL, Entry.DATE_COL);
		
		int id = 1;
		String name = "first transaction1";
		Date date = new Date();
		Entry newTransaction = Entry.createTransaction(id, name, date);
		
		Entry added = addTransaction(newTransaction, null);
		
		Assert.assertTrue(id == added.getTransactionId());
		Assert.assertEquals(name, added.getName());
		Assert.assertEquals(date.getTime(), added.getDate().longValue());
		
		// now, editing
		String newName = added.getName() + " Updated!"; 
		added.setName(newName);
		
		GoogleUrl url = new GoogleUrl(added.findEditLink().getHref());
        url.setPrettyPrint(true);
		
		AtomContent requestContent = AtomContent.forEntry(ROW_NAMESPACE, added);
		HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
		addGoogleHeaders(request);
        request.setParser(new XmlObjectParser(ROW_NAMESPACE));
		
		System.err.println("trying editing transaction");
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 200);
		
		Entry edited = resp.parseAs(Entry.class);
		Assert.assertEquals(edited.getName(), newName);
	}
	
//	@Test TODO: fix 400 bad request
	public void addHeaderCellsInBatch() throws IOException {
		Feed cellFeed = retrieveCellFeed();
		
		String batchUrl = cellFeed.findBatchUrl().getHref();
		String postUrl = cellFeed.findPostUrl().getHref();
		
		
		// getting cell first
		Entry a1 = retrieveCell(postUrl, 1, 1);
		Entry a2 = retrieveCell(postUrl, 1, 2);
		
		// now updating it
		Feed headerRow = new Feed();
		
		a1.getCell().setInputValue("id");
		a1.setBatchId("A1");
		a1.setTitle("A1");
		a1.setContent(null);
		a1.setBatchOp(BatchOperation.UPDATE);
		headerRow.addEntry(a1);
		
		a2.getCell().setInputValue("name");
		a2.setBatchId("B1");
		a2.setTitle("B1");
		a2.setContent(null);
		a2.setBatchOp(BatchOperation.UPDATE);
		headerRow.addEntry(a2);
		
		GoogleUrl url = new GoogleUrl(batchUrl);
		AtomContent requestContent = AtomContent.forFeed(CELL_NAMESPACE, headerRow);
		HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
        request.setParser(new XmlObjectParser(CELL_NAMESPACE));
		addGoogleHeaders(request);
		request.getHeaders().setIfMatch(cellFeed.getEtag());
		
		System.err.println("trying adding:");
		HttpResponse resp = request.execute();
		Assert.assertTrue(resp.getStatusCode() == 200);
		
		System.err.println(resp.parseAsString());
		
		
		/*Feed rowFeed = resp.parseAs(Feed.class);
		Assert.assertNull(rowFeed.getEntries());
		System.err.println(resp.parseAsString());*/
	}

    public Feed retrieveCellFeed() throws IOException {
        GoogleUrl url = new GoogleUrl(worksheetEntry.findCellFeedUrl().getHref());
        url.setPrettyPrint(true);

        HttpRequest request = createGetRequest(url);
        request.setParser(new XmlObjectParser(CELL_NAMESPACE));
        HttpResponse resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

//		System.err.println(resp.parseAsString()); return null;
        Feed cellFeed = resp.parseAs(Feed.class);
        //Assert.assertNull(cellFeed.getEntries());
        return cellFeed;
    }

    public Entry retrieveCell(String postUrl, int row, int col) throws IOException {
        String cellEditUrl = generateCellUrl(postUrl, row, col);
        GoogleUrl url = new GoogleUrl(cellEditUrl);
        url.setPrettyPrint(true);

        HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
        addGoogleHeaders(request);
        request.setParser(new XmlObjectParser(CELL_NAMESPACE));
        System.err.println("trying getting:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Entry cellEntry = resp.parseAs(Entry.class);
        Assert.assertTrue(cellEntry.getCell().getRow() == row);
        Assert.assertTrue(cellEntry.getCell().getCol() == col);
//		System.err.println(cellEntry);

        return cellEntry;
    }
	
	private String generateCellUrl(String postUrl, int row, int col) {
		return String.format(postUrl + "/R%sC%s", row, col);
	}

    public Entry addTransaction(Entry transaction, Feed rowFeed) throws IOException {
        if (rowFeed == null ) {
            rowFeed = getRowFeed();
        }

        GoogleUrl url = new GoogleUrl(rowFeed.findPostUrl().getHref());
        url.setPrettyPrint(true);

        AtomContent requestContent = AtomContent.forEntry(ROW_NAMESPACE, transaction);
        HttpRequest request = transport.createRequestFactory().buildPostRequest(url, requestContent);
        addGoogleHeaders(request);
        request.setParser(new XmlObjectParser(ROW_NAMESPACE));

        System.err.println("trying adding new transaction");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 201);

        Entry added = resp.parseAs(Entry.class);

        Assert.assertEquals(transaction.getName(), added.getName());
        Assert.assertEquals(transaction.getDate(), added.getDate());
        Assert.assertEquals(transaction.getTransactionId(), added.getTransactionId());

        return added;


//		System.err.println(resp.parseAsString());
    }

    public Feed getRowFeed() throws IOException {
        GoogleUrl url = new GoogleUrl(worksheetEntry.getContent().getSrc());
        url.setPrettyPrint(true);

        HttpRequest request = createGetRequest(url);
        request.setParser(new XmlObjectParser(ROW_NAMESPACE));
        HttpResponse resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed rowFeed = resp.parseAs(Feed.class);
        Assert.assertNull(rowFeed.getEntries());
//		System.err.println(resp.parseAsString()); return null;
        return rowFeed;
    }

    public void addOrUpdateHeader(String...names) throws IOException {
        for (int i = 0; i < names.length; i++) {
            String header = names[i];
            updateCell(1, i+1, header);
        }
    }



    private Entry updateCell(int row, int col, String content) throws IOException {
        Feed cellFeed = retrieveCellFeed();
        String postUrl = cellFeed.findPostUrl().getHref();

        // getting cell first
        Entry entry = retrieveCell(postUrl, row, col);
        entry.getCell().setInputValue(content);
        entry.getContent().setValue(content);
        Entry updated = updateEntry(entry);

        Assert.assertEquals(updated.getCell().getInputValue(), content);
        Assert.assertEquals(updated.getContent().getValue(), content);
        Assert.assertEquals(updated.getCell().getRow(), row);
        Assert.assertEquals(updated.getCell().getCol(), col);

        return updated;
    }

    private Entry updateEntry(Entry entry) throws IOException {
        GoogleUrl url = new GoogleUrl(entry.findEditLink().getHref());
        AtomContent requestContent = AtomContent.forEntry(CELL_NAMESPACE, entry);
        HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
        addGoogleHeaders(request);
        request.setParser(new XmlObjectParser(CELL_NAMESPACE));
        request.getHeaders().setIfMatch(entry.getEtag());

        System.err.println("trying updating entry:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);
        return resp.parseAs(Entry.class);
    }


	

	private static HttpRequest createGetRequest(GenericUrl url) throws IOException {
		HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
		
		addGoogleHeaders(request);
		return request;
	}
	
	private static void addGoogleHeaders(HttpRequest request) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAuthorization(authResp.getAuthorizationHeaderValue());
		//headers.setUserAgent("docker android junit test (user-agent)");
		headers.set(GDATA_VERSION_HTTP_HEADER, "3.0");
		request.setHeaders(headers);
	}
	
	

}
