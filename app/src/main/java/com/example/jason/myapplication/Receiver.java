package com.example.jason.myapplication;

import android.content.Intent;
import android.util.Log;

import com.example.jason.myapplication.containers.Chat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;

public class Receiver extends FirebaseMessagingService {

    String TAG = "Receiver";

    public Receiver() {
        super();
        Log.d(TAG, "Notification Gorilla: Reset arraylist");
    }

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
                NotificationGorilla.newMatchNotification(match, this);
            }else if(message.startsWith("message:")) {
                String incomingMessage = message.substring("message: ".length());
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction("IncomingMessage");
                broadcastIntent.putExtra("message", incomingMessage);
                sendBroadcast(broadcastIntent);
                NotificationGorilla.messageToNotification(incomingMessage, this);
            }


        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }
}
