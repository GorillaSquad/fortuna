package com.example.jason.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.jason.myapplication.containers.Chat;
import com.example.jason.myapplication.containers.Matches;
import com.example.jason.myapplication.network.Account;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatRoom extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private MyBroadRequestReceiver receiver;

    private FirebaseUser user;
    private Account myAccount;

    private Chat chat;
    private String match;
    private String matchId;

    public class MyBroadRequestReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Gson gson = new Gson();

            Chat.ChatMessage message = gson.fromJson(intent.getStringExtra("message"), Chat.ChatMessage.class);
            addMessage(message);
        }
    }

    public void addMessage(Chat.ChatMessage message) {
        Chat.ChatMessage[] messages = ((ChatAdapter)mAdapter).getData();
        ArrayList<Chat.ChatMessage> messageList = new ArrayList<Chat.ChatMessage>(Arrays.asList(messages));

        messageList.add(message);

        ((ChatAdapter)mAdapter).updateData(messageList.toArray(new Chat.ChatMessage[0]));
        mAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(messages.length);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        IntentFilter filter = new IntentFilter("IncomingMessage");
        receiver = new MyBroadRequestReceiver();
        registerReceiver( receiver, filter);

        user = FirebaseAuth.getInstance().getCurrentUser();
        myAccount = new Account(user.getUid());
        Intent incomingIntent = getIntent();
        String matchJson = incomingIntent.getStringExtra("match");
        Log.d("JSON", matchJson);
        Gson gson = new Gson();
        Matches.Match m = gson.fromJson(matchJson, Matches.Match.class);
        if(m.deviceID.equalsIgnoreCase(user.getUid())){
            match = m.matchedWith;
        }else{
            match = m.deviceID;
        }
        matchId = m.id;
        chat = myAccount.getChatWith(m.id);

        mRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        if(chat == null)
            chat = new Chat();

        mAdapter = new ChatAdapter(chat.messages, match);
        mRecyclerView.setAdapter(mAdapter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        if(chat.messages != null)
            mRecyclerView.smoothScrollToPosition(chat.messages.length);


    }

    @Override
    public void onStop() {
        Log.d("DESTROY", "stopped chat room");
        super.onStop();
    }
    @Override
    public void onDestroy() {
        Log.d("DESTROY", "closed chat room");
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void sendMessage(View v) {
        EditText input = findViewById(R.id.editText);
        String msg = input.getText().toString();
        myAccount.sendMessageTo(match,matchId, msg);
        input.setText("");

        Chat.ChatMessage message = new Chat().new ChatMessage();
        message.message = msg;
        message.to = match;
        message.from = "";
        message.timestamp = (System.currentTimeMillis()/1000)+"";
        addMessage(message);
    }

    public void leaveChat(View v) {
        myAccount.sendMessageTo(match, matchId,"/leave");
        Chat.ChatMessage message = new Chat().new ChatMessage();
        message.message = "/leave";
        message.to = match;
        message.from = "";
        message.timestamp = (System.currentTimeMillis()/1000)+"";
        addMessage(message);
    }


}
