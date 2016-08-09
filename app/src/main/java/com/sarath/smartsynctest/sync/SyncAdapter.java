package com.sarath.smartsynctest.sync;

/**
 * Created by sarath on 3/8/16.
 */

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.salesforce.androidsdk.smartstore.store.DBHelper;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;
import com.salesforce.androidsdk.smartsync.util.SyncState;
import com.sarath.smartsynctest.StoreUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Handle the transfer of data between a server and an
 * app, using the Android sync adapter framework.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {



    public SyncAdapter(Context context, boolean autoInitialize,
                           boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(getClass().getName(),"onPerformSync");

        if(extras.getBoolean("CLEAR_CACHE",false)){
            DBHelper.getInstance(StoreUtils.getSmartStore().getDatabase()).clearMemoryCache();
        }else {
            StoreUtils.clearSoup();
            try {
                StoreUtils.getSmartStore().upsert(SyncState.SYNCS_SOUP,new JSONObject());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            StoreUtils.insert();
        }
    }
}
