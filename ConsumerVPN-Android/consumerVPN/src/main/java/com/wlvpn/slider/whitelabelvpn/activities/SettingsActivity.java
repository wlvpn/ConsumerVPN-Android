package com.wlvpn.slider.whitelabelvpn.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.jakewharton.rxbinding.view.RxView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.helpers.LocaleHelper;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.models.Country;
import com.wlvpn.slider.whitelabelvpn.settings.ConnectionStartupPref;
import com.wlvpn.slider.whitelabelvpn.settings.PortPref;
import com.wlvpn.slider.whitelabelvpn.settings.ProtocolPref;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import rx.android.schedulers.AndroidSchedulers;

public class SettingsActivity extends BaseActivity {

    @Inject
    public SettingsManager settingsManager;

    private RadioGroup optionConnect;
    private ConstraintLayout optionProtocol;
    private ConstraintLayout optionPort;
    private TextView countryLabel;
    private CheckBox autoReconnect;
    private CheckBox scramble;
    private TextView protocolValue;
    private TextView portValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_settings);

        optionConnect = findViewById(R.id.startup_connect_group);
        optionProtocol = findViewById(R.id.connection_protocol);
        optionPort = findViewById(R.id.connection_port);
        countryLabel = findViewById(R.id.startup_fastest_country_label);
        autoReconnect = findViewById(R.id.connection_auto_reconnect);
        scramble = findViewById(R.id.connection_scramble);
        protocolValue = findViewById(R.id.connection_protocol_value);
        portValue = findViewById(R.id.connection_port_value);

        presentAutoConnectSetting(settingsManager.getAutoReconnectPref());
        presentScrambleSetting(settingsManager.getScramblePref());
        presentPortSetting(settingsManager.getPortPref());
        presentProtocolSetting(settingsManager.getProtocolPref());

        setConnectOnChangeListener(getStartupCheckListener());
        setReconnectOnChangeListener(getAutoReconnectCheckListener());
        setScrambleOnChangeListener(getScrambleCheckListener());
        initStartupSetting();
    }

    @Override
    protected void onResume() {
        super.onResume();

        initListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CountryListActivity.SELECT_COUNTRY:
                    updateStartupPreference(ConnectionStartupPref.CONNECT_TO_FASTEST_IN_COUNTRY);
                    Country country = data.getParcelableExtra(CountryListActivity.COUNTRY_SELECTION);
                    updateCountry(country);
                    initStartupSetting();
                    break;
                default:
                    super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    private void initListeners() {
        // Port Listener
        getMainSubscription().add(RxView.clicks(optionPort)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                        presentDisconnectWarningDialog();
                    } else {
                        presentModalWithOptions(R.array.preference_port_types, getPortDialogListener());
                    }
                }));

        getMainSubscription().add(RxView.clicks(optionProtocol)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> {
                    if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                        presentDisconnectWarningDialog();
                    } else {
                        presentModalWithOptions(R.array.preference_protocol_types, getProtocolDialogListener());
                    }
                }));

        getMainSubscription().add(RxView.clicks(countryLabel)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> presentCountrySelectionActivity()));
    }

    public void presentCountrySelectionActivity() {
        Intent intent = new Intent(this, CountryListActivity.class);
        startActivityForResult(intent, CountryListActivity.SELECT_COUNTRY);
    }

    public void presentModalWithOptions(@ArrayRes int options, DialogInterface.OnClickListener handler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme);
        builder.setItems(options, handler);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void presentStartupSetting(ConnectionStartupPref pref) {
        int prefOption = pref.getConnectionStartUpPref();
        RadioButton btn = (RadioButton) optionConnect.getChildAt(prefOption);
        btn.setChecked(true);
    }

    public void presentAutoConnectSetting(boolean pref) {
        autoReconnect.setChecked(pref);
    }

    public void presentScrambleSetting(boolean pref) {
        scramble.setChecked(pref);
    }

    public void presentProtocolSetting(ProtocolPref pref) {
        String[] protocols = getResources().getStringArray(R.array.preference_protocol_types);
        protocolValue.setText(protocols[pref.getProtocolPref()]);
    }

    public void presentPortSetting(PortPref pref) {
        String[] ports = getResources().getStringArray(R.array.preference_port_types);
        portValue.setText(ports[pref.getPortPref()]);
    }

    public void showCountryLabel(String country) {
        countryLabel.setText(country);
        countryLabel.setFocusable(true);
        countryLabel.setClickable(true);
        countryLabel.setVisibility(View.VISIBLE);
    }

    public void hideCountryLabel() {
        countryLabel.setClickable(false);
        countryLabel.setFocusable(false);
        countryLabel.setVisibility(View.INVISIBLE);
    }

    public void setConnectOnChangeListener(RadioGroup.OnCheckedChangeListener listener) {
        optionConnect.setOnCheckedChangeListener(listener);
    }

    public void setReconnectOnChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        autoReconnect.setOnCheckedChangeListener(listener);
    }

    public void setScrambleOnChangeListener(CompoundButton.OnCheckedChangeListener listener) {
        scramble.setOnCheckedChangeListener(listener);
    }

    public void presentDisconnectWarningDialog() {
        new AlertDialog.Builder(this, R.style.WLVPNAlertDialogTheme)
                .setMessage(R.string.settings_disconnect_alert_message)
                .setTitle(R.string.settings_disconnect_alert_title)
                .setPositiveButton(R.string.settings_disconnect_alert_positive, getDisconnectWarningDialogListener())
                .setNegativeButton(R.string.settings_disconnect_alert_negative, null)
                .create()
                .show();
    }

    private void initStartupSetting() {
        presentStartupSetting(settingsManager.getConnectionStartupPref());
        setStartupCountryFromPreference();
    }

    private void updateCountry(Country country) {
        settingsManager.updateStartupCountry(country.getCountryCode());
    }

    private void updateStartupPreference(@ConnectionStartupPref.ConnectionStartup int connectionStartupPref) {
        settingsManager.updateStartupConnectionPref(new ConnectionStartupPref(connectionStartupPref));
    }

    /**
     * Sets the Startup country label text
     */
    private void setStartupCountryFromPreference() {
        String connectionStartupCountry = settingsManager.getStartupCountry();
        ConnectionStartupPref startupPref = settingsManager.getConnectionStartupPref();
        if (connectionStartupCountry != null) {
            showCountryLabel(LocaleHelper.getCountryByCode(connectionStartupCountry));
        }
        if (startupPref.getConnectionStartUpPref() !=
                ConnectionStartupPref.CONNECT_TO_FASTEST_IN_COUNTRY) {

            hideCountryLabel();
        }
    }

    /**
     * Get the positive action for the disconnect warning dialog
     *
     * @return DialogInterface.OnClickListener
     */
    private DialogInterface.OnClickListener getDisconnectWarningDialogListener() {
        return (dialogInterface, i) ->
                ConsumerVpnApplication.getVpnSdk()
                        .disconnect()
                        .subscribe();
    }

    /**
     * Get dialog handler for the Port options
     *
     * @return DialogInterface.OnClickListener
     */
    private DialogInterface.OnClickListener getPortDialogListener() {
        return (dialogInterface, i) -> {
            PortPref pref = new PortPref(i);
            settingsManager.updatePortPref(pref);
            presentPortSetting(pref);
        };
    }

    /**
     * Returns the dialog handler for Protocol options
     *
     * @return DialogInterface.OnClickListener
     */
    private DialogInterface.OnClickListener getProtocolDialogListener() {
        return (dialogInterface, i) -> {
            ProtocolPref pref = new ProtocolPref(i);
            settingsManager.updateProtocolPref(pref);
            presentProtocolSetting(pref);
        };
    }

    /**
     * Get Startup Check listener
     *
     * @return RadioGroup.OnCheckedChangeListener
     */
    private RadioGroup.OnCheckedChangeListener getStartupCheckListener() {
        return (radioGroup, i) -> {
            RadioButton radioButton = radioGroup.findViewById(i);

            @ConnectionStartupPref.ConnectionStartup int radioIndex =
                    radioGroup.indexOfChild(radioButton);

            hideCountryLabel();

            if (radioIndex == ConnectionStartupPref.CONNECT_TO_FASTEST_IN_COUNTRY) {
                String country = settingsManager.getStartupCountry();
                if (country != null) {
                    updateStartupPreference(ConnectionStartupPref.CONNECT_TO_FASTEST_IN_COUNTRY);

                    showCountryLabel(LocaleHelper.getCountryByCode(country));
                } else {
                    presentCountrySelectionActivity();
                }
            } else {
                updateStartupPreference(radioIndex);
            }
            initStartupSetting();
        };
    }

    /**
     * Get Auto Reconnect Click listener
     *
     * @return CompoundButton.OnCheckedChangeListener
     */
    private CompoundButton.OnCheckedChangeListener getAutoReconnectCheckListener() {
        return (compoundButton, b) -> {
            if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                presentDisconnectWarningDialog();
                compoundButton.setChecked(!b);
            } else {
                settingsManager.updateAutoReconnectPref(b);
            }
        };
    }

    /**
     * Get Scramble Click listener
     *
     * @return CompoundButton.OnCheckedChangeListener
     */
    private CompoundButton.OnCheckedChangeListener getScrambleCheckListener() {
        return (compoundButton, b) -> {
            if (ConsumerVpnApplication.getVpnSdk().isConnected()) {
                presentDisconnectWarningDialog();
                compoundButton.setChecked(!b);
            } else {
                // TODO: Add logic for scramble
                settingsManager.updateScramblePref(b);
            }
        };
    }
}
