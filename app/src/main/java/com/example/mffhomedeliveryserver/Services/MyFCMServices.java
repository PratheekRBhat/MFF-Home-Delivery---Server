package com.example.mffhomedeliveryserver.Services;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.mffhomedeliveryserver.Common.Common;
import com.example.mffhomedeliveryserver.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public class MyFCMServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String, String> dataRecv = remoteMessage.getData();
        if (dataRecv != null) {
            if (dataRecv.get(Common.NOTI_TITLE).equals("New Order")){
                //Opening Main Activity rather than Home to set Common.currentServerUser value.
                Intent notificationIntent = new Intent(this, MainActivity.class);

                //Use this Extra to detect if the activity is opened from notification or not.
                notificationIntent.putExtra(Common.IS_OPEN_ACTIVITY_NEW_ORDER, true);

                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        notificationIntent);
            } else {
                Common.showNotification(this, new Random().nextInt(),
                        dataRecv.get(Common.NOTI_TITLE),
                        dataRecv.get(Common.NOTI_CONTENT),
                        null);
            }
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Common.updateToken(this, s);
    }
}
