package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.apache.ApacheHttpTransport;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Mikhail Baturov www.yetanothercoder.ru
 *   @date 24.05.12 16:48
 */
public class FileUploadTests {

    public static final String INIT_URL = "https://docs.google.com/feeds/upload/create-session/default/private/full";
    public static final String GDATA_VERSION_HTTP_HEADER = "GData-Version";

    private static ApacheHttpTransport transport;
    private static ClientLogin.Response authResp;


    @BeforeClass
    public static void auth() throws HttpResponseException, IOException {
        // auth
        transport = new ApacheHttpTransport();

        ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "writely"; // Documents List Data API token type
        authenticator.transport = transport;
        authenticator.username = System.getProperty("user");
        authenticator.password = System.getProperty("pass");

        System.err.println("logining:");
        authResp = authenticator.authenticate();
        System.err.println("auth token:" + authResp.auth);
    }

    @Test
    public void fileUpload() throws IOException {
        File mediaFile = new File("c:\\tmp\\settings.xml");
        InputStreamContent mediaContent =
                new InputStreamContent("text/xml",
                        new BufferedInputStream(new FileInputStream(mediaFile)));
        mediaContent.setLength(mediaFile.length());


        GoogleHeaders gHeaders = new GoogleHeaders();
        gHeaders.setGDataVersion("3.0");
        gHeaders.setSlug("MyUploadedFile");
        gHeaders.setSlugFromFileName("settings.xml");
        gHeaders.setAuthorization(authResp.getAuthorizationHeaderValue());
        gHeaders.setUploadContentType("");

        MediaHttpUploader uploader = new MediaHttpUploader(mediaContent, transport, null);
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
