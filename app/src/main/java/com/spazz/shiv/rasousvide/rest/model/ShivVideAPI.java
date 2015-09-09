package com.spazz.shiv.rasousvide.rest.model;


import butterknife.Bind;
import retrofit.http.Body;
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
    Observable<String> postDeliciousParams(
            @Field("mode") String mode,
            @Field("setpoint") double setpoint,
            @Field("k") double kParameter,
            @Field("i") double iParameter,
            @Field("d") double dParameter,
            @Field("dutycycle") double dutyCycle,
            @Field("cycletime") double cycleTime,
            @Field("boilManageTemp") double boilManageTemp,
            @Field("numPntsSmooth") int numSmoothPoint);

    @FormUrlEncoded
    @POST("/")
        //TODO: Add object with data to send to Pi
    Observable<String> postOldAPIParams(
            @Field("mode") String mode,
            @Field("setpoint") double setpoint,
            @Field("k") double kParameter,
            @Field("i") double iParameter,
            @Field("d") double dParameter,
            @Field("dutycycle") double dutyCycle,
            @Field("cycletime") double cycleTime,
            @Field("boilManageTemp") double boilManageTemp,
            @Field("numPntsSmooth") int numSmoothPoint);

}
