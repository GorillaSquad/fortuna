package com.example.jason.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.inmobi.ads.InMobiBanner;
import com.inmobi.sdk.InMobiSdk;

public class AdvertisementTest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisement);

        InMobiSdk.init(this, "36567d23a95f40d286f3abf52bb96720");

        InMobiBanner bannerAd = new InMobiBanner(this, 1531708574428L);

        LinearLayout adContainer = (LinearLayout) findViewById(R.id.ad_container);
        LinearLayout.LayoutParams bannerLp = new LinearLayout.LayoutParams(950, 900);
        //bannerLp.
        adContainer.addView(bannerAd,bannerLp);
        bannerAd.load();

        InMobiBanner bannerAd2 = (InMobiBanner)findViewById(R.id.banner);
        //bannerAd2.load();
    }
}
