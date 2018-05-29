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

import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnConnectionInfo;
import com.gentlebreeze.vpn.sdk.model.VpnDataUsage;
import com.gentlebreeze.vpn.sdk.model.VpnGeoData;
import com.gentlebreeze.vpn.sdk.model.VpnState;
import com.jakewharton.rxbinding.view.RxView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.helpers.ConnectionHelper;
import com.wlvpn.slider.whitelabelvpn.helpers.PreferencesHelper;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectedLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectingLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.SliderLayout;
import com.wlvpn.slider.whitelabelvpn.managers.AccountManager;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.NavigationManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.managers.VpnNotificationManager;
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

    private static final int VPN_PREPARE = 1000;

    @Inject
    public ConnectionHelper connectionHelper;

    @Inject
    public SettingsManager settingsManager;

    @Inject
    public AccountManager accountManager;

    @Inject
    public VpnNotificationManager vpnNotificationManager;

    @Inject
    public NavigationManager navigationManager;

    @Inject
    public ConnectableManager connectableManager;

    private ICallback<VpnState> callbackState;
    private ICallback<VpnDataUsage> dataUsageSubscription;
    private Subscription navigationSubscription;

    private ViewGroup mainContainer;
    private Button connectButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_main);

        mainContainer = findViewById(R.id.activity_main_fl_container);
        connectButton = findViewById(R.id.button_connect);

        if (accountManager.isUserLoggedIn()) {
            if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                presentConnected();
            } else if (isBootLoaded()) {
                bootStart();
            } else {
                presentSlider();
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

    @Override
    public void onPause() {
        super.onPause();
        callbackState.unsubscribe();

        unregisterDataUsage();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == VPN_PREPARE) {
            startVpnConnectionTask();
        } else {
            //Canceled
            disconnectVpn();
        }
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

    public boolean isBootLoaded() {
        return getIntent().getBooleanExtra(Startup.FLAG_INITIAL_BOOT, false);
    }

    public void showLoginFailedToast() {
        Toast.makeText(getApplicationContext(), R.string.login_failed, Toast.LENGTH_LONG).show();
    }

    public void showNoTunDialog(
            DialogInterface.OnClickListener positiveClickListener,
            DialogInterface.OnClickListener negativeClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme);
        final AlertDialog dialog = builder.setTitle(R.string.no_tun_module_found)
                .setMessage(R.string.no_tun_module_found_message)
                .setPositiveButton(R.string.no_tun_module_found_install_button, positiveClickListener)
                .setNegativeButton(R.string.no_tun_module_found_cancel_button, negativeClickListener)
                .create();

        dialog.show();
    }

    public void presentLoginView() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish(); //Avoid adding main activity to back trace on login activity
    }

    /**
     * Vpn service not supported dialog
     */
    public void showVpnServiceNotSupportedDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme);
        builder.setMessage(R.string.vpnservice_is_not_supported_on_this_firmware)
                .setPositiveButton(R.string.disconnect, (dialog, which) -> finish()).create().show();
    }

    public void shouldStartActivityForResult(Intent intent, int result) {
        startActivityForResult(intent, result);
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
            AnimationUtil.fadeInView(nextView, 500, null);

            AnimationUtil.fadeOutView(currentView, 400,
                    new AnimationUtil.AnimationListener() {
                        @Override
                        public void onAnimationEnd(View view) {
                            mainContainer.removeView(currentView);
                        }
                    });
        }
    }

    private void unregisterDataUsage() {
        if (dataUsageSubscription != null) {
            dataUsageSubscription.unsubscribe();
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
                            if (state.contains("auth-failure")
                                    && state.equals("EXITING")) {
                                showLoginFailedToast();
                            } else if (state.equals("NO_TUN")) {
                                showNoTunDialog(
                                        getPositiveNoTunDialogListener(),
                                        getNegativeNoTunDialogListener()
                                );
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
                .subscribe(location -> presentView(location));
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
                dataUsageSubscription = registerDataUsageListener(R.string.notification_vpn_connected_title);
                break;
            default:
                vpnNotificationManager.cancelNotifications();
                unregisterDataUsage();
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
            } catch (ActivityNotFoundException anfe) {
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

    private ICallback<VpnDataUsage> registerDataUsageListener(final @StringRes int notificationTitleRes) {
        return ConsumerVpnApplication.getVpnSdk().listenToConnectionData()
                .subscribe(vpnDataUsage -> {
                    VpnConnectionInfo vpnConnectionInfo
                            = ConsumerVpnApplication.getVpnSdk().getConnectionInfo();
                    vpnNotificationManager.updateConnectionNotification(
                            notificationTitleRes,
                            vpnConnectionInfo,
                            vpnDataUsage
                    );
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to get data usage");
                    return Unit.INSTANCE;
                });
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