package com.wlvpn.slider.whitelabelvpn.activities;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.gentlebreeze.vpn.http.api.error.LoginErrorThrowable;
import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnConnectionInfo;
import com.gentlebreeze.vpn.sdk.model.VpnGeoData;
import com.gentlebreeze.vpn.sdk.model.VpnState;
import com.jakewharton.rxbinding.view.RxView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.auth.Credentials;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.fragments.VpnPermissionDenialDialogFragment;
import com.wlvpn.slider.whitelabelvpn.helpers.ConnectionHelper;
import com.wlvpn.slider.whitelabelvpn.helpers.PreferencesHelper;
import com.wlvpn.slider.whitelabelvpn.jobs.ServersRefreshJob;
import com.wlvpn.slider.whitelabelvpn.jobs.TokenRefreshJob;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectedLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectingLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.SliderLayout;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.NavigationManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.managers.VpnNotificationManager;
import com.wlvpn.slider.whitelabelvpn.models.ApiErrorCodes;
import com.wlvpn.slider.whitelabelvpn.settings.ConnectionStartupPref;
import com.wlvpn.slider.whitelabelvpn.startup.Startup;
import com.wlvpn.slider.whitelabelvpn.utilities.AnimationUtil;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kotlin.Unit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class MainActivity extends BaseActivity {

    public static final int VPN_PREPARE = 1000;

    private static final String VPN_PREPARE_PERMISSION_DENIED =
            MainActivity.class.getCanonicalName() + "VPN_PREPARE_PERMISSION_DENIED";

    @Inject
    ConnectionHelper connectionHelper;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AccountManager accountManager;

    @Inject
    VpnNotificationManager vpnNotificationManager;

    @Inject
    NavigationManager navigationManager;

    @Inject
    ConnectableManager connectableManager;

    @Inject
    CredentialsManager credentialsManager;

    private ICallback<VpnState> callbackState;
    private ViewGroup mainContainer;
    private Button connectButton;
    private boolean isVpnPermissionDenied;

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(VPN_PREPARE_PERMISSION_DENIED, isVpnPermissionDenied);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_main);

        mainContainer = findViewById(R.id.activity_main_fl_container);
        connectButton = findViewById(R.id.button_connect);

        if (accountManager.isUserLoggedIn()) {

            TokenRefreshJob.schedule();
            ServersRefreshJob.schedule();

            refreshToken();

            if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                presentConnected();
            } else if (isBootLoaded()) {
                bootStart();
            } else {
                presentSlider();
            }

            if (savedInstanceState != null) {
                isVpnPermissionDenied = savedInstanceState.getBoolean(
                        VPN_PREPARE_PERMISSION_DENIED, false);
            }

        } else {
            presentLoginView();
        }
    }

    /**
     * Delegates the onResume from the MainActivity
     */
    @Override
    public void onResume() {
        super.onResume();

        callbackState = registerVpnStateListener();
        getMainSubscription().add(registerNavigationListener());

        initListeners();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        // Check if the dialog exists, if exists it will be recover from the fragment manager
        VpnPermissionDenialDialogFragment vpnPermissionDenialDialog =
                (VpnPermissionDenialDialogFragment) getSupportFragmentManager()
                        .findFragmentByTag(VpnPermissionDenialDialogFragment.TAG);

        if (isVpnPermissionDenied) {

            if (vpnPermissionDenialDialog == null) {
                vpnPermissionDenialDialog = VpnPermissionDenialDialogFragment.newInstance();

                vpnPermissionDenialDialog.show(
                        getSupportFragmentManager(),
                        VpnPermissionDenialDialogFragment.TAG);
            }

            vpnPermissionDenialDialog.setDialogFragmentCallBack(this::startVpnConnection);
        } else if (vpnPermissionDenialDialog != null) {
            vpnPermissionDenialDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        callbackState.unsubscribe();
        callbackState = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case VPN_PREPARE:
                if (resultCode == RESULT_OK) {
                    isVpnPermissionDenied = false;
                    startVpnConnectionTask();
                } else {
                    isVpnPermissionDenied = true;
                    disconnectVpn();
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_main_si_about:
                launchAboutActivity();
                break;
            case R.id.activity_main_si_settings:
                launchSettingsActivity();
                break;
            case R.id.activity_main_si_logout:
                showLogoutAlert();
                break;
        }
        return true;
    }

    private void refreshToken() {
        if (!ConsumerVpnApplication.getVpnSdk().isAccessTokenValid()) {
            ConsumerVpnApplication.getVpnSdk()
                    .refreshToken()
                    .subscribe(vpnLoginResponse -> {
                        tokenFailLogin();
                        return Unit.INSTANCE;
                    }, throwable -> {
                        Timber.e(throwable, "Error while trying to refresh Token");
                        tokenFailLogin();
                        return Unit.INSTANCE;
                    });
        }
    }

    private void tokenFailLogin() {
        Credentials credentials = credentialsManager.getCredentials();

        ConsumerVpnApplication.getVpnSdk()
                .loginWithUsername(credentials.getUsername(), credentials.getPassword())
                .subscribe(vpnLoginResponse -> Unit.INSTANCE,
                        throwable -> {
                            Timber.e(throwable,
                                    "The login failed while trying to refresh token");
                            LoginErrorThrowable loginThrowable =
                                    (LoginErrorThrowable) throwable;
                            // When login fails that means user updated
                            // it's credentials on web the user will be logged out
                            if (loginThrowable.getResponseCode() ==
                                    ApiErrorCodes.INVALID_CREDENTIALS) {

                                if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                                    ConsumerVpnApplication.getVpnSdk().disconnect();
                                }

                                Toast.makeText(getApplicationContext(),
                                        "Your Credentials are no longer valid for ConsumerVPN",
                                        Toast.LENGTH_LONG).show();

                                logout();
                            }
                            return Unit.INSTANCE;
                        });
    }

    public void initListeners() {
        getMainSubscription().add(RxView.clicks(connectButton)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    switch (navigationManager.getCurrentLocation()) {
                        case NavigationManager.LOCATION_SLIDER:
                            onConnectClicked();
                            break;

                        case NavigationManager.LOCATION_CONNECTED:
                        case NavigationManager.LOCATION_CONNECTING:
                            onDisconnectClicked();
                            break;
                    }
                }));
    }

    public boolean isBootLoaded() {
        return getIntent().getBooleanExtra(Startup.FLAG_INITIAL_BOOT, false);
    }

    public void showLoginFailedToast() {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
    }

    public void showNoTunDialog(DialogInterface.OnClickListener positiveClickListener,
                                DialogInterface.OnClickListener negativeClickListener) {

        new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme)
                .setTitle(R.string.no_tun_module_found)
                .setMessage(getString(R.string.no_tun_module_found_message,
                        getString(R.string.app_name)))
                .setPositiveButton(R.string.no_tun_module_found_install_button, positiveClickListener)
                .setNegativeButton(R.string.no_tun_module_found_cancel_button, negativeClickListener)
                .create()
                .show();
    }

    public void presentLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); //Avoid adding main activity to back trace on login activity
    }

    public void shouldLaunchActivityWithUri(Uri uri) {
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }

    public void showLogoutAlert() {
        new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme)
                .setTitle(R.string.alert_dialog_title_logout_confirmation)
                .setMessage(R.string.alert_dialog_message_logout_confirmation)
                .setNegativeButton(R.string.alert_dialog_negative_button, null)
                .setPositiveButton(R.string.alert_dialog_positive_button, (dialog, which) -> logout())
                .create()
                .show();
    }

    public void launchAboutActivity() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void launchSettingsActivity() {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void presentView(@NavigationManager.Navigation int location) {

        View nextView;

        final View currentView = mainContainer.getChildCount() > 0 ?
                mainContainer.getChildAt(0) :
                null;

        switch (location) {
            case NavigationManager.LOCATION_SLIDER:
                setConnectButtonColor(R.drawable.button_connect);
                setConnectButtonText(R.string.connect);

                nextView = new SliderLayout(this);
                break;
            case NavigationManager.LOCATION_CONNECTED:
                setConnectButtonColor(R.drawable.button_disconnect);
                setConnectButtonText(R.string.disconnect);

                nextView = new ConnectedLayout(this);
                break;
            case NavigationManager.LOCATION_CONNECTING:
                setConnectButtonColor(R.drawable.button_disconnect);
                setConnectButtonText(R.string.cancel);

                nextView = new ConnectingLayout(this);
                break;

            default:
                return;
        }

        mainContainer.addView(nextView, 0);

        if (currentView != null) {
            AnimationUtil.fadeInView(nextView,
                    getResources().getInteger(R.integer.fade_in_time),
                    null);

            AnimationUtil.fadeOutView(
                    currentView,
                    getResources().getInteger(R.integer.fade_out_time),
                    new AnimationUtil.AnimationListener() {
                        @Override
                        public void onAnimationEnd(View view) {
                            mainContainer.removeView(currentView);
                        }
                    });
        }
    }

    private ICallback<VpnState> registerVpnStateListener() {
        return ConsumerVpnApplication.getVpnSdk().listenToConnectState()
                .onNext(vpnState -> {
                    updateNotification(vpnState);
                    String state = vpnState.getConnectionDescription();
                    int connectionState = vpnState.getConnectionState();
                    switch (connectionState) {
                        case VpnState.DISCONNECTED:
                            presentSlider();
                            if (state.contains("auth-failure")) {
                                showLoginFailedToast();
                            } else if (state.equals("NO_TUN")) {
                                showNoTunDialog(
                                        getPositiveNoTunDialogListener(),
                                        getNegativeNoTunDialogListener());
                            }
                            break;
                        case VpnState.CONNECTED:
                            presentConnected();
                            break;
                        case VpnState.CONNECTING:
                            presentConnecting();
                            break;
                    }
                    return Unit.INSTANCE;
                })
                .onError(throwable -> {
                    Timber.e(throwable, "Failed to listen to vpn state");
                    return Unit.INSTANCE;
                })
                .subscribe();
    }

    /**
     * Register the Navigation subject listener
     * to handle view changes
     */
    private Subscription registerNavigationListener() {
        return navigationManager.getLocationObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::presentView);
    }

    /**
     * Update the notification if vpn is in one of
     * three different states to prevent unnecessary
     * db querying
     *
     * @param vpnState current vpn state
     */
    private void updateNotification(VpnState vpnState) {
        switch (vpnState.getConnectionState()) {
            case VpnState.CONNECTED:

                VpnConnectionInfo vpnConnectionInfo
                        = ConsumerVpnApplication.getVpnSdk().getConnectionInfo();

                vpnNotificationManager.updateConnectionNotification(
                        R.string.notification_vpn_connected_title,
                        vpnConnectionInfo
                );
                break;
            default:
                vpnNotificationManager.cancelNotifications();
        }
    }

    private void presentConnected() {
        navigationManager.navigateTo(NavigationManager.LOCATION_CONNECTED);
    }

    private void presentConnecting() {
        navigationManager.navigateTo(NavigationManager.LOCATION_CONNECTING);
    }

    private void presentSlider() {
        navigationManager.navigateTo(NavigationManager.LOCATION_SLIDER);
    }

    public void onConnectClicked() {
        presentConnecting();
        startVpnConnection();
    }

    public void onDisconnectClicked() {
        presentSlider();
        disconnectVpn();
    }

    public void logout() {
        if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
            ConsumerVpnApplication.getVpnSdk().disconnect();
        }

        accountManager.logout();
        settingsManager.clear();
        connectableManager.clear();
        disconnectVpn();
        presentLoginView();
    }

    /**
     * Disconnect from VPN
     */
    public void disconnectVpn() {
        ConsumerVpnApplication.getVpnSdk().disconnect()
                .subscribe(aBoolean -> {
                    Timber.d("Disconnected vpn service");
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e("Failed to disconnect vpn service");
                    return Unit.INSTANCE;
                });
    }

    /**
     * Will boot load if coming from a BOOT_COMPLETED state
     */
    private void bootStart() {
        ConnectionStartupPref startupPref = settingsManager.getConnectionStartupPref();

        VpnGeoData geoInfo = ConsumerVpnApplication.getVpnSdk().getGeoInfo();

        switch (startupPref.getConnectionStartUpPref()) {
            case ConnectionStartupPref.CONNECT_TO_LAST_CONNECTED:
                String host = connectableManager.getSelectedHost();
                if (host == null) {
                    presentSlider();
                } else {
                    presentConnecting();
                    startVpnConnection();
                }
                break;
            case ConnectionStartupPref.CONNECT_TO_FASTEST:
                bootToFastestServerInCountry(geoInfo.getGeoCountryCode());
                break;
            case ConnectionStartupPref.CONNECT_TO_FASTEST_IN_COUNTRY:
                String startupCountry = settingsManager.getStartupCountry();
                if (startupCountry != null) {
                    bootToFastestServerInCountry(startupCountry);
                } else {
                    bootToFastestServerInCountry(geoInfo.getGeoCountryCode());
                }
                break;
        }
    }

    /**
     * Boot to the fastest server in the country
     *
     * @param countryCode to boot to
     */
    private void bootToFastestServerInCountry(final String countryCode) {
        connectionHelper.connectByCountryCode(countryCode);
    }

    /**
     * Start Vpn Connection
     */
    private void startVpnConnection() {
        if (ConsumerVpnApplication.getVpnSdk().isVpnServicePrepared()) {
            startVpnConnectionTask();
        } else {
            ConsumerVpnApplication.getVpnSdk().prepareVpnService(this);
        }
    }

    /**
     * Starts the Vpn Connection task
     */
    private void startVpnConnectionTask() {
        String selectedCountryCode = "";
        String selectedCity = "";

        if (connectableManager.getConnectable() != null) {
            selectedCountryCode = connectableManager.getConnectable().getCountryCode();
            selectedCity = connectableManager.getConnectable().getCity();
        }

        if (!TextUtils.isEmpty(selectedCountryCode)
                && !TextUtils.isEmpty(selectedCity)) {
            connectByVpnPop(selectedCountryCode, selectedCity);
        } else {
            connectByGeo();
        }
    }

    /**
     * Uses connectable manager configuration
     * to connect to vpn pop
     */
    private void connectByVpnPop(String countryCode, String city) {
        ConsumerVpnApplication.getVpnSdk().fetchPopByCountryCodeAndCity(countryCode, city)
                .subscribe(vpnPop -> {
                    connectionHelper.connectByVpnPop(vpnPop);
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to find pop");
                    return Unit.INSTANCE;
                });
    }

    /**
     * Let SDK handle the connection using the geolocation
     */
    private void connectByGeo() {
        ConsumerVpnApplication.getVpnSdk().fetchGeoInfo()
                .subscribe(geoData -> {
                    connectionHelper.connectByGeo();
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to fetch IpGeo");
                    connectionHelper.connectByGeo();
                    return Unit.INSTANCE;
                });
    }

    /**
     * Get positive action on no tun
     *
     * @return DialogInterface.OnClickListener
     */
    private DialogInterface.OnClickListener getPositiveNoTunDialogListener() {
        return (dialog, whichButton) -> {
            try {
                shouldLaunchActivityWithUri(PreferencesHelper.getTunKoMarketURL());
            } catch (ActivityNotFoundException ex) {
                Timber.e(ex);
                shouldLaunchActivityWithUri(PreferencesHelper.getTunKoMarketWebsiteUrl());
            }
        };
    }

    /**
     * Get negative action on No Tun
     *
     * @return DialogInterface.OnClickListener
     */
    private DialogInterface.OnClickListener getNegativeNoTunDialogListener() {
        return (dialog, whichButton) -> presentSlider();
    }

    public void setConnectButtonColor(@DrawableRes int backgroundId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            connectButton.setBackground(
                    ContextCompat.getDrawable(getApplicationContext(), backgroundId));
        } else {
            connectButton.setBackgroundDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), backgroundId));
        }
    }

    public void setConnectButtonText(@StringRes int stringRes) {
        connectButton.setText(stringRes);
    }

    public void enableConnectButton() {
        connectButton.setFocusable(true);
        connectButton.setEnabled(true);
    }

    public void disableConnectButton() {
        connectButton.setFocusable(false);
        connectButton.setEnabled(false);
    }

}