package com.spazz.shiv.rasousvide.model;

import com.spazz.shiv.rasousvide.model.ShivVideAPI;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by pula on 1/18/15.
 */
public class RestClient {

  private static ShivVideAPI REST_CLIENT;
  private static String API_CALL = "25.48.16.20:5000";

  static {
    setupRestClient();
  }

  private RestClient() {}

  public static ShivVideAPI getAPI() {
    return REST_CLIENT;
  }

  private static void setupRestClient() {
    RestAdapter.Builder builder = new RestAdapter.Builder()
     .setEndpoint(API_CALL)
     .setClient(new OkClient(new OkHttpClient()))
     .setLogLevel(RestAdapter.LogLevel.FULL);

     RestAdapter restAdapter = builder.build();
     REST_CLIENT = restAdapter.create(ShivVideAPI.class);
  }
}
