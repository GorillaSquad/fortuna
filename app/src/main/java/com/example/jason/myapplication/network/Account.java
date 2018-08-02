package com.example.jason.myapplication.network;

import android.util.Log;

import com.example.jason.myapplication.helpers.WebHelper;
import com.google.firebase.iid.FirebaseInstanceId;

public class Account {

    String id;

    public Account(String id) {
        this.id = id;
    }

    public void login() {
        Log.d("TEST", new WebHelper().execute("GET","Login.php?deviceID="+id+"&token="+ FirebaseInstanceId.getInstance().getToken())+"");
    }


    public void getChatWith(String other) {
        new WebHelper().execute("GET","QueueManager.php?from="+id+"&to="+other);
    }

    public void joinQueue() {
        new WebHelper().execute("GET","QueueManager.php?deviceID="+id+"&command=join");
    }

    public void leaveQueue() {
        new WebHelper().execute("GET","QueueManager.php?deviceID="+id+"&command=remove");
    }

    public void getMatches() {
        String matchesText = new WebHelper().execute("GET","UserManager.php?deviceID="+id+"&command=getChats")+"";
    }

    public void sendMessageTo(String to, String message){
        new WebHelper().execute("GET","SendMessage.php?from="+id+"&to="+to+"&message="+message);
    }
}
