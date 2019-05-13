package com.wlvpn.consumervpn.presentation.features.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.preference.ListPreference
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.model.Settings.GeneralConnection.StartupConnectOption
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import kotlinx.android.synthetic.main.activity_settings.*
import javax.inject.Inject

private const val SETTINGS_FRAGMENT_TAG = "SETTINGS_FRAGMENT_TAG"

class SettingsActivity :
    PresenterOwnerActivity<SettingsContract.Presenter>(), SettingsContract.View {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private var settingsFragment: SettingsPreferenceFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        setupSettingsFragment()
    }

    override fun onResume() {
        super.onResume()
        setupPreferenceListeners()
    }

    override fun onDestroy() {
        settingsFragment = null
        super.onDestroy()
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun setPortPreferenceListOptions(options: List<Port>) {
        val values = options.map { it.portNumber.toString() }.toTypedArray()
        settingsFragment?.portPreference?.entries = values
        settingsFragment?.portPreference?.entryValues = values
    }

    override fun setProtocolPreferenceListOptions(options: List<Protocol>) {
        val keys = options.map { it.name }.toTypedArray()
        val values = options.map { it.protocolName }.toTypedArray()
        settingsFragment?.protocolPreference?.entries = values
        settingsFragment?.protocolPreference?.entryValues = keys
    }

    override fun setStartupConnectPreferenceListOptions(options: List<StartupConnectOption>) {
        val keys = options.map { it.name }.toTypedArray()
        val values = options.map { getStartupConnectOptionTitle(it) }.toTypedArray()
        settingsFragment?.startupConnectPreference?.entries = values
        settingsFragment?.startupConnectPreference?.entryValues = keys
    }

    override fun updateSettings(settings: Settings.GeneralConnection) {
        settingsFragment?.launchStartupPreference?.isChecked = settings.launchOnStartup
        settingsFragment?.autoReconnectPreference?.isChecked = settings.autoReconnect
        settingsFragment?.scramblePreference?.isChecked = settings.scramble
        settingsFragment?.protocolPreference
            ?.findValueAndSet(settings.protocol.name)
        settingsFragment?.startupConnectPreference
            ?.findValueAndSet(settings.startupConnectOption.name)
        settingsFragment?.portPreference
            ?.findValueAndSet(settings.port.portNumber.toString())
    }

    override fun setLoadingVisibility(visibility: Boolean) {
        loadingView.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun showFastestServerNotImplementedMessage() {
        Toast.makeText(
            this,
            getString(R.string.settings_fastest_in_country_not_implemented),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showAbout() {
        featureNavigator.navigateToAbout()
    }


    private fun setupSettingsFragment() {
        val restoredFragment = supportFragmentManager.findFragmentByTag(SETTINGS_FRAGMENT_TAG)
                as? SettingsPreferenceFragment
        if (restoredFragment == null) {
            settingsFragment = SettingsPreferenceFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, settingsFragment!!, SETTINGS_FRAGMENT_TAG)
                .commit()
        } else {
            settingsFragment = restoredFragment
        }
    }

    private fun setupPreferenceListeners() {
        settingsFragment?.launchStartupPreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onAppStartupLaunchChanged(value as Boolean)
            false
        }

        settingsFragment?.startupConnectPreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onStartupConnectChanged(StartupConnectOption.valueOf(value as String))
            false
        }
        settingsFragment?.scramblePreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onScrambleChanged(value as Boolean)
            false
        }
        settingsFragment?.protocolPreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onProtocolChanged(Protocol.valueOf(value as String))
            false
        }
        settingsFragment?.portPreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onPortChanged(Port((value as String).toInt()))
            false
        }
        settingsFragment?.autoReconnectPreference?.setOnPreferenceChangeListener { _, value ->
            presenter.onAutoReconnect(value as Boolean)
            false
        }

        settingsFragment?.aboutPreference?.setOnPreferenceClickListener {
            presenter.onAboutPreferenceClick()
            true
        }
    }

    private fun getStartupConnectOptionTitle(startupConnectOption: StartupConnectOption): String {
        return when (startupConnectOption) {
            StartupConnectOption.NONE ->
                getString(R.string.settings_preference_startup_do_not)
            StartupConnectOption.LAST_SERVER ->
                getString(R.string.settings_preference_startup_last_server)
            StartupConnectOption.FASTEST_SERVER ->
                getString(R.string.settings_preference_startup_fastest)
            StartupConnectOption.FASTEST_IN_LOCATION ->
                getString(R.string.settings_preference_startup_fastest_in_country)
        }
    }

}

private fun ListPreference.findValueAndSet(value: String) {
    val valueIndex = findIndexOfValue(value)
    setValueIndex(if (valueIndex == -1) 0 else valueIndex)
}
