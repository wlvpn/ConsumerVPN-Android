package com.wlvpn.slider.whitelabelvpn.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;

import javax.inject.Inject;

public class SplashActivity extends BaseActivity {

    @Inject
    AccountManager accountManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        Intent intent = accountManager.isUserLoggedIn() ?
                new Intent(this, MainActivity.class)
                : new Intent(this, LoginActivity.class);

        startActivity(intent);
        finish();
    }
}
