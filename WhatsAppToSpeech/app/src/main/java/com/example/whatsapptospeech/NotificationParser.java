package com.example.whatsapptospeech;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
public class NotificationParser extends NotificationListenerService {
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Log.d("ME", "Notification posted");
        String opPackage = sbn.getOpPkg();
        Log.d("ME", "Notification from package: " + opPackage);
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        Intent intent = new Intent(opPackage);
        intent.putExtra("Title ", title);
        intent.putExtra("Text ", text);
        sendBroadcast(intent);
    }

    @Override
    public void onListenerConnected(){
        Log.d("ME", "Listener enabled and conneceted to notification manager");
    }
}
