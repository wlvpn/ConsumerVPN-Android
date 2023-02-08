package com.wlvpn.consumervpn.presentation.features.settings

import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.model.Settings.GeneralConnection.StartupConnectOption
import com.wlvpn.consumervpn.domain.model.VpnProtocol
import com.wlvpn.consumervpn.domain.model.VpnProtocol.IKEV2
import com.wlvpn.consumervpn.domain.model.VpnProtocol.OPENVPN
import com.wlvpn.consumervpn.domain.model.VpnProtocol.WIREGUARD
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import com.wlvpn.consumervpn.presentation.util.isRunning
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import kotlin.properties.Delegates


class SettingsPresenter(
    private val settingsService: SettingsService,
    private val vpnService: VpnService,
    private val authenticationService: UserAuthenticationService,
    private val schedulerProvider: SchedulerProvider
) : SettingsContract.Presenter {

    override var view: SettingsContract.View? = null

    private var getSettingsDisposable = Disposables.disposed()

    private var updateSettingsDisposable = Disposables.disposed()

    private var disposables = CompositeDisposable()

    private var current: Settings.GeneralConnection by
    Delegates.observable(Settings.GeneralConnection()) { _, _, updatedSettings ->
        view?.setPortPreferenceListOptions(updatedSettings.availablePorts)
        view?.updateSettings(updatedSettings)
        when (updatedSettings.vpnProtocol) {
            OPENVPN -> view?.showOpenVpnPreferences()
            IKEV2 -> view?.showIkev2Preferences()
            WIREGUARD -> view?.showWireGuardPreferences()
        }
    }

    override fun start() {
        //Set initial list options
        view?.setStartupConnectPreferenceListOptions(StartupConnectOption.values().asList())
        view?.setVpnProtocolPreferenceListOptions(VpnProtocol.values().asList())
        view?.setProtocolPreferenceListOptions(Protocol.values().asList())
        view?.setPortPreferenceListOptions(current.availablePorts)
        view?.toolbarVisibility(true)

        //If any task is running show loading
        view?.setLoadingVisibility(
            getSettingsDisposable.isRunning() ||
                    updateSettingsDisposable.isRunning()
        )

        //Start show settings task
        runShowStoredSettingsTask(onStarted = { view?.setLoadingVisibility(true) },
            onFinished = { view?.setLoadingVisibility(false) })
    }

    override fun cleanUp() {
        disposables.clear()
        super.cleanUp()
    }

    override fun onStartupConnectChanged(startupConnectOption: StartupConnectOption) {
        current.startupConnectOption = startupConnectOption
        runNotifySettingsUpdatedTask()
    }

    override fun onScrambleChanged(scramble: Boolean) {
        current.scramble = scramble
        runNotifySettingsUpdatedTask()
    }

    override fun onVpnProtocolChanged(vpnProtocol: VpnProtocol) {
        current.vpnProtocol = vpnProtocol
        runNotifySettingsUpdatedTask()
    }

    override fun onProtocolChanged(protocol: Protocol) {
        current.protocol = protocol
        runNotifySettingsUpdatedTask()
    }

    override fun onPortChanged(port: Port) {
        current.port = port
        runNotifySettingsUpdatedTask()
    }

    override fun onAutoReconnect(reconnect: Boolean) {
        current.autoReconnect = reconnect
        runNotifySettingsUpdatedTask()
    }

    override fun onKillSwitchPreferenceClick() {
        view?.showKillSwitchDialog()
    }

    override fun onSupportPreferenceClick() {
        view?.showSupport()
    }

    override fun onAboutPreferenceClick() {
        view?.showAbout()
    }

    override fun onLogOutMenuItemClick() {
        view?.showLogoutDialog()
    }

    override fun onLogoutClick() {
        vpnService.disconnect()
            .onErrorComplete()
            .andThen(authenticationService.logout())
            .subscribe({
                view?.showLogin()
            }) {
                Timber.e(it, "Error while login out")
            }.addTo(disposables)
    }

    private fun runShowStoredSettingsTask(
        onStarted: () -> Unit = {},
        onFinished: () -> Unit = {}
    ) {

        //If this task or runNotifySettingsUpdatedTask are running, don't run this again
        if (!getSettingsDisposable.isRunning() && !updateSettingsDisposable.isRunning()) {
            getSettingsDisposable = settingsService.getGeneralConnectionSettings()
                .defaultSchedulers(schedulerProvider)
                .doOnSubscribe { onStarted() }
                .doFinally { onFinished() }
                .subscribe({ current = it }) {
                    Timber.e(it, "Error getting settings")
                }
                .addTo(disposables)
        }
    }

    private fun runNotifySettingsUpdatedTask(
        onStarted: () -> Unit = {},
        onFinished: () -> Unit = {}
    ) {
        //If this task is running, kill it and start it again
        if (updateSettingsDisposable.isRunning()) {
            updateSettingsDisposable.dispose()
        }
        updateSettingsDisposable = settingsService.updateGeneralSettings(current)
            .andThen(settingsService.getGeneralConnectionSettings())
            .defaultSchedulers(schedulerProvider)
            .doOnSubscribe { onStarted() }
            .doFinally { onFinished() }
            .subscribe({ current = it }) {
                Timber.e(it, "Error updating settings")
            }
            .addTo(disposables)
    }
}