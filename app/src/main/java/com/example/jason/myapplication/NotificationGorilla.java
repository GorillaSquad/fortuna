package com.example.jason.myapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.jason.myapplication.containers.Chat;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class NotificationGorilla {



    private Map<String, Integer> notificationIDMapping = new HashMap<>();
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    String TAG = "Notification";

    public void messageToNotification(String incomingMessage, Context context){

        Gson gson = new Gson();
        Chat.ChatMessage message = gson.fromJson(incomingMessage, Chat.ChatMessage.class);
        int idToAssign;
        if(!notificationIDMapping.containsKey(message.from)){
            do {
                Random random = new Random();
                idToAssign = random.nextInt();
            }while(notificationIDMapping.containsValue(idToAssign));
            notificationIDMapping.put(message.from, idToAssign);
        }
        sendNotification(message.from.substring(0,3), message.message, notificationIDMapping.get(message.from), "USER_MESSAGE", context);
    }


    private CharSequence getMessageText(Intent intent) {
        if (Build.VERSION.SDK_INT >= 20) {

            Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
            if (remoteInput != null) {
                Log.d(TAG, "" + remoteInput.getCharSequence(KEY_TEXT_REPLY));
                return remoteInput.getCharSequence(KEY_TEXT_REPLY);
            }
        }
        return null;
    }

    public void sendNotification(String textTitle, String textContent, int notificationID, String channelID, Context context) {


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);



        //This is the reply button (only works on apu level 20 or higher)
        if (Build.VERSION.SDK_INT >= 20) {
            RemoteInput remoteInput = new RemoteInput.Builder(KEY_TEXT_REPLY)
                    .setLabel("REPLY")
                    .build();
            /*
            PendingIntent replyPendingIntent =
                    PendingIntent.getBroadcast(context,
                            conversation.getConversationId(),
                            getMessageReplyIntent(conversation.getConversationId()),
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Create the reply action and add the remote input.
            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.ic_send_btn,
                            "REPLY2", replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build();
        */
        }


        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);





        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID,  mBuilder.build());
    }
}
