package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.xml.XmlObjectParser;
import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import static ru.yetanothercoder.googleapi.tests.SpreadsheetTests.*;

/**
 *
 * @author Mikhail Baturov www.yetanothercoder.ru
 *   @date 24.05.12 16:48
 */
public class FileUploadTests {

    public static final String UPLOAD_URL = "https://docs.google.com/feeds/upload/create-session/default/private/full";

    public static final String SHEET_SERVICE = "wise";
    public static final String DOCLIST_SERVICE = "writely";

    private static ApacheHttpTransport transport;
    //private static ClientLogin.Response authResp;


    public String auth(String serviceId) throws HttpResponseException, IOException {
        // auth
        transport = new ApacheHttpTransport();

        ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = serviceId;
        authenticator.transport = transport;
        authenticator.username = System.getProperty("user");
        authenticator.password = System.getProperty("pass");

        System.err.println("logining:");
        ClientLogin.Response authResp = authenticator.authenticate();
        System.err.println("auth token:" + authResp.auth);
        return authResp.getAuthorizationHeaderValue();
    }



    @Test
    public void uploadIfNotExists() throws IOException {

        // 1. auth >>
        String sheetAuthToken = auth(SHEET_SERVICE);
        System.err.println("sheet auth: " + sheetAuthToken);

        String filename = "financisto-UPLOADED2";

        // 2. checking the spreadsheet
        Feed sheetFeed = getSpreadsheetFeed(filename, sheetAuthToken);
        GoogleHeaders gHeaders;

        boolean sheetExists = sheetFeed.getEntries() != null;

        if (!sheetExists) {
            // 3. upload an empty one >>
            String uploadAuthToken = auth(DOCLIST_SERVICE);
            System.err.println("upload auth: " + uploadAuthToken);

            String cvsSimple = "id,date,author,amount,currency";

            InputStreamContent mediaContent =
                    new InputStreamContent("text/csv",
                            new BufferedInputStream(new ByteArrayInputStream(cvsSimple.getBytes())));
            mediaContent.setLength(cvsSimple.length());

            Entry sheet = new Entry();
            sheet.setTitle(filename);


            AtomContent requestContent = AtomContent.forEntry(SPREADSHEET_NAMESPACE, sheet);
            MediaHttpUploader uploader = new MediaHttpUploader(mediaContent, transport, null);
            uploader.setMetadata(requestContent);


            gHeaders = new GoogleHeaders();
            gHeaders.setGDataVersion("3.0");
            gHeaders.setSlugFromFileName("test.csv");
            gHeaders.setAuthorization(uploadAuthToken);
            gHeaders.setUploadContentType("text/csv");
            uploader.setInitiationHeaders(gHeaders);

            GoogleUrl url = new GoogleUrl(UPLOAD_URL);
            url.setPrettyPrint(true);


            HttpResponse response = uploader.upload(url);
            if (!response.isSuccessStatusCode()) {
                //throw GoogleJsonResponseException(jsonFactory, response);
                throw new RuntimeException(response.getStatusMessage());
            }
            response.disconnect();

            sheetFeed = getSpreadsheetFeed(filename, sheetAuthToken);
        }

        // 4. getting the worksheet feed
        GoogleUrl wUrl = new GoogleUrl(sheetFeed.getEntries().get(0).getContent().getSrc());

        HttpRequest request = transport.createRequestFactory().buildGetRequest(wUrl);
        gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setAuthorization(sheetAuthToken);
        request.setHeaders(gHeaders);

        request.setParser(new XmlObjectParser(WORKSHEET_NAMESPACE));
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed worksheedFeed = resp.parseAs(Feed.class);
        System.err.println(worksheedFeed);
        resp.disconnect();

        Assert.assertNotNull(worksheedFeed.getEntries());
        Entry worksheetEntry = worksheedFeed.getEntries().get(0);

        // 5. editing the worksheet
        worksheetEntry.setTitle("transactions");
        worksheetEntry.setRowCount(77);
        worksheetEntry.setColCount(7);

        GoogleUrl editUrl = new GoogleUrl(worksheetEntry.findEditLink().getHref());
        AtomContent editedContent = AtomContent.forEntry(WORKSHEET_NAMESPACE, worksheetEntry);
        request = transport.createRequestFactory().buildPutRequest(editUrl, editedContent);
        gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setAuthorization(sheetAuthToken);
        request.setHeaders(gHeaders);
        request.setParser(new XmlObjectParser(WORKSHEET_NAMESPACE));

        resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Entry editedEntry = resp.parseAs(Entry.class);
        System.err.println(editedEntry);
        resp.disconnect();

        // 6. get cell feed
        GoogleUrl url = new GoogleUrl(editedEntry.getContent().getSrc());
        url.setPrettyPrint(true);

        request = transport.createRequestFactory().buildGetRequest(url);
        gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setAuthorization(sheetAuthToken);
        request.setHeaders(gHeaders);
        request.setParser(new XmlObjectParser(ROW_NAMESPACE));
        resp = request.execute();

        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed rowFeed = resp.parseAs(Feed.class);
        Assert.assertNull(rowFeed.getEntries());
        System.err.println(rowFeed);
    }

    private Feed getSpreadsheetFeed(String filename, String sheetAuthToken) throws IOException {
        GoogleUrl sUrl = new GoogleUrl(String.format(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full?title=%s&title-exact=true",
                URLEncoder.encode(filename, "UTF-8")));
        sUrl.setPrettyPrint(true);

        HttpRequest request = transport.createRequestFactory().buildGetRequest(sUrl);
        GoogleHeaders gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setAuthorization(sheetAuthToken);
        request.setHeaders(gHeaders);

        request.setParser(new XmlObjectParser(SPREADSHEET_NAMESPACE));
        HttpResponse resp = request.execute();
        Assert.assertTrue(resp.getStatusCode() == 200);

        Feed sheetFeed = resp.parseAs(Feed.class);
        System.err.println(sheetFeed);

        resp.disconnect();
        return sheetFeed;
    }
}
