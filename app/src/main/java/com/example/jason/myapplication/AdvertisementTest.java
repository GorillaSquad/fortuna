package com.example.jason.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

        RelativeLayout adContainer = (RelativeLayout) findViewById(R.id.ad_container);
        RelativeLayout.LayoutParams bannerLp = new RelativeLayout.LayoutParams(950, 900);
        bannerLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bannerLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        adContainer.addView(bannerAd,bannerLp);



        bannerAd.load();
    }
}
