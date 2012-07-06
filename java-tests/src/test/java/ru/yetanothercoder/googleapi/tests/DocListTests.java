/*
 * *
 *  *
 *  * @author Mikhail Baturov (www.yetanothercoder.ru)
 *
 */

package ru.yetanothercoder.googleapi.tests;

import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import junit.framework.Assert;
import org.apache.http.client.HttpResponseException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * @author Mikhail Baturov, 06.07.12 20:09
 */
public class DocListTests {

    private static ApacheHttpTransport transport;
    private static ClientLogin.Response authResponse;

    @BeforeClass
    public static void authAndGetSpreadsheet() throws HttpResponseException, IOException {
        // auth
        transport = new ApacheHttpTransport();

        ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "writely";
        authenticator.transport = transport;
        authenticator.username = System.getProperty("user");
        authenticator.password = System.getProperty("pass");

        System.err.println("logining:");
        ClientLogin.Response authResp = authenticator.authenticate();
        Assert.assertNotNull(authResp);
        Assert.assertNotNull(authResp.auth);

        System.err.println("auth token:" + authResp.auth);
        authResponse = authResp;
    }


    @Test
    public void testCsvUpload() throws Exception {
        String cvsSimple = "\"REVIEW_DATE\",\"AUTHOR\",\"ISBN\",\"DISCOUNTED_PRICE\"\n" +
                "\"1985/01/21\",\"Douglas Adams\",0345391802,5.95\n" +
                "\"1990/01/12\",\"Douglas Hofstadter\",0465026567,9.95\n" +
                "\"1998/07/15\",\"Timothy \"\"The Parser\"\" Campbell\",0968411304,18.99\n" +
                "\"1999/12/03\",\"Richard Friedman\",0060630353,5.95\n" +
                "\"2001/09/19\",\"Karen Armstrong\",0345384563,9.95\n" +
                "\"2002/06/23\",\"David Jones\",0198504691,9.95\n" +
                "\"2002/06/23\",\"Julian Jaynes\",0618057072,12.50\n" +
                "\"2003/09/30\",\"Scott Adams\",0740721909,4.95\n" +
                "\"2004/10/04\",\"Benjamin Radcliff\",0804818088,4.95\n" +
                "\"2004/10/04\",\"Randel Helms\",0879755725,4.50";

        InputStreamContent mediaContent =
                new InputStreamContent("text/csv",
                        new BufferedInputStream(new ByteArrayInputStream(cvsSimple.getBytes())));
        mediaContent.setLength(cvsSimple.length());


        MediaHttpUploader uploader = new MediaHttpUploader(mediaContent, transport, new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setInterceptor(new HttpExecuteInterceptor() {
                    @Override
                    public void intercept(HttpRequest request) throws IOException {
                        request.getHeaders().setAuthorization(authResponse.getAuthorizationHeaderValue());
                        request.getHeaders().set("GData-Version", "3.0");
                    }
                });
            }
        });

        uploader.setProgressListener(new CustomProgressListener());
        GoogleUrl url = new GoogleUrl("https://docs.google.com/feeds/upload/create-session/default/private/full");
        url.setPrettyPrint(true);


        HttpResponse response = uploader.upload(url);
        if (!response.isSuccessStatusCode()) {
            throw new RuntimeException("failed!");
        }
    }

    class CustomProgressListener implements MediaHttpUploaderProgressListener {
        public void progressChanged(MediaHttpUploader uploader) throws IOException {
            switch (uploader.getUploadState()) {
                case INITIATION_STARTED:
                    System.err.println("Initiation has started!");
                    break;
                case INITIATION_COMPLETE:
                    System.err.println("Initiation is complete!");
                    break;
                case MEDIA_IN_PROGRESS:
                    System.err.println(uploader.getProgress());
                    break;
                case MEDIA_COMPLETE:
                    System.err.println("Upload is complete!");
            }
        }
    }

}
