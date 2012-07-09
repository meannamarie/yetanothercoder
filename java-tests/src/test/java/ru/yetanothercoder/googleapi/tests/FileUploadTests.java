package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.XmlObjectParser;
import junit.framework.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

/**
 *
 * @author Mikhail Baturov www.yetanothercoder.ru
 *   @date 24.05.12 16:48
 */
public class FileUploadTests {

    public static final String INIT_URL = "https://docs.google.com/feeds/upload/create-session/default/private/full";

    public static final String SHEET_SERVICE = "wise";
    public static final String DOCLIST_SERVICE = "writely";

    private static final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

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
    public void fileUpload() throws IOException {
        String sheetAuthToken = auth(SHEET_SERVICE);
        System.err.println("sheet auth: " + sheetAuthToken);

        String filename = "financisto-UPLOADED";

        // getting the sreadsheet
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

        boolean sheetExists = sheetFeed.getEntries() != null;

        // now upload >>
        String uploadAuthToken = auth(DOCLIST_SERVICE);
        System.err.println("upload auth: " + uploadAuthToken);


        File mediaFile = new File("c:\\tmp\\test.csv");
        InputStreamContent mediaContent =
                new InputStreamContent("text/csv",
                        new BufferedInputStream(new FileInputStream(mediaFile)));
        mediaContent.setLength(mediaFile.length());

        Entry sheet = new Entry();
        sheet.setTitle(filename);
        String eTag = null;
        if (sheetExists) {
            eTag = sheetFeed.getEntries().get(0).getEtag();
            sheet.setEtag(eTag);
        }

        AtomContent requestContent = AtomContent.forEntry(SPREADSHEET_NAMESPACE, sheet);
        MediaHttpUploader uploader = new MediaHttpUploader(mediaContent, transport, null);
        uploader.setMetadata(requestContent);


        gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setSlugFromFileName("test.csv");
        gHeaders.setAuthorization(uploadAuthToken);
        gHeaders.setUploadContentType("text/csv");
        if (sheetExists) {
            uploader.setInitiationMethod(HttpMethod.PUT);
            //gHeaders.setETag(eTag);
            request.getHeaders().setIfMatch(eTag);
        }
        uploader.setInitiationHeaders(gHeaders);

        GoogleUrl url = new GoogleUrl(INIT_URL);
        url.setPrettyPrint(true);


        HttpResponse response = uploader.upload(url);
        if (!response.isSuccessStatusCode()) {
            //throw GoogleJsonResponseException(jsonFactory, response);
            throw new RuntimeException(response.getStatusMessage());
        }
    }
}
