package com.wlvpn.slider.whitelabelvpn.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import com.wlvpn.slider.whitelabelvpn.BuildConfig;
import com.wlvpn.slider.whitelabelvpn.R;

public class AboutActivity extends BaseActivity {

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView version = findViewById(R.id.activity_about_tv_version);
        version.setText("v" + BuildConfig.VERSION_NAME);
    }
}
