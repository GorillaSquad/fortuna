package com.example.jason.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        if(message.message.equalsIgnoreCase("/leave"))
        {
            Log.d("TEST", "LEAVE");
            EditText input = findViewById(R.id.editText);
            ImageButton sendButton = findViewById(R.id.sendButton);
            ImageButton endButton = findViewById(R.id.endButton);
            input.setEnabled(false);
            sendButton.setOnClickListener(null);
            endButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavUtils.navigateUpFromSameTask(ChatRoom.this);
                }
            });
        }
        Chat.ChatMessage[] messages = ((ChatAdapter)mAdapter).getData();
        ArrayList<Chat.ChatMessage> messageList = new ArrayList<>(Arrays.asList(messages));

        messageList.add(message);

        ((ChatAdapter)mAdapter).updateData(messageList.toArray(new Chat.ChatMessage[0]));
        mRecyclerView.smoothScrollToPosition(messages.length);
        mAdapter.notifyDataSetChanged();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        InMobiSdk.init(this, "36567d23a95f40d286f3abf52bb96720");

        IntentFilter filter = new IntentFilter("IncomingMessage");
        receiver = new MyBroadRequestReceiver();
        registerReceiver( receiver, filter);

        EditText editText = (EditText) findViewById(R.id.editText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendMessage(null);
                    handled = true;
                }
                return handled;
            }
        });

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

        mAdapter = new ChatAdapter(chat.messages, match, this);
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
        if(msg.replaceAll(" ", "").equals(""))
            return;
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

        new AlertDialog.Builder(this)
                .setTitle("End Chat")
                .setMessage("Do you really want to end the chat?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        sendLeave();
                    }})
                .setNegativeButton(android.R.string.no, null).show();


    }

    private void sendLeave() {
        myAccount.sendMessageTo(match, matchId,"/leave");
        Chat.ChatMessage message = new Chat().new ChatMessage();
        message.message = "/leave";
        message.to = match;
        message.from = "";
        message.timestamp = (System.currentTimeMillis()/1000)+"";
        addMessage(message);
    }

}
