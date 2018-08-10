package com.example.jason.myapplication;

import com.example.jason.myapplication.containers.Chat;
import com.example.jason.myapplication.helpers.SavedInfo;
import com.example.jason.myapplication.network.Account;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.example.jason.myapplication.helpers.WebHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private FirebaseUser user;
    private Account myAccount;
    private MainActivity.MyBroadRequestReceiver receiver;
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

    boolean joinPress;
    public void startQueue(View v) {

        int colorStart = 0xFF99CC00;
        int colorEnd = 0xFFEC1F43;
        ValueAnimator queueAnimation = ObjectAnimator.ofInt(v,
                "backgroundColor", colorStart, colorEnd);
        queueAnimation.setDuration(100);
        queueAnimation.setEvaluator(new ArgbEvaluator());

        Button queueBtn = (Button)findViewById(R.id.joinBtn);
        myAccount.joinQueue();

        if (myAccount.isInQueue() && !joinPress) {
            queueAnimation.start();
            queueBtn.setText("In Queue!");
            joinPress = true;
            Log.d("queueJoined", "Joined");
        } else if (joinPress) {
            queueAnimation.reverse();
            myAccount.leaveQueue();
            queueBtn.setText("Join");
            joinPress = false;
            Log.d("Queue Left", "Left");
        }
    }

    public void sendNotification(String textTitle, String textContent, int notificationID, String channelID) {
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

        JSONObject consentObject = new JSONObject();
        try {
            // Provide correct consent value to sdk which is obtained by User
            consentObject.put(InMobiSdk.IM_GDPR_CONSENT_AVAILABLE, false);
            // Provide 0 if GDPR is not applicable and 1 if applicable
            consentObject.put("gdpr", "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InMobiSdk.init(this, "36567d23a95f40d286f3abf52bb96720", consentObject);
        InMobiBanner bannerAd = new InMobiBanner(this, 1531708574428L);

        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.banner);
        RelativeLayout.LayoutParams bannerLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bannerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adContainer.addView(bannerAd,bannerLp);
        bannerAd.load();

        // LOGIN
        StartUp start = new StartUp(this);
        start.start();
        //END OF LOGIN


        mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInAnonymously:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    myAccount = new Account(user.getUid());
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    //Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        IntentFilter filter = new IntentFilter("MatchFound");
        receiver = new MainActivity.MyBroadRequestReceiver();
        registerReceiver( receiver, filter);

        FirebaseUser user = mAuth.getCurrentUser();
        myAccount = new Account(user.getUid());
        SavedInfo.getInstance().load(this);
        Log.w(TAG, "CONSENT " + SavedInfo.getInstance().EUConsent);
    }

    public void test(View v){
        Log.w(TAG, "CONSENT " + SavedInfo.getInstance().EUConsent);
        SavedInfo.getInstance().EUConsent = true;
        SavedInfo.getInstance().save(this);

        SavedInfo.getInstance().load(this);
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }


    public class MyBroadRequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String match = intent.getStringExtra("match");
            startChat(match);
        }
    }


    public void startChat(String match){
        Intent chatIntent = new Intent(this, ChatRoom.class);
        chatIntent.putExtra("match", match);
        Log.d(TAG, match);
        startActivity(chatIntent);
    }
}