package com.wlvpn.slider.whitelabelvpn.utilities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * This class executes registered tasks when/if in the right network state.
 *
 * @author pat
 */
public class NetworkStateReceiver extends BroadcastReceiver {
    public enum NetworkState {
        HasNetworkConnection,
        HasNoNetworkConnection
    }

    private static final String TAG = "NetworkStateTaskQueue";
    private final Activity runOnUiThreadActivity;
    private final List<NetworkStateListener> listeners = new ArrayList<>();

    public NetworkStateReceiver(Activity runOnUiThreadActivity) {
        this.runOnUiThreadActivity = runOnUiThreadActivity;
    }

    public void addListener(NetworkStateListener listener) {
        listeners.add(listener);
    }

    public void removeListener(NetworkStateListener listener) {
        listeners.remove(listener);
    }

    public static NetworkState getNetworkState(Context context) {
        NetworkInfo networkInfo = getCurrentNetworkInfo(context);
        return stateFromNetworkInfo(networkInfo);
    }

    private static NetworkState stateFromNetworkInfo(NetworkInfo networkInfo) {
        if (networkInfo == null || networkInfo.getState() != NetworkInfo.State.CONNECTED) {
            return NetworkState.HasNoNetworkConnection;
        } else {
            return NetworkState.HasNetworkConnection;
        }
    }

    private static NetworkInfo getCurrentNetworkInfo(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    @Override
    public synchronized void onReceive(Context receiverContext, Intent intent) {
        NetworkInfo networkInfo = getCurrentNetworkInfo(receiverContext);

        final NetworkState currentState = stateFromNetworkInfo(networkInfo);
        Timber.d("Network state changed: %s", currentState);
        for (final NetworkStateListener listener : listeners) {
            if (runOnUiThreadActivity != null) {
                runOnUiThreadActivity.runOnUiThread(() -> listener.networkStateChanged(currentState));
            } else {
                listener.networkStateChanged(currentState);
            }
        }
    }


}
