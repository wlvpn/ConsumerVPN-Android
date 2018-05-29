package com.wlvpn.slider.whitelabelvpn.layouts;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnState;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;

import kotlin.Unit;
import timber.log.Timber;

public class ConnectingLayout extends ConstraintLayout
        implements LifecycleObserver {

    private TextView connectingProgress;

    private ICallback<VpnState> stateSub;

    public ConnectingLayout(Context context) {
        super(context);

        ConsumerVpnApplication.component().inject(this);

        ((AppCompatActivity) context).getLifecycle().addObserver(this);

        inflate(getContext(), R.layout.layout_connecting, this);

        connectingProgress = findViewById(R.id.fragment_connecting_tv_connecting_progress);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        setStateCallback();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void setStateCallback() {
        if (stateSub == null) {

            stateSub = ConsumerVpnApplication.getVpnSdk().listenToConnectState()
                    .onNext(vpnState -> {
                        if (connectingProgress != null) {
                            connectingProgress.setText(vpnState.getConnectionDescription());
                        }
                        return Unit.INSTANCE;
                    })
                    .onError(throwable -> {
                        Timber.e(throwable, "Failed to get VPN state");
                        return Unit.INSTANCE;
                    })
                    .subscribe();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onPause();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPause() {
        if (stateSub != null) {
            stateSub.unsubscribe();
            stateSub = null;
        }
    }
}