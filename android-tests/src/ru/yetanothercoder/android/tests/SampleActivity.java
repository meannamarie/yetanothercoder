package ru.yetanothercoder.android.tests;

import android.accounts.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import ru.yetanothercoder.android.googleapi.spreadsheets.Entry;
import ru.yetanothercoder.android.googleapi.spreadsheets.Feed;

import java.io.IOException;
import java.net.URLEncoder;

public class SampleActivity extends Activity {

    private String TAG = "77777 MY LOG:";

    public static final String FILE_NAME = "financisto-demo.copy";

    public static final String AUTHORIZATION_HTTP_HEADER = "Authorization";
    public static final String GDATA_VERSION_HTTP_HEADER = "GData-Version";
    public static final String CONTENT_LENGTH_HTTP_HEADER = "Content-Length";
    public static final String CONTENT_TYPE_HTTP_HEADER = "Content-Type";

    private static ClientLogin.Response authResp;
    private static ApacheHttpTransport transport;

    private static Entry testSpreadsheetEntry;

    private static final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    private AccountManager accountManager;
    private String authToken;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //auth();

        setContentView(R.layout.main);
    }

    void auth() {
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Log.d(TAG, "accounts size: " + accounts.length);

        for (Account account : accounts) {
            Log.d(TAG, String.format("account: %s, type: %s", account.name, account.type));
        }

        Account myAccount = accounts[0];

        accountManager.getAuthToken(
                myAccount,                     // Account retrieved using getAccountsByType()
                "wise",            // Auth scope - spreadsheets
                new Bundle(),                        // Authenticator-specific options
                this,                           // Your activity
                new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                new Handler(new OnError()));    // Callback called if an error occurs


//        accountManager.getAuthToken(accounts[0], "ah", false, new GetAuthTokenCallback(), null);

        //this.setListAdapter(new ArrayAdapter(this, R.layout.list_item, accounts));
    }

    public void getSpreadsheet() throws IOException {
        Log.d(TAG, "auth and get spreadsheet test");

        transport = new ApacheHttpTransport();

        /*ClientLogin authenticator = new ClientLogin();
        authenticator.authTokenType = "wise";
        authenticator.transport = transport;
        authenticator.username = System.getProperty("user");
        authenticator.password = System.getProperty("pass");

        System.err.println("logining:");
        authResp = authenticator.authenticate();
        System.err.println("auth token:" + authResp.auth);*/

        // getting the sreadsheet
        GoogleUrl sUrl = new GoogleUrl(String.format(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full?title=%s&title-exact=true",
                URLEncoder.encode(FILE_NAME, "UTF-8")));
        sUrl.setPrettyPrint(true);

        Log.d(TAG, "creating request...");
        HttpRequest request = transport.createRequestFactory().buildGetRequest(sUrl);

        Log.d(TAG, "adding headers...");
        addGoogleHeaders(request);

        request.addParser(new AtomParser(SPREADSHEET_NAMESPACE));
        Log.d(TAG, "executing");
        HttpResponse resp = request.execute();
        //Assert.assertTrue(resp.getStatusCode() == 200);
        Log.d(TAG, "resp status code: " + resp.getStatusCode());


        Feed f = resp.parseAs(Feed.class);
        //Assert.assertTrue(f.getEntries().size() == 1);

        testSpreadsheetEntry = f.getEntries().get(0);
        Log.d(TAG, "Feed entry: " + f);
    }



    private void addGoogleHeaders(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("GoogleLogin auth=" + authToken);
        headers.set("", "");
        //headers.setUserAgent("docker android junit test (user-agent)");
        headers.set(GDATA_VERSION_HTTP_HEADER, "3.0");
        request.setHeaders(headers);
    }

    private class OnError implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            return false;
        }
    }

    private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        @Override
        public void run(AccountManagerFuture<Bundle> result) {
            // Get the result of the operation from the AccountManagerFuture.
            Bundle bundle = null;
            try {
                bundle = result.getResult();
            } catch (OperationCanceledException e) {
                Log.d(TAG, e.toString());
            } catch (IOException e) {
                Log.d(TAG, e.toString());
            } catch (AuthenticatorException e) {
                Log.d(TAG, e.toString());
            }

            Intent launch = (Intent) bundle.get(AccountManager.KEY_INTENT);
            if (launch != null) {
                Log.d(TAG, "intent instead of key: " + launch);
                startActivityForResult(launch, 0);
                return;
            }



            // The token is a named value in the bundle. The name of the value
            // is stored in the constant AccountManager.KEY_AUTHTOKEN.
            authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
            Log.d(TAG, "token: " + authToken);

            try {
                getSpreadsheet();
            } catch (IOException e) {
                Log.d(TAG, "spreadsheet failed: ", e);
            }
        }
    }
}
