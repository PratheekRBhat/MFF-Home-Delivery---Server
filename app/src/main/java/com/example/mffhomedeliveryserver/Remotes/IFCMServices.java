package com.example.mffhomedeliveryserver.Remotes;

import com.example.mffhomedeliveryserver.Model.FCMResponse;
import com.example.mffhomedeliveryserver.Model.FCMSendData;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMServices {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAArlZs158:APA91bFVyz9QojX3Q1kbtYmHf017yV1omqZd973DQJZ8SzQTAoo6cJ36SJrgEh21fFCNCcuRH-v-heVXzZ9UnMftnh638BSjmUbxhRCL7FSRqczAE1u423KMueiTdtk18b35xl2tQEhQ"
    })
    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);
    }
