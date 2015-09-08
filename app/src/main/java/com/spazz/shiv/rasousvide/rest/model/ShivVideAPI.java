package com.spazz.shiv.rasousvide.rest.model;


import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import rx.Observable;

/**
 * Created by pula on 1/18/15.
 */
public interface ShivVideAPI {
    @GET("/getstatus")
    Observable<ShivVideResponse> getCurrentPiParams();

    @FormUrlEncoded
    @POST("postparams/1")
    //TODO: Add object with data to send to Pi
    void postDeliciousParams(
            @Field("mode") String mode,
            @Field("setpoint") float setpoint,
            @Field("k") float kParameter,
            @Field("i") float iParameter,
            @Field("d") float dParameter,
            @Field("dutycycle") float dutyCycle,
            @Field("cycletime") float cycleTime,
            @Field("boilManageTemp") float boilManageTemp,
            @Field("numPntsSmooth") int numSmoothPoint);
}
