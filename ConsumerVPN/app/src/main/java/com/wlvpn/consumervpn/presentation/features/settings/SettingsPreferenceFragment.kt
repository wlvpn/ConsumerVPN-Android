package com.wlvpn.consumervpn.presentation.features.settings

import android.content.DialogInterface
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.SwitchPreference
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.logout.LogoutDialogFragment
import com.wlvpn.consumervpn.presentation.features.logout.OnLogoutDialogResult
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerPreferenceFragment
import com.wlvpn.consumervpn.presentation.util.isVisible
import javax.inject.Inject

class SettingsPreferenceFragment
    : PresenterOwnerPreferenceFragment<SettingsContract.Presenter>(),
    SettingsContract.View, OnLogoutDialogResult {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private lateinit var autoReconnectPreference: SwitchPreference
    private lateinit var scramblePreference: SwitchPreference
    private lateinit var launchStartupPreference: SwitchPreference
    private lateinit var protocolPreference: ListPreference
    private lateinit var portPreference: ListPreference
    private lateinit var startupConnectPreference: ListPreference
    private lateinit var aboutPreference: Preference

    private var loadingView: View? = null

    companion object {
        val TAG = "${BuildConfig.APPLICATION_ID}:${this::class.java.name}"
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        loadingView = activity?.findViewById(R.id.progressContainerHome)

        (activity as PresenterOwnerActivity<*>).supportActionBar?.title =
            getString(R.string.settings_fragment_label_title)

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupBackButtonHandling()
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.settings, rootKey)

        Injector.INSTANCE.initViewComponent(activity as AppCompatActivity).inject(this)

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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_settings, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item_log_out -> {
                presenter.onLogOutMenuItemClick()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        setupPreferenceListeners()
    }

    override fun showLogoutDialog() {
        activity?.let { activity ->
            val dialogFragment = LogoutDialogFragment.newInstance()
            dialogFragment.onResultCallback = this
            dialogFragment.show(
                activity.supportFragmentManager,
                LogoutDialogFragment.TAG
            )
        }
    }

    override fun onLogoutDialogResponse(resultCode: Int) {
        when (resultCode) {
            DialogInterface.BUTTON_POSITIVE -> presenter.onLogoutClick()

            DialogInterface.BUTTON_NEGATIVE -> return
        }
    }

    override fun toolbarVisibility(isVisible: Boolean) {
        if (isVisible) {
            (activity as PresenterOwnerActivity<*>).supportActionBar?.show()
        } else {
            (activity as PresenterOwnerActivity<*>).supportActionBar?.hide()
        }
    }

    override fun onPause() {
        setLoadingVisibility(false)
        super.onPause()
    }

    override fun setPortPreferenceListOptions(options: List<Port>) {
        val values = options.map { option -> option.portNumber.toString() }.toTypedArray()
        portPreference.entries = values
        portPreference.entryValues = values
    }

    override fun setProtocolPreferenceListOptions(options: List<Protocol>) {
        val keys = options.map { option -> option.name }.toTypedArray()
        val values = options.map { option -> option.protocolName }.toTypedArray()
        protocolPreference.entries = values
        protocolPreference.entryValues = keys
    }

    override fun setStartupConnectPreferenceListOptions(
        options: List<Settings.GeneralConnection.StartupConnectOption>
    ) {
        val keys = options.map { option -> option.name }.toTypedArray()

        val values = options.map { option -> getStartupConnectOptionTitle(option) }.toTypedArray()

        startupConnectPreference.entries = values
        startupConnectPreference.entryValues = keys
    }

    override fun updateSettings(settings: Settings.GeneralConnection) {
        launchStartupPreference.isChecked = settings.launchOnStartup
        autoReconnectPreference.isChecked = settings.autoReconnect
        scramblePreference.isChecked = settings.scramble
        protocolPreference.findValueAndSet(settings.protocol.name)
        startupConnectPreference.findValueAndSet(settings.startupConnectOption.name)
        portPreference.findValueAndSet(settings.port.portNumber.toString())
    }

    override fun setLoadingVisibility(visibility: Boolean) {
        loadingView?.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun showFastestServerNotImplementedMessage() {
        Toast.makeText(
            activity,
            getString(R.string.settings_fastest_in_country_not_implemented),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun showAbout() {
        featureNavigator.navigateToAbout()
    }

    override fun showLogin() {
        featureNavigator.navigateToLogin()
        activity?.finish()
    }

    private fun setupBackButtonHandling() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (view?.isVisible() == true) {
                    event?.action?.let {
                        if (keyCode == KeyEvent.KEYCODE_BACK && it == KeyEvent.ACTION_UP) {
                            featureNavigator.navigateToConnectView()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

    private fun setupPreferenceListeners() {
        launchStartupPreference.setOnPreferenceChangeListener { _, value ->
            presenter.onAppStartupLaunchChanged(value as Boolean)
            false
        }

        startupConnectPreference.setOnPreferenceChangeListener { _, value ->
            presenter.onStartupConnectChanged(
                Settings.GeneralConnection.StartupConnectOption.valueOf(
                    value as String
                )
            )
            false
        }
        scramblePreference.setOnPreferenceChangeListener { _, value ->
            presenter.onScrambleChanged(value as Boolean)
            false
        }
        protocolPreference.setOnPreferenceChangeListener { _, value ->
            presenter.onProtocolChanged(Protocol.valueOf(value as String))
            false
        }
        portPreference.setOnPreferenceChangeListener { _, value ->
            presenter.onPortChanged(Port((value as String).toInt()))
            false
        }
        autoReconnectPreference.setOnPreferenceChangeListener { _, value ->
            presenter.onAutoReconnect(value as Boolean)
            false
        }

        aboutPreference.setOnPreferenceClickListener {
            presenter.onAboutPreferenceClick()
            true
        }
    }

    private fun getStartupConnectOptionTitle(
        startupConnectOption: Settings.GeneralConnection.StartupConnectOption
    ): String {
        return when (startupConnectOption) {
            Settings.GeneralConnection.StartupConnectOption.NONE ->
                getString(R.string.settings_preference_startup_do_not)
            Settings.GeneralConnection.StartupConnectOption.LAST_SERVER ->
                getString(R.string.settings_preference_startup_last_server)
            Settings.GeneralConnection.StartupConnectOption.FASTEST_SERVER ->
                getString(R.string.settings_preference_startup_fastest)
        }
    }
}

private fun ListPreference.findValueAndSet(value: String) {
    val valueIndex = findIndexOfValue(value)
    setValueIndex(if (valueIndex == -1) 0 else valueIndex)
}