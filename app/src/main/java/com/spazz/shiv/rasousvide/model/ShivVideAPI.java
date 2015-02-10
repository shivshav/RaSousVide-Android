package com.spazz.shiv.rasousvide.model;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;

/**
 * Created by pula on 1/18/15.
 */
public interface ShivVideAPI {
    @GET("/getstatus")
    void getCurrentPiParams(Callback<ShivVideResponse> callback);

    @POST("postparams/1")
    //TODO: Add object with data to send to Pi
    void postDeliciousParams();
}
