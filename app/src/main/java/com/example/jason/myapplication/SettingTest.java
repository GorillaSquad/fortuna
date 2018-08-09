package com.example.jason.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Switch;

public class SettingTest extends AppCompatActivity {
    public void switchTest (View v) {
        Switch allowAds = (Switch) findViewById(R.id.ad_switch);
        if (allowAds) {
            allowAds.setTextOff();
        }
    }
}
