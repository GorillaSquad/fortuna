package com.example.jason.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.jason.myapplication.containers.Matches;
import com.example.jason.myapplication.helpers.WebHelper;
import com.example.jason.myapplication.network.Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

public class StartUp {

    private String TAG = "StartUp";
    private FirebaseAuth mAuth;
    private Context context;

    public StartUp(Context c) {
        context = c;
    }

    public void start() {
        mAuth = FirebaseAuth.getInstance();
        firebaseLogin();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null)
            loginAccount(user.getUid());
    }

    public void firebaseLogin() {

    }

    public void loginAccount(String accountID) {
        Log.w(TAG, "logging in " + accountID);
        Account myAccount = new Account(accountID);
        myAccount.login();
    }
}
