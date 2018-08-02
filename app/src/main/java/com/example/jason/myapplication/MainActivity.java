package com.example.jason.myapplication;

import com.example.jason.myapplication.network.Account;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.jason.myapplication.helpers.WebHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private FirebaseAuth mAuth;


    public void testNotificationClick(View v) {
        Random rand = new Random();
        int x = (rand.nextInt(99999) + 1);
        sendNotification("Updated Notification", "This keeps updating! " + x, 234528947,"DEBUG_NOTIFICATION");
    }

    public void testNewNotificationClick(View v) {
        Random rand = new Random();
        int x = (rand.nextInt(99999) + 1);
        sendNotification("New Notification", "This is a new notification! ID: " + x, x,"DEBUG_NOTIFICATION");
    }

    public void startQueue(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Account myAccount = new Account(user.getUid());
        myAccount.joinQueue();
        Log.d("Start Complete", "Search Started");
    }

    public void stopQueue(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Account myAccount = new Account(user.getUid());
        myAccount.leaveQueue();
        Log.d("Stop Complete", "Search Stopped");
    }

    public void sendNotification(String textTitle, String textContent, int notificationID, String channelID){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.common_google_signin_btn_text_dark)
                .setContentTitle(textTitle)
                .setContentText(textContent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationID,  mBuilder.build());
    }

    public void createNotificationChannel(String channelName, String channelDesc, String channelID) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = channelName;
            String description = channelDesc;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        else{
            //TODO Test on lower sdk to make sure this doesn't need to be handled
        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Create all channels, must before sending any notification
        createNotificationChannel("Debug", "Debug notifications for development", "DEBUG_NOTIFICATION");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // LOGIN
        StartUp start = new StartUp(this);
        start.start();

        //END OF LOGIN
    }
}
