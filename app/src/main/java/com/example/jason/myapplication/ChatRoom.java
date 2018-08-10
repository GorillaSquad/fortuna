package com.example.jason.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.jason.myapplication.containers.Chat;
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
        match = incomingIntent.getStringExtra("match");
        chat = myAccount.getChatWith(match);

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
    }

    @Override
    public void onDestroy() {
        this.unregisterReceiver(receiver);
        super.onDestroy();
    }

    public void sendMessage(View v) {
        EditText input = findViewById(R.id.editText);
        String msg = input.getText().toString();
        myAccount.sendMessageTo(match, msg);
        input.setText("");

        Chat.ChatMessage message = new Chat().new ChatMessage();
        message.message = msg;
        message.to = match;
        message.from = "";
        message.timestamp = (System.currentTimeMillis()/1000)+"";
        addMessage(message);
    }

    public void test(View v){
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("IncomingMessage");
        sendBroadcast(broadcastIntent);
    }
}
