package com.spazz.shiv.rasousvide.rest;

import android.nfc.Tag;
import android.util.Log;

import com.spazz.shiv.rasousvide.rest.model.ShivVideAPI;

import retrofit.RestAdapter;

/**
 * Created by pula on 1/18/15.
 */
public class RestClient {

    private static final String TAG = RestClient.class.getSimpleName();

    private static ShivVideAPI REST_CLIENT;
    private static String API_CALL = "http://192.168.1.201";

    private RestClient() {}

    public static ShivVideAPI getAPI() {

        if (REST_CLIENT == null) {
            initAPI();
        }

        return REST_CLIENT;
    }

    public static void initAPI(String endpoint) {

        if (endpoint == null) {
            endpoint = API_CALL;
        }
        Log.e(TAG, "Called initApi with endpoint=" + endpoint);
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(endpoint)
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter = builder.build();
        REST_CLIENT = restAdapter.create(ShivVideAPI.class);
    }

    public static void initAPI() {
        initAPI(API_CALL);
    }

    public static String getApiEndpoint() {
        return API_CALL;
    }

    public static void setApiEndpoint(String endpoint) {
        API_CALL = endpoint;
    }


}
