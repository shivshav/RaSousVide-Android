package com.spazz.shiv.rasousvide.rest.model;


import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by pula on 1/18/15.
 */
public interface ShivVideAPI {
    @GET("/getstatus")
    Observable<ShivVideResponse> getCurrentPiParams();

    @POST("postparams/1")
    //TODO: Add object with data to send to Pi
    void postDeliciousParams(ShivVidePost post);
}
