package com.sarath.smartsynctest;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.salesforce.androidsdk.smartstore.store.IndexSpec;
import com.salesforce.androidsdk.smartstore.store.SmartStore;
import com.salesforce.androidsdk.smartsync.app.SmartSyncSDKManager;
import com.salesforce.androidsdk.smartsync.manager.SyncManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;
import java.util.Random;

/**
 * Created by sarath on 3/8/16.
 */
public class StoreUtils {
    private static Context mContext = null;
    public static final String AUTHORITY = "com.sarath.smartsynctest.provider";

    public static void init(Context context){
        mContext = context;
    }

    public static SmartStore getSmartStore(){
        return SmartSyncSDKManager.getInstance().getSmartStore();
    }

    public static void dropSoups(){
        getSmartStore().dropAllSoups();
        Log.d(StoreUtils.class.getName(), "Soups Dropped");
    }

    public static Account getAccount(){
        return SmartSyncSDKManager.getInstance().getUserAccountManager().getCurrentAccount();
    }

    public static void registerSoups(){
        getSmartStore().registerSoup("MySoup1",getIndexSpecs());
        getSmartStore().registerSoup("MySoup2",getIndexSpecs());
        getSmartStore().registerSoup("MySoup3",getIndexSpecs());
        getSmartStore().registerSoup("MySoup4",getIndexSpecs());
        getSmartStore().registerSoup("MySoup5",getIndexSpecs());
        getSmartStore().registerSoup("MySoup6",getIndexSpecs());
        getSmartStore().registerSoup("MySoup7",getIndexSpecs());
        getSmartStore().registerSoup("MySoup8",getIndexSpecs());

        Log.d(StoreUtils.class.getName(), "Soups Registred");


    }

    public static void clearSoup(){
        getSmartStore().clearSoup("MySoup1");
        getSmartStore().clearSoup("MySoup2");
        getSmartStore().clearSoup("MySoup3");
        getSmartStore().clearSoup("MySoup4");
        getSmartStore().clearSoup("MySoup5");
        getSmartStore().clearSoup("MySoup6");
        getSmartStore().clearSoup("MySoup7");
        getSmartStore().clearSoup("MySoup8");
        Log.d(StoreUtils.class.getName(), "Soups Cleared");
    }

    public static void insert(){
        sendStartIntent();
        for(int i=1;i<=8;i++) {
            sendInsertBeginIntent(i);
            for (int j = 0; j < 200; j++) {
                try {
                    getSmartStore().create(String.format(Locale.ENGLISH, "MySoup%d", i), makeJsonObject());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            sendEndIntent();
        }
        Log.d(StoreUtils.class.getName(), "Values Inserted");
    }
    private static void sendStartIntent(){
        sendBroadcast(getDatabaseIntent()
                .putExtra(Constants.INSERTING,true));
    }

    private static void sendInsertBeginIntent(int table){
        sendBroadcast(getDatabaseIntent()
                .putExtra(Constants.INSERTING,true)
                .putExtra(Constants.TABLE,table));
    }

    private static void sendEndIntent(){
        sendBroadcast(getDatabaseIntent()
                .putExtra(Constants.INSERTING,false));
    }

    private static Intent getDatabaseIntent(){
        return new Intent(Constants.DATABASE_INTENT_ACTION);
    }

    private static void sendBroadcast(Intent intent){
        if(mContext!=null)
            mContext.sendBroadcast(intent);
    }

    private static JSONObject makeJsonObject() throws JSONException {
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("Id",getAlphaNumericString());
        jsonObject.put("Name",getAlphaString());
        jsonObject.put(SyncManager.LOCAL,true);
        jsonObject.put(SyncManager.LOCALLY_CREATED,true);
        jsonObject.put(SyncManager.LOCALLY_DELETED,false);
        jsonObject.put(SyncManager.LOCALLY_UPDATED,false);
        return jsonObject;
    }

    private static String alphabet="AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz";
    private static String alphaNumeric="AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";


    private static String getAlphaString(){
        return getRandomAlphaString(15,false);
    }

    private static String getAlphaNumericString(){
        return getRandomAlphaString(15,true);
    }

    private static String getRandomAlphaString(int size, boolean isAlphaNumeric){
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random(72475259075274L);
        for(int i=0;i<size;i++)
            if(isAlphaNumeric)
                stringBuilder.append(alphaNumeric.charAt(random.nextInt(62)));
            else
                stringBuilder.append(alphabet.charAt(random.nextInt(52)));
        return stringBuilder.toString();
    }


    private static IndexSpec[] getIndexSpecs(){
        return  new IndexSpec[]{
                new IndexSpec("Id", SmartStore.Type.string),
                new IndexSpec("Name", SmartStore.Type.string),
                new IndexSpec(SyncManager.LOCAL, SmartStore.Type.string),
                new IndexSpec(SyncManager.LOCALLY_CREATED, SmartStore.Type.string),
                new IndexSpec(SyncManager.LOCALLY_DELETED, SmartStore.Type.string),
                new IndexSpec(SyncManager.LOCALLY_UPDATED, SmartStore.Type.string)
        };
    }
}
