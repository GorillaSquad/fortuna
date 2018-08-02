package com.example.jason.myapplication;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

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
        FirebaseUser user = mAuth.getCurrentUser();
        firebaseLogin(user.getUid());
        loginAccount(user.getUid());
    }

    public void firebaseLogin(final String uid) {
        mAuth.signInAnonymously().addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInAnonymously:success");
                } else {
                    Log.w(TAG, "signInAnonymously:failure", task.getException());
                    //Toast.makeText(MainActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loginAccount(String accountID) {
        Account myAccount = new Account(accountID);
        myAccount.login();
    }
}