package com.wlvpn.slider.whitelabelvpn.managers;


import android.content.Context;
import android.content.SharedPreferences;

import com.wlvpn.slider.whitelabelvpn.settings.CipherPref;
import com.wlvpn.slider.whitelabelvpn.settings.ConnectionStartupPref;
import com.wlvpn.slider.whitelabelvpn.settings.PortPref;
import com.wlvpn.slider.whitelabelvpn.settings.ProtocolPref;
import com.wlvpn.slider.whitelabelvpn.settings.SortPref;

import javax.inject.Inject;

public class SettingsManager {

    private static final String SHARED_PREFERENCES_NAMESPACE = "Settings-Area";
    private static final String SETTINGS_STARTUP = "settings:startup";
    private static final String SETTINGS_STARTUP_COUNTRY = "settings:startup_country";
    private static final String SETTINGS_PORT = "settings:port";
    private static final String SETTINGS_PROTOCOL = "settings:protocol";
    private static final String SETTINGS_CIPHER = "settings:cipher";
    private static final String SETTINGS_RECONNECT = "settings:reconnect";
    private static final String SETTINGS_SORT_MODE = "settings:sort";
    private static final String SETTINGS_SCRAMBLE = "settings:scramble";

    private final SharedPreferences sharedPreferences;

    @Inject
    public SettingsManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAMESPACE, Context.MODE_PRIVATE);

    }

    /**
     * Get Connection Startup Preference
     *
     * @return ConnectionStartupPref
     */
    public ConnectionStartupPref getConnectionStartupPref() {
        @ConnectionStartupPref.ConnectionStartup int pref = sharedPreferences.getInt(
                SETTINGS_STARTUP,
                ConnectionStartupPref.DO_NOT_AUTOMATICALLY_CONNECT
        );
        return new ConnectionStartupPref(pref);
    }

    /**
     * Get Port Preference
     *
     * @return PortPref
     */
    public PortPref getPortPref() {
        @PortPref.Port int pref = sharedPreferences.getInt(
                SETTINGS_PORT,
                PortPref.PORT_443
        );
        return new PortPref(pref);
    }

    /**
     * Get Protocol Preference
     *
     * @return ProtocolPref
     */
    public ProtocolPref getProtocolPref() {
        @ProtocolPref.Protocol int pref = sharedPreferences.getInt(
                SETTINGS_PROTOCOL,
                ProtocolPref.UDP
        );
        return new ProtocolPref(pref);
    }

    /**
     * Get Auto Reconnect Preference
     *
     * @return boolean
     */
    public boolean getAutoReconnectPref() {
        return sharedPreferences.getBoolean(
                SETTINGS_RECONNECT,
                true
        );
    }

    /**
     * Get Scramble Preference
     *
     * @return boolean
     */
    public boolean getScramblePref() {
        return sharedPreferences.getBoolean(
                SETTINGS_SCRAMBLE,
                false
        );
    }

    /**
     * Get the Cipher Preference
     *
     * @return CipherPref
     */
    public CipherPref getCipherPref() {
        @CipherPref.Cipher int pref = sharedPreferences.getInt(
                SETTINGS_CIPHER,
                CipherPref.CIPHER_AES128
        );
        return new CipherPref(pref);
    }

    /**
     * Get Sort Preference
     *
     * @return SortPref
     */
    public SortPref getSortPref() {
        @SortPref.ServerSort int pref = sharedPreferences.getInt(
                SETTINGS_SORT_MODE,
                SortPref.SORT_COUNTRY
        );
        return new SortPref(pref);
    }

    /**
     * Get Startup Country preference
     *
     * @return String
     */
    public String getStartupCountry() {
        return sharedPreferences.getString(
                SETTINGS_STARTUP_COUNTRY,
                null
        );
    }


    /**
     * Update Startup Connection Preference
     *
     * @param pref ConnectionStartupPref preference
     */
    public void updateStartupConnectionPref(ConnectionStartupPref pref) {
        int prefOption = pref.getConnectionStartUpPref();
        sharedPreferences.edit()
                .putInt(SETTINGS_STARTUP, prefOption)
                .apply();
    }

    /**
     * Update Auto Reconnect Preference
     *
     * @param pref boolean preference
     */
    public void updateAutoReconnectPref(boolean pref) {
        sharedPreferences.edit()
                .putBoolean(SETTINGS_RECONNECT, pref)
                .apply();
    }

    /**
     * Update Scramble Preference
     *
     * @param pref boolean preference
     */
    public void updateScramblePref(boolean pref) {
        sharedPreferences.edit()
                .putBoolean(SETTINGS_SCRAMBLE, pref)
                .apply();
    }

    /**
     * Update Protocol Preference
     *
     * @param pref Protocol preference
     */
    public void updateProtocolPref(ProtocolPref pref) {
        sharedPreferences.edit()
                .putInt(SETTINGS_PROTOCOL, pref.getProtocolPref())
                .apply();
    }

    /**
     * Update Port Preference
     *
     * @param pref PortPref preference
     */
    public void updatePortPref(PortPref pref) {
        sharedPreferences.edit()
                .putInt(SETTINGS_PORT, pref.getPortPref())
                .apply();
    }

    /**
     * Update Sort Pref
     *
     * @param pref SortPref preference
     */
    public void updateSortPref(SortPref pref) {
        sharedPreferences.edit()
                .putInt(SETTINGS_SORT_MODE, pref.getSortMode())
                .apply();
    }

    /**
     * Update Cipher preference
     *
     * @param pref CipherPref preference
     */
    public void updateCipher(CipherPref pref) {
        sharedPreferences.edit()
                .putInt(SETTINGS_CIPHER, pref.getCipher())
                .apply();
    }

    /**
     * Update the startup country preference
     *
     * @param country country string
     */
    public void updateStartupCountry(String country) {
        sharedPreferences.edit()
                .putString(SETTINGS_STARTUP_COUNTRY, country)
                .apply();
    }

    /**
     * Clear preferences and revert back to defaults
     */
    public void clear() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}
