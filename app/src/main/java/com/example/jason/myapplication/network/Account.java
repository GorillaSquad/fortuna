package com.example.jason.myapplication.network;

import android.util.Log;

import com.example.jason.myapplication.containers.Chat;
import com.example.jason.myapplication.containers.Matches;
import com.example.jason.myapplication.helpers.WebHelper;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

public class Account {

    String id;

    public Account(String id) {
        this.id = id;
    }

    // ACCOUNT
    public void login() {
        new WebHelper().execute("GET","Login.php?deviceID="+id+"&token="+ FirebaseInstanceId.getInstance().getToken());
    }

    public Matches getMatches() {
        Matches m = null;
        Gson gson = new Gson();
        try {
            String test = new WebHelper().execute("GET", "UserManager.php?deviceID=" + id + "&command=getChats").get();
            m = gson.fromJson(test, Matches.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return m;
    }

    // MESSAGING
    public Chat getChatWith(String other) {
        Chat c = null;
        Gson gson = new Gson();
        try {
            c = gson.fromJson(new WebHelper().execute("GET","GetMessages.php?from="+id+"&to="+other).get(), Chat.class);
        }catch(Exception e){
            e.printStackTrace();
        }
        return c;
    }

    public void sendMessageTo(String to, String message){
        new WebHelper().execute("GET","SendMessage.php?from="+id+"&to="+to+"&message="+message);
    }

    // QUEUE
    public void joinQueue() {
        new WebHelper().execute("GET","QueueManager.php?deviceID="+id+"&command=join");
    }

    public void leaveQueue() {
        new WebHelper().execute("GET","QueueManager.php?deviceID="+id+"&command=remove");
    }
}
