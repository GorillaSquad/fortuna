package com.example.jason.myapplication;

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


    public void testNotificationClick(View v){
        Random rand = new Random();
        int x = (rand.nextInt(99999) + 1);
        sendNotification("Updated Notification", "This keeps updating! " + x, 234528947,"DEBUG_NOTIFICATION");
    }

    public void testNewNotificationClick(View v){
        Random rand = new Random();
        int x = (rand.nextInt(99999) + 1);
        sendNotification("New Notification", "This is a new notification! ID: " + x, x,"DEBUG_NOTIFICATION");
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
        mAuth = FirebaseAuth.getInstance();

        Log.d(TAG,  FirebaseInstanceId.getInstance().getId());
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInAnonymously:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            new WebHelper().execute("GET","Login.php?deviceID="+user.getUid()+"&token="+FirebaseInstanceId.getInstance().getToken());
                        } else {
                            Log.w(TAG, "signInAnonymously:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
