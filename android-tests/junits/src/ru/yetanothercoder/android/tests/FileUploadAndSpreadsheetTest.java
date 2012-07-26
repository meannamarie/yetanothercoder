package ru.yetanothercoder.android.tests;

import android.accounts.*;
import android.content.Intent;
import android.os.Bundle;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import com.google.api.client.xml.XmlNamespaceDictionary;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class FileUploadAndSpreadsheetTest extends ActivityInstrumentationTestCase2<SampleActivity> {

    private String TAG = "77777:" + FileUploadAndSpreadsheetTest.class.getSimpleName();

    private static String SPREADSHEET_SERVICE = "wise";

    private static final XmlNamespaceDictionary SPREADSHEET_NAMESPACE = new XmlNamespaceDictionary()
            .set("", "http://www.w3.org/2005/Atom")
            .set("gd", "http://schemas.google.com/g/2005")
            .set("openSearch", "http://a9.com/-/spec/opensearch/1.1/");

    private AccountManager accountManager;
    private String authToken;

    public FileUploadAndSpreadsheetTest() {
        super("ru.yetanothercoder.android.tests", SampleActivity.class);
    }

    public void testSpreadsheetAuth() throws IOException, AuthenticatorException, OperationCanceledException, InterruptedException {
        // ex. http://blog.notdot.net/2010/05/Authenticating-against-App-Engine-from-an-Android-app
        Log.d(TAG, "BEFORE >>");
        getActivity().startActivity(new Intent(this.getActivity(), AccListActivity.class));

        TimeUnit.SECONDS.sleep(5);
        /*AccountManagerFuture<Bundle> result = new GoogleAuth(getActivity(), SPREADSHEET_SERVICE).requestAuth(new OnTokenAcquired());
        Log.d(TAG, "CALLED, waiting for the result...");
        Bundle finished = result.getResult();
        Log.d(TAG, "TOKEN: " + authToken);*/
    }





    class OnTokenAcquired implements AccountManagerCallback<Bundle> {
        private String TAG = OnTokenAcquired.class.getSimpleName();

        @Override
        public void run(AccountManagerFuture<Bundle> future) {
            try {
                Bundle bundle = future.getResult();
                if (bundle.containsKey(AccountManager.KEY_INTENT)) {
                    Intent intent = bundle.getParcelable(AccountManager.KEY_INTENT);
                    intent.setFlags(intent.getFlags() & ~Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivityForResult(intent, 0); // TODO:  private static final int REQUEST_AUTHENTICATE = 0;
                } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                    authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    Log.d(TAG, "received token: " + authToken);
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
}
