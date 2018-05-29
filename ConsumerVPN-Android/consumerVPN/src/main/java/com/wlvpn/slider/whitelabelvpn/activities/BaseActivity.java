package com.wlvpn.slider.whitelabelvpn.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity {

    public final static int CLICK_DELAY = 500;

    private CompositeSubscription mainSubscription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {

        mainSubscription = new CompositeSubscription();

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
