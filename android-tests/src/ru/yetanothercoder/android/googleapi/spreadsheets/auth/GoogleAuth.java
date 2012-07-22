/*
 * Copyright (c) 2012 Denis Solonenko.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */

package ru.yetanothercoder.android.googleapi.spreadsheets.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Mikhail Baturov, 02.07.12 20:57
 */
public class GoogleAuth {
    public static final String TAG = GoogleAuth.class.getSimpleName();
    public static final String SPREADSHEET_SERVICE = "wise";
    public static final String DOCLIST_SERVICE = "writely";

    private final Context context;
    private final String service;

    public GoogleAuth(Context context, String service) {
        this.context = context;
        this.service = service;
    }

    public AccountManagerFuture<Bundle> requestAuth(AccountManagerCallback<Bundle> tokenHandler) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Log.d(TAG, "accounts size: " + accounts.length);

        if (accounts.length > 0) {
            for (Account account : accounts) {
                Log.d(TAG, String.format("account: %s, type: %s", account.name, account.type));
            }

            Account myAccount = accounts[0];

            return accountManager.getAuthToken(
                    myAccount,                     // Account retrieved using getAccountsByType()
                    service,                         // Auth scope - spreadsheets
                    true,
                    tokenHandler,          // Callback called when a token is successfully acquired
                    null);    // Callback called if an error occurs
        } else {
            throw new IllegalStateException();
        }

    }


    private class OnError implements Handler.Callback {

        @Override
        public boolean handleMessage(Message msg) {
            Log.d(TAG, msg.toString());
            return false;
        }
    }

}
