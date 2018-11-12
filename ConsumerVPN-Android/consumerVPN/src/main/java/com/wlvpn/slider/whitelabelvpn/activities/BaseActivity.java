package com.wlvpn.slider.whitelabelvpn.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;

import javax.inject.Inject;
import javax.inject.Named;

import rx.subscriptions.CompositeSubscription;

import static com.wlvpn.slider.whitelabelvpn.di.AppModule.IS_DEVICE_TV_PROPERTY;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {

    public final static int CLICK_DELAY = 500;

    @Inject
    @Named(IS_DEVICE_TV_PROPERTY)
    boolean isTv;

    private CompositeSubscription mainSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        if (!isTv) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

    @Override
    protected void onResume() {

        mainSubscription = new CompositeSubscription();

        // For everything except tv use portrait only

        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mainSubscription != null) {
            mainSubscription.unsubscribe();
            mainSubscription = null;
        }
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public CompositeSubscription getMainSubscription() {
        return mainSubscription;
    }
}
