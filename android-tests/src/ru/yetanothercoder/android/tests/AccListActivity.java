package ru.yetanothercoder.android.tests;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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

        this.setListAdapter(new ArrayAdapter(this, R.layout.accview, R.id.acc_item, accounts));
    }

    private Account[] getAccounts() {
        /*accountManager = AccountManager.get(getApplicationContext());
        Account[] accounts = accountManager.getAccountsByType("com.google");*/
        return new Account[] {new Account("mbaturov", "com.google"), new Account("docker", "com.google")};
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {



        Account account = (Account)getListView().getItemAtPosition(position);
        Intent intent = new Intent(this, SampleActivity.class);
        intent.putExtra("account", account);
        startActivity(intent);
    }
}
