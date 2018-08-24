package com.example.jason.myapplication;


import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.RemoteInput;
import android.util.Log;

import com.example.jason.myapplication.containers.Chat;
import com.example.jason.myapplication.containers.Matches;
import com.google.gson.Gson;

import java.util.ArrayList;

public class NotificationGorilla {

    private static int globalNotificationId = 182736;
    static String TAG = "Notification";
    private static ArrayList<Chat.ChatMessage> recentMessages  = recentMessages = new ArrayList<>();


    public static void messageToNotification(String incomingMessage, Context context){
        Gson gson = new Gson();
        Chat.ChatMessage message = gson.fromJson(incomingMessage, Chat.ChatMessage.class);
        //Log.d(TAG, "Notification Gorilla: Message From is: " + message.from);

        switch(message.message){
            case "/advertisement":
                break;
            case "/leave":
                endChatNotification(context);
                break;
            default:
                recentMessages.add(message);
                sendNotification("Stranger", message.message, globalNotificationId, "USER_MESSAGE", context);
                break;
        }
    }

    public static void sendNotification(String senderName, String textContent, int notificationID, String channelID, Context context) {

        if(Build.VERSION.SDK_INT >= 24){
            Log.d(TAG, "Notification Gorilla: API 24 and above");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            NotificationCompat.MessagingStyle nMes = new NotificationCompat.MessagingStyle("Me");

            for(Chat.ChatMessage s:recentMessages) {
                Log.d(TAG, "Notification Gorilla: Added Message");
                nMes.addMessage(s.message, Long.parseLong(s.timestamp), "Stranger");
            }
            Log.d(TAG, "Notification Gorilla: Done Adding Messages");

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setStyle(nMes);


            // Create pending intent, mention the Activity which needs to be
            //triggered when user clicks on notification(MainActivity.class in this case)

            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);

            //Reply Button
            RemoteInput remoteInput = new RemoteInput.Builder("key_text_reply")
                    .setLabel("Reply")
                    .build();

            Intent intent = new Intent(context, NotificationGorilla.class);


            PendingIntent replyPendingIntent =
                    PendingIntent.getBroadcast(context,
                            notificationID,
                            intent,
                            PendingIntent.FLAG_UPDATE_CURRENT);

            // Create the reply action and add the remote input.
            NotificationCompat.Action action =
                    new NotificationCompat.Action.Builder(R.drawable.ic_send_btn,
                            "ReplyB", replyPendingIntent)
                            .addRemoteInput(remoteInput)
                            .build();

            // Build the notification and add the action.
            mBuilder.addAction(action);


            notificationManager.notify(notificationID,  mBuilder.build());
        }
        else {
            Log.d(TAG, "Notification Gorilla: Below 24");
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelID)
                    .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle(senderName)
                    .setContentText(textContent);
            //Click Notification
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(contentIntent);

            notificationManager.notify(notificationID,  mBuilder.build());
        }





    }


    public static void endChatNotification(Context context){
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "USER_MESSAGE")
                .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("Chat Ended")
                .setContentText("A user has ended the chat");
        notificationManager.notify(23,  mBuilder.build());
    }

    public static void newMatchNotification(String incomingMatch, Context context){
        Gson gson = new Gson();
        Matches.Match match = gson.fromJson(incomingMatch, Matches.Match.class);

        Log.d(TAG, "Notification Gorilla: Match Device ID is: " + match.deviceID);
        Log.d(TAG, "Notification Gorilla: Match WITH ID is: " + match.matchedWith);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, "USER_MESSAGE")
                .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle("New Match!")
                .setContentText("You have a new match");
        notificationManager.notify(Integer.parseInt(match.id),  mBuilder.build());
    }

}
