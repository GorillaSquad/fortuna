package com.example.jason.myapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class Receiver extends FirebaseMessagingService {

    String TAG = "Receiver";




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            String message = remoteMessage.getData().get("message");
            if(message.startsWith("match:")) {
                String match = message.substring("match: ".length());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("MatchFound");
                broadcastIntent.putExtra("match", match);
                sendBroadcast(broadcastIntent);
            }else if(message.startsWith("message:")) {
                String incomingMessage = message.substring("message: ".length());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("IncomingMessage");
                broadcastIntent.putExtra("message", incomingMessage);
                sendBroadcast(broadcastIntent);
            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
