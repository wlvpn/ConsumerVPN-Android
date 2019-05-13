package com.wlvpn.consumervpn.presentation.features.settings

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.wlvpn.consumervpn.R

class SettingsPreferenceFragment : PreferenceFragmentCompat() {

    lateinit var autoReconnectPreference: SwitchPreference
    lateinit var scramblePreference: SwitchPreference
    lateinit var launchStartupPreference: SwitchPreference
    lateinit var protocolPreference: ListPreference
    lateinit var portPreference: ListPreference
    lateinit var startupConnectPreference: ListPreference
    lateinit var aboutPreference: Preference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        autoReconnectPreference = preferenceManager
            .findPreference(getString(R.string.preference_reconnect_key))
                as SwitchPreference

        scramblePreference = preferenceManager
            .findPreference(getString(R.string.preference_scramble_key))
                as SwitchPreference

        launchStartupPreference = preferenceManager
            .findPreference(getString(R.string.preference_launch_startup_key))
                as SwitchPreference

        protocolPreference = preferenceManager
            .findPreference(getString(R.string.preference_protocol_key))
                as ListPreference

        portPreference = preferenceManager
            .findPreference(getString(R.string.preference_port_key))
                as ListPreference

        startupConnectPreference = preferenceManager
            .findPreference(getString(R.string.preference_auto_connect_key))
                as ListPreference

        aboutPreference = preferenceManager
            .findPreference(getString(R.string.preference_about_key))
                as Preference

        preferenceScreen.isPersistent = false
        autoReconnectPreference.isPersistent = false
        scramblePreference.isPersistent = false
        protocolPreference.isPersistent = false
        portPreference.isPersistent = false
        startupConnectPreference.isPersistent = false
        launchStartupPreference.isPersistent = false
    }

}
