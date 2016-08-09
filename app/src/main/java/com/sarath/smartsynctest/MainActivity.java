/*
 * Copyright (c) 2012-2016, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.sarath.smartsynctest;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;
import com.sarath.smartsynctest.sync.SyncAdapter;
import com.sarath.smartsynctest.sync.SyncService;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

    private ProgressDialog progressDialog;




    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(getClass().getName(),"onReceive" );
            Bundle bundle = intent.getExtras();
            if(progressDialog==null) {
                progressDialog = new ProgressDialog(context);
                progressDialog.setTitle("Please wait");
            }

            if(bundle.getBoolean(Constants.INSERTING,true)) {
                if (!progressDialog.isShowing())
                    progressDialog.show();
                Integer table = bundle.getInt(Constants.TABLE,0);
                progressDialog.setMessage(String.format(Locale.ENGLISH,"Values are inserting %d/8",table));
            }else {
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver,new IntentFilter(Constants.DATABASE_INTENT_ACTION));
    }

    @Override
    public void onResume(RestClient client) {
        // Show everything
        findViewById(R.id.root).setVisibility(View.VISIBLE);
    }

    /**
     * Called when "Logout" button is clicked.
     *
     * @param v
     */
    public void onLogoutClick(View v) {
        SalesforceSDKManager.getInstance().logout(this);
    }

    /**
     * Called when "Fetch Contacts" button is clicked
     *
     * @param v
     * @throws UnsupportedEncodingException
     */
    public void onFetchContactsClick(View v) throws UnsupportedEncodingException {
        reset();
    }

    private void reset() {
        StoreUtils.dropSoups();
        Bundle settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        settingsBundle.putBoolean("CLEAR_CACHE", true);
        ContentResolver.requestSync(StoreUtils.getAccount(), StoreUtils.AUTHORITY, settingsBundle);


        StoreUtils.registerSoups();
        settingsBundle = new Bundle();
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(StoreUtils.getAccount(), StoreUtils.AUTHORITY, settingsBundle);
    }


}
