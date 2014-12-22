package ru.yetanothercoder.android.tests;

import android.accounts.*;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.google.api.client.googleapis.auth.clientlogin.ClientLogin;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.xml.atom.AtomContent;
import com.google.api.client.http.xml.atom.AtomFeedParser;
import com.google.api.client.xml.XmlNamespaceDictionary;
import org.xmlpull.v1.XmlPullParserException;
import ru.yetanothercoder.android.googleapi.spreadsheets.Content;
import ru.yetanothercoder.android.googleapi.spreadsheets.Entry;
import ru.yetanothercoder.android.googleapi.spreadsheets.Feed;
import ru.yetanothercoder.android.googleapi.spreadsheets.MySheetEntry;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Date;

public class SampleActivity extends Activity {

    private String TAG = "77777 MY LOG:";

//    public static final String FILE_NAME = "financisto-demo.copy";
    public static final String FILE_NAME = "my";

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
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/")
            .set("xmlns:gsx", "http://schemas.google.com/spreadsheets/2006/extended");

    private AccountManager accountManager;
    private String authToken = "DQAAAOwAAAC1HDiaTCQYEt0mwBniuC-ST3gIwPzNxpjue0sviCoxFJwHj0oVwRUuZRhgPeIjmfnFXXEeCh6TIOJtNzc30DDaVpHUk6ZapIsOQt7b_CXPZbPHX26pxW8rGz6Bs39gPtsNxI6UOja5DFBf-DkF_g0bGk0jX3q0o0TjCLGTPXL1TddDu9vR383pYpBYGeiZVpkwoWQskFm3JtJ73Y8hX2YX69rpfPnliM0m4nbOMW7ItUpQ1JPpVUZDn4XSiwnb8x_-NPe-lunEfTqvu5NH9QjwVZYYMLpeosF34P4h-tOMX2rkxtEb5GeEtPvBm79PfU0";


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (authToken == null) {
            auth();
        } else {
            try {
                processSpreadsheet();
            } catch (Exception e) {
                Log.d(TAG, "spreadsheet failed: ", e);
            }
        }

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

    public void processSpreadsheet() throws IOException, XmlPullParserException {
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
        GenericUrl sUrl = new GenericUrl(String.format(
                "https://spreadsheets.google.com/feeds/spreadsheets/private/full?title=%s&title-exact=true",
                URLEncoder.encode(FILE_NAME, "UTF-8")));
//        sUrl.setPrettyPrint(true);

        Log.d(TAG, "creating request...");
        HttpRequest request = transport.createRequestFactory().buildGetRequest(sUrl);

        Log.d(TAG, "adding headers...");
        addGoogleHeaders(request);

//        request.setParser(AtomFeedParser.create()<Feed, Entry>(SPREADSHEET_NAMESPACE));
        Log.d(TAG, "executing");
        HttpResponse resp = null;
        try {
            resp = request.execute();
        } catch (IOException e) {
            Toast.makeText(SampleActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "request failed:", e);
            return;
        }
        //Assert.assertTrue(resp.getStatusCode() == 200);
        Log.d(TAG, "resp status code: " + resp.getStatusCode());


        AtomFeedParser<Feed, Entry> parser = AtomFeedParser.create(resp, SPREADSHEET_NAMESPACE, Feed.class, Entry.class);

        Entry sheet = parser.parseNextEntry();
        Log.d(TAG, "Feed entry: " + sheet);

        // ******************* getting content feed >>
        String contentFeedUrl = sheet.getContent().getSrc();
        request = transport.createRequestFactory().buildGetRequest(new GenericUrl(contentFeedUrl));
        addGoogleHeaders(request);
        Log.d(TAG, "executing");
        try {
            resp = request.execute();
        } catch (IOException e) {
            Toast.makeText(SampleActivity.this, "Request failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.d(TAG, "request failed:", e);
            return;
        }
        //Assert.assertTrue(resp.getStatusCode() == 200);
        Log.d(TAG, "resp status code: " + resp.getStatusCode());

        parser = AtomFeedParser.create(resp, SPREADSHEET_NAMESPACE, Feed.class, Entry.class);

        Entry contentFeed = parser.parseNextEntry();
        Log.d(TAG, "content entry: " + contentFeed);



        // ******************* getting CELLS feed >>
        String cellsFeedUrl = contentFeed.findCellFeedUrl().getHref();
        request = transport.createRequestFactory().buildGetRequest(new GenericUrl(cellsFeedUrl));
        resp = sendRequest(request);

        parser = AtomFeedParser.create(resp, SPREADSHEET_NAMESPACE, Feed.class, Entry.class);

        Entry cell = parser.parseNextEntry();
        log(cell);


        // ******************* EDITing the cell >>
        Content cellContents = cell.getContent();
        String newValue = "Колонка была подменена! в " + new Date();
        cellContents.setValue(newValue);
        cell.getCell().setInputValue(newValue);
        cell.getCell().setValue(newValue);
        String editCellUrl = cell.findEditLink().getHref();

        HttpContent cellContent = AtomContent.forEntry(SPREADSHEET_NAMESPACE, cell);
        request = transport.createRequestFactory().buildPutRequest(new GenericUrl(editCellUrl), cellContent);
        resp = sendRequest(request);

//        log(resp.parseAsString());

        AtomFeedParser<Entry, Entry> editParser = AtomFeedParser.create(resp, SPREADSHEET_NAMESPACE, Entry.class, Entry.class);

        Entry editAns = editParser.parseNextEntry();
        log(editAns);


        // ************************ ADDing row >>
        MySheetEntry newRow = new MySheetEntry();
        newRow.setCol1("31");
        newRow.setCol1("32");
        newRow.setCol1("33");

        HttpContent rowContent = AtomContent.forEntry(SPREADSHEET_NAMESPACE, newRow);
        request = transport.createRequestFactory().buildPostRequest(new GenericUrl(contentFeedUrl), rowContent);
        resp = sendRequest(request);

        AtomFeedParser<Entry, Entry> rowParser = AtomFeedParser.create(resp, SPREADSHEET_NAMESPACE, Entry.class, Entry.class);

        Entry rowAns = editParser.parseNextEntry();
        log(rowAns);

    }

    private void log(Object editAns) {
        Log.d(TAG, "cell entry: " + editAns);
    }

    private HttpResponse sendRequest(HttpRequest request) {
        Log.d(TAG, "sending request...");
        try {
            addGoogleHeaders(request);
            HttpResponse resp = request.execute();
            Log.d(TAG, "resp status code: " + resp.getStatusCode());
            //Assert.assertTrue(resp.getStatusCode() == 200);
            return resp;
        } catch (IOException e) {
            Toast.makeText(SampleActivity.this, String.format("Request `%s` failed: %s", request, e.getMessage()), Toast.LENGTH_LONG).show();
            Log.d(TAG, "request failed:", e);
            throw new RuntimeException(e);
        }
    }

    private void addGoogleHeaders(HttpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAuthorization("GoogleLogin auth=" + authToken);
//        headers.set("", "");
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

            if (bundle == null) {
                Log.d(TAG, "null bundle!");
                return;
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
                processSpreadsheet();
            } catch (Exception e) {
                Log.d(TAG, "spreadsheet failed: ", e);
            }
        }
    }
}
