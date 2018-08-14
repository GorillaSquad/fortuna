package com.example.jason.myapplication;

import com.example.jason.myapplication.containers.Matches;
import com.example.jason.myapplication.helpers.SavedInfo;
import com.example.jason.myapplication.network.Account;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private FirebaseUser user;
    private Account myAccount;
    private MainActivity.MyBroadRequestReceiver receiver;
    private FirebaseAuth mAuth;
    InMobiBanner bannerAd;

    boolean joinPress;
    int green   = 0xFF99CC00;
    int red     = 0xFFEC1F43;
    int orange  = 0xFFe76e18;
    int buttonColour = green;

    View.OnClickListener startQueue = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(myAccount == null){
                Toast.makeText(MainActivity.this,"Connecting to server. Try again in a few seconds.",Toast.LENGTH_SHORT);
                return;
            }

            Button queueBtn = (Button)findViewById(R.id.joinBtn);

            if (!joinPress) {
                myAccount.joinQueue();
                joinPress = true;
                Log.d("queueJoined", "Joined");
                switchButton("leaveQueue");
            } else if (joinPress) {
                myAccount.leaveQueue();
                joinPress = false;
                Log.d("Queue Left", "Left");
                switchButton("joinQueue");
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel("Messages", "Notifications when someone sends you a new message", "USER_MESSAGE");

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
        bannerAd = new InMobiBanner(this, 1531708574428L);

        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.banner);
        RelativeLayout.LayoutParams bannerLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        bannerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adContainer.addView(bannerAd,bannerLp);


        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInAnonymously:success");
                    user = mAuth.getCurrentUser();
                    myAccount = new Account(user.getUid());
                    myAccount.login();
                    refresh();
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    //Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(user != null) {
            myAccount = new Account(user.getUid());
        }

        IntentFilter filter = new IntentFilter("MatchFound");
        receiver = new MainActivity.MyBroadRequestReceiver();
        registerReceiver( receiver, filter);


        SavedInfo.getInstance().load(this);
        Log.d(TAG, "CONSENT " + SavedInfo.getInstance().EUConsent);


    }

    private void switchButton(String state) {
        int colour = 0;
        Button queueBtn = (Button) findViewById(R.id.joinBtn);
        switch(state){
            case "joinQueue":
                colour = green;
                queueBtn.setText("Join");
                break;
            case "leaveQueue":
                colour = red;
                queueBtn.setText("In Queue!");
                break;
            case "joinChat":
                colour = orange;
                queueBtn.setText("Enter Chat");
                break;
        }
        ValueAnimator queueAnimation = ObjectAnimator.ofInt(queueBtn, "backgroundColor", buttonColour, colour);
        queueAnimation.setDuration(100);
        queueAnimation.setEvaluator(new ArgbEvaluator());
        queueAnimation.start();
        buttonColour = colour;
    }

    public void refresh(){
        Button queueBtn = findViewById(R.id.joinBtn);
        if (myAccount == null) {
            queueBtn.setOnClickListener(startQueue);
            if(user != null){
                myAccount = new Account(user.getUid());
            } else {
                Log.d(TAG, "myAccount is null");
                super.onResume();
                return;
            }
        }
        final Matches.Match[] matches = myAccount.getMatches().matches;



        if (matches.length > 0) {
            switchButton("joinChat");
            queueBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Gson gson = new Gson();
                    String match = gson.toJson(matches[0]);
                    //String matchId = matches[0].matchedWith;
                    startChat(match);
                }
            });
        } else if (myAccount.isInQueue()) {
            joinPress = true;
            switchButton("leaveQueue");
            queueBtn.setOnClickListener(startQueue);
        } else {
            joinPress = false;
            switchButton("joinQueue");
            queueBtn.setOnClickListener(startQueue);
        }
        bannerAd.load();
    }

    public void onResume() {
        refresh();
        super.onResume();
    }


    public void test(View v){
        Log.d(TAG, "CONSENT " + SavedInfo.getInstance().EUConsent);
        SavedInfo.getInstance().EUConsent = true;
        SavedInfo.getInstance().save(this);
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

    private void createNotificationChannel(String channel_name, String channel_description, String channel_id) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channel_id, channel_name, importance);
            channel.setDescription(channel_description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void startChat(String match) {
        Intent chatIntent = new Intent(this, ChatRoom.class);
        chatIntent.putExtra("match", match);
        startActivity(chatIntent);
    }
}