package ru.yetanothercoder.android.tests;

import android.accounts.*;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import junit.framework.Assert;
import ru.yetanothercoder.android.googleapi.spreadsheets.Entry;
import ru.yetanothercoder.android.googleapi.spreadsheets.Feed;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class ru.yetanothercoder.android.tests.SampleActivityTest \
 * ru.yetanothercoder.android.tests.tests/android.test.InstrumentationTestRunner
 */
public class SampleActivityTest extends ActivityInstrumentationTestCase2<SampleActivity> {

    private String TAG = "77777 TEST LOG:";

    public static final String FILE_NAME = "financisto-demo.copy";

    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    public static final String GDATA_VERSION_HTTP_HEADER = "GData-Version";
    public static final String CONTENT_LENGTH_HTTP_HEADER = "Content-Length";
    public static final String CONTENT_TYPE_HTTP_HEADER = "Content-Type";

    private static ApacheHttpTransport transport;

    private static Entry testSpreadsheetEntry;
    private Entry worksheetEntry;

    private final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    private final XmlNamespaceDictionary WORKSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
            .set("gs", "http://schemas.google.com/spreadsheets/2006")
            .set("gsx", "http://schemas.google.com/spreadsheets/2006/extended");

    private final XmlNamespaceDictionary ROW_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("gsx", "http://schemas.google.com/spreadsheets/2006/extended")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    public final XmlNamespaceDictionary CELL_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("batch", "http://schemas.google.com/gdata/batch")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("gs", "http://schemas.google.com/spreadsheets/2006")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    private static String authToken;
    private AccountManager accountManager;

    public SampleActivityTest() {
        super("ru.yetanothercoder.android.tests", SampleActivity.class);
    }

    @Override
    public void setUp() throws Exception{
        super.setUp();

        if (authToken == null) {
            Log.d(TAG, "authentication...");
            authToken = auth();
            Log.i(TAG, "Auth_Token: " + authToken);
        }

        if (testSpreadsheetEntry == null) {
            Log.d(TAG, "getting spreadsheet...");
            testSpreadsheetEntry = getSpreadsheet();
            Log.d(TAG, "Feed entry: " + testSpreadsheetEntry);
        }

        if (worksheetEntry == null) {
            Log.d(TAG, "adding worksheet...");
            worksheetEntry = addWorksheet();
            Log.d(TAG, "Added worksheet: " + worksheetEntry);
        }
    }

    //@Test
    public void testEditWorksheet() throws IOException {
        GoogleUrl url = new GoogleUrl(worksheetEntry.findEditLink().getHref());
        url.setPrettyPrint(true);

        worksheetEntry.setTitle(worksheetEntry.getTitle() + " editted!");

        AtomContent requestContent = AtomContent.forEntry(WORKSHEET_NAMESPACE, worksheetEntry);
        HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
        request.addParser(new AtomParser(WORKSHEET_NAMESPACE));
        addGoogleHeaders(request);

        Log.d(TAG, "trying editting:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);
        //Log.d(TAG, resp.parseAsString());
        worksheetEntry = resp.parseAs(Entry.class);
        Log.d(TAG, "editted worksheet entry: " + worksheetEntry);
    }

    //@Test
    public void testUpdateCell() throws IOException {
        updateCell(1, 1, "id");
    }

//    @Test
    public void testAddAndDeleteTransaction() throws IOException {
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
        url.prettyprint = true;

        HttpRequest request = transport.createRequestFactory().buildDeleteRequest(url);
        addGoogleHeaders(request);
        request.getHeaders().setIfMatch(added.getEtag());

        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);
    }

//    @Test
    public void testAddAndEditTransaction() throws IOException {
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
        url.prettyprint = true;

        AtomContent requestContent = AtomContent.forEntry(ROW_NAMESPACE, added);
        HttpRequest request = transport.createRequestFactory().buildPutRequest(url, requestContent);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(ROW_NAMESPACE));

        Log.d(TAG, "trying editing transaction");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Entry edited = resp.parseAs(Entry.class);
        Assert.assertEquals(edited.getName(), newName);
    }

//    @Test
    public void testAddAndSelectTransactions() throws IOException {
        addOrUpdateHeader(Entry.ID_COL, Entry.NAME_COL, Entry.DATE_COL);

        Feed rowFeed = getRowFeed();

        Entry first = addTransaction(Entry.createTransaction(1, "first", new Date(1000)), rowFeed);
        Entry second = addTransaction(Entry.createTransaction(2, "второй", new Date(2000)), rowFeed);
        Entry third = addTransaction(Entry.createTransaction(3, "третий", new Date(3000)), rowFeed);
        Entry fourth = addTransaction(Entry.createTransaction(4, "четверный", new Date(4000)), rowFeed);

        // select third and second
        GoogleUrl url = new GoogleUrl(worksheetEntry.getContent().getSrc());
        url.prettyprint = true;
        url.set("sq", "id>1 and id <=3");
        url.set("orderby", "id");
        url.set("reverse", "true");

        HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(ROW_NAMESPACE));
        HttpResponse resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed transactFeed = resp.parseAs(Feed.class);
        Assert.assertTrue(transactFeed.getEntries().size() == 2);
        Entry firstReturned = transactFeed.getEntries().get(0);
        Assert.assertEquals(firstReturned.getTitle(), third.getTransactionId().toString());
        Assert.assertEquals(firstReturned.getName(), third.getName());

        Entry secondReturned = transactFeed.getEntries().get(1);
        Assert.assertEquals(secondReturned.getDate(), second.getDate());

//		Log.d(TAG, resp.parseAsString());
    }

    @Override
    public void tearDown() throws Exception {
        if (worksheetEntry != null) {
            Log.d(TAG, "deleting worksheet...");
            deleteWorksheet();
        }

        super.tearDown();
    }

    @Override
    protected void scrubClass(Class<?> testCaseClass) {
        // ignore
    }

    private Entry addTransaction(Entry transaction, Feed rowFeed) throws IOException {
        if (rowFeed == null ) {
            rowFeed = getRowFeed();
        }

        GoogleUrl url = new GoogleUrl(rowFeed.findPostUrl().getHref());
        url.prettyprint = true;

        AtomContent requestContent = AtomContent.forEntry(ROW_NAMESPACE, transaction);
        HttpRequest request = transport.createRequestFactory().buildPostRequest(url, requestContent);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(ROW_NAMESPACE));

        Log.d(TAG, "trying adding new transaction");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 201);

        Entry added = resp.parseAs(Entry.class);

        Assert.assertEquals(transaction.getName(), added.getName());
        Assert.assertEquals(transaction.getDate(), added.getDate());
        Assert.assertEquals(transaction.getTransactionId(), added.getTransactionId());

        return added;
    }

    private Feed getRowFeed() throws IOException {
        GoogleUrl url = new GoogleUrl(worksheetEntry.getContent().getSrc());
        url.prettyprint = true;

        HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(ROW_NAMESPACE));
        HttpResponse resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed rowFeed = resp.parseAs(Feed.class);
        Assert.assertNull(rowFeed.getEntries());
//		Log.d(TAG, resp.parseAsString()); return null;
        return rowFeed;
    }

    private void addOrUpdateHeader(String...names) throws IOException {
        for (int i = 0; i < names.length; i++) {
            String header = names[i];
            updateCell(1, i+1, header);
        }
    }


    private String auth() throws IOException, AuthenticatorException, OperationCanceledException {
        accountManager = AccountManager.get(getActivity().getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Log.d(TAG, "accounts size: " + accounts.length);

        Account myAccount = null;
        String accName = "mbaturov";
        //Assert.assertNotNull("Please specify -Dacc option!", accName);
        for (Account account : accounts) {
            Log.d(TAG, String.format("account: %s, type: %s", account.name, account.type));
            if (account.name.contains(accName)) {
                myAccount = account;
                break;
            }
        }

        Assert.assertNotNull(myAccount);

        AccountManagerFuture<Bundle> result = accountManager.getAuthToken(myAccount, "wise", null, getActivity(), null, null);

        Bundle b = result.getResult();
        return b.getString(AccountManager.KEY_AUTHTOKEN);
    }


    private Entry getSpreadsheet() throws IOException {
        Log.i(TAG, "getting spreadsheet test");

        transport = new ApacheHttpTransport();

        // getting the sreadsheet
        GoogleUrl sUrl = new GoogleUrl(String.format(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full?title=%s&title-exact=true",
                URLEncoder.encode(FILE_NAME, "UTF-8")));
        sUrl.prettyprint = true;

        Log.d(TAG, "creating request...");
        HttpRequest request = transport.createRequestFactory().buildGetRequest(sUrl);

        Log.d(TAG, "adding headers...");
        addGoogleHeaders(request);

        request.addParser(new AtomParser(SPREADSHEET_NAMESPACE));
        Log.d(TAG, "executing");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);
        Log.d(TAG, "resp status code: " + resp.getStatusCode());


        Feed f = resp.parseAs(Feed.class);
        //Assert.assertTrue(f.getEntries().size() == 1);

        return f.getEntries().get(0);
    }

    private Entry addWorksheet() throws IOException {
        // adding new worksheet
        GoogleUrl url = new GoogleUrl(testSpreadsheetEntry.getContent().getSrc());
        url.prettyprint = true;

        String newWorksheetName = "android-" + Math.random();
        Entry newWorksheet = new Entry(newWorksheetName, 7, 17);

        AtomContent requestContent = AtomContent.forEntry(WORKSHEET_NAMESPACE, newWorksheet);
        HttpRequest request = transport.createRequestFactory().buildPostRequest(url, requestContent);
        request.addParser(new AtomParser(WORKSHEET_NAMESPACE));
        addGoogleHeaders(request);

        Log.d(TAG, "trying adding:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 201);
        Entry added = resp.parseAs(Entry.class);
        Log.d(TAG, "added worksheet entry: " + added);
        return added;
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
        request.addParser(new AtomParser(CELL_NAMESPACE));
        request.getHeaders().setIfMatch(entry.getEtag());

        Log.d(TAG, "trying updating entry:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);
        return resp.parseAs(Entry.class);
    }

    private Entry retrieveCell(String postUrl, int row, int col) throws IOException {
        String cellEditUrl = generateCellUrl(postUrl, row, col);
        GoogleUrl url = new GoogleUrl(cellEditUrl);
        url.prettyprint = true;

        HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(CELL_NAMESPACE));
        Log.d(TAG, "trying getting:");
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Entry cellEntry = resp.parseAs(Entry.class);
        Assert.assertTrue(cellEntry.getCell().getRow() == row);
        Assert.assertTrue(cellEntry.getCell().getCol() == col);

        return cellEntry;
    }

    private String generateCellUrl(String postUrl, int row, int col) {
        return String.format(postUrl + "/R%sC%s", row, col);
    }

    private Feed retrieveCellFeed() throws IOException {
        GoogleUrl url = new GoogleUrl(worksheetEntry.findCellFeedUrl().getHref());
        url.prettyprint = true;

        HttpRequest request = transport.createRequestFactory().buildGetRequest(url);
        addGoogleHeaders(request);
        request.addParser(new AtomParser(CELL_NAMESPACE));
        HttpResponse resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

//		Log.d(TAG, resp.parseAsString()); return null;
        Feed cellFeed = resp.parseAs(Feed.class);
        //Assert.assertNull(cellFeed.getEntries());
        return cellFeed;
    }

    private void deleteWorksheet() throws IOException {
        GoogleUrl deleteUrl = new GoogleUrl(worksheetEntry.findEditLink().getHref());
        HttpRequest request = transport.createRequestFactory().buildDeleteRequest(deleteUrl);
        addGoogleHeaders(request);
        request.getHeaders().setIfMatch("*");
        //request.getHeaders().setIfMatch(worksheetEntry.getEtag());

        HttpResponse resp = request.execute(); // no content in return
        Assert.assertTrue(resp.getStatusCode() == 200);
    }

    private void addGoogleHeaders(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("GoogleLogin auth=" + authToken);
        //headers.setUserAgent("docker android junit test (user-agent)");
        headers.set(GDATA_VERSION_HTTP_HEADER, "3.0");
        request.setHeaders(headers);
    }
}
