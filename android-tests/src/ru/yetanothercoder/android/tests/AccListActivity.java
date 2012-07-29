package ru.yetanothercoder.android.tests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import ru.yetanothercoder.android.googleapi.spreadsheets.auth.GoogleAuth;

import java.util.Arrays;

/**
 * @author Mikhail Baturov
 */
public class AccListActivity extends ListActivity {
    private String TAG = "77777:" + AccListActivity.class.getSimpleName();

    protected AccountManager accountManager;
    protected Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "created!");


        setContentView(R.layout.acclist);

        Account[] accounts = getAccounts();

        Log.i(TAG, "accounts: " + Arrays.toString(accounts));

        this.setListAdapter(new ArrayAdapter<Account>(this, R.layout.accview, R.id.acc_item, accounts));
    }

    private Account[] getAccounts() {
        accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");
        return accounts;
//        return new Account[] {new Account("mbaturov", "com.google"), new Account("docker", "com.google")};
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {

        /*AccountManagerFuture<Bundle> result = new GoogleAuth(this, SPREADSHEET_SERVICE).requestAuth(new OnTokenAcquired(), null);
        Log.d(TAG, "CALLED, waiting for the result...");
        Bundle finished;
        try {
            finished = result.getResult();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Log.d(TAG, "TOKEN: " + finished.get authToken);*/

        Account account = (Account)getListView().getItemAtPosition(position);
        Log.i(TAG, "selected account" + account);

        AccountManagerFuture<Bundle> f = accountManager.getAuthToken(
                account,                     // Account retrieved using getAccountsByType()
                GoogleAuth.DOCLIST_SERVICE,                         // Auth scope - spreadsheets
                true,
                new OnTokenAcquired(),          // Callback called when a token is successfully acquired
                null);

        /*Intent intent = new Intent(this, SampleActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);*/
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
                    startActivity(intent);
                } else if (bundle.containsKey(AccountManager.KEY_AUTHTOKEN)) {
                    String authToken = bundle.getString(AccountManager.KEY_AUTHTOKEN);
                    Log.i(TAG, "received token: " + authToken);
                }
            } catch (Exception e) {
                Log.e(TAG, "auth failed", e);
            }
        }
    }
}
