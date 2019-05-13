package com.wlvpn.consumervpn.presentation.features.home.connection

import com.wlvpn.consumervpn.data.exception.NetworkNotAvailableException
import com.wlvpn.consumervpn.data.exception.UnknownErrorException
import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.model.Settings.ConnectionRequest.ConnectOption.*
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnNotPreparedException
import com.wlvpn.consumervpn.presentation.bus.Event
import com.wlvpn.consumervpn.presentation.bus.SinglePipelineBus
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import com.wlvpn.consumervpn.presentation.util.isRunning
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import kotlin.properties.Delegates

class ConnectionPresenter(
    private val vpnService: VpnService,
    private val schedulerProvider: SchedulerProvider,
    private val settingsService: SettingsService,
    private val authenticationService: UserAuthenticationService,
    private val connectionEventBus: SinglePipelineBus<Event.ConnectionRequestEvent>
) : ConnectionContract.Presenter {

    private val disposables = CompositeDisposable()

    //TODO we must find a way to handle observable operations, handling disposables like this could be a common boilerplate
    private var getCurrentVpnStateDisposable = Disposables.disposed()

    private var getConnectionSettingsDisposable = Disposables.disposed()

    private var connectDisposable = Disposables.disposed()

    private var disconnectDisposable = Disposables.disposed()

    private var connectionRequestEventDisposable = Disposables.disposed()

    override var view: ConnectionContract.View? = null

    override fun start() {
        getCurrentVpnState()
        listenToVpnStates()
        listenToConnectionRequestEvent()

        // Always refresh the location at the beginning to avoid false positive locations on connect
        fetchGeoInfo()
    }

    override fun onLogoutClick() {
        if (connectDisposable.isRunning()) {
            connectDisposable.dispose()
        }
        vpnService.disconnect()
            .onErrorComplete()
            .andThen(authenticationService.logout())
            .subscribe({
                view?.showLogin()
            }) {
                Timber.e(it, "Error while login out")
            }.addTo(disposables)
    }

    override fun onSettingsClick() {
        view?.showSettings()
    }

    override fun cleanUp() {
        disposables.clear()
        super.cleanUp()
    }

    override fun onConnectClick() {
        connectToVPN()
    }

    override fun onDisconnectClick() {
        view?.showDisconnectedView()
        if (connectDisposable.isRunning()) {
            connectDisposable.dispose()
        }

        if (!disconnectDisposable.isRunning()) {
            disconnectDisposable = vpnService.disconnect()
                .subscribe({
                    updateDisconnectedView()
                }) {}
                .addTo(disposables)
        }
    }

    override fun onPermissionsDenied() {
        updateDisconnectedView()
    }

    override fun onPermissionsGranted() {
        connectToVPN()
    }

    override fun onRefreshRequest() {
        getCurrentVpnState()
    }

    override fun onLocationClick() {
        view?.showServersView()
    }

    private fun getCurrentVpnState() {

        if (getCurrentVpnStateDisposable.isRunning()) {
            getCurrentVpnStateDisposable.dispose()
        }

        getCurrentVpnStateDisposable = vpnService.getCurrentConnectionState()
            .defaultSchedulers(schedulerProvider)
            .subscribe({
                connectionState = it
            }) {
                Timber.e(it, "Error while reading connect state")
            }
            .addTo(disposables)
    }

    private fun updateDisconnectedView() {

        if (getConnectionSettingsDisposable.isRunning()) {
            getConnectionSettingsDisposable.dispose()
        }

        getConnectionSettingsDisposable = settingsService.getConnectionRequestSettings()
            .defaultSchedulers(schedulerProvider)
            .subscribe({
                when (it.connectionOption) {
                    FASTEST_SERVER -> {
                        view?.setDisconnectedToFastest()
                    }

                    FASTEST_IN_LOCATION -> {
                        val location = it.location
                        if (location?.city == null) {
                            view?.setDisconnectedLocationToFastest(it.location!!.country)
                        } else {
                            view?.setDisconnectedLocation(location)
                        }
                    }

                    WITH_SERVER -> {
                        val location = it.server?.location
                        if (location?.city == null) {
                            view?.setDisconnectedToFastest()
                        } else {
                            view?.setDisconnectedLocation(location)
                        }
                    }
                }
                view?.showDisconnectedView()
            }) {
                Timber.e(it, "Unknown error when connecting to the vpn")
            }.addTo(disposables)
    }

    private fun updateConnectedView() {
        vpnService.getConnectedServer()
            .defaultSchedulers(schedulerProvider)
            .subscribe({
                view?.showConnectedServer(it)
                view?.showConnectedView()
            }) {
                Timber.e(it, "Unknown error when obtaining connected server")
            }.addTo(disposables)

    }

    private fun connectToVPN() {
        if (connectionState == ConnectionState.DISCONNECTED) {

            if (connectDisposable.isRunning()) {
                connectDisposable.dispose()
            }

            if (disconnectDisposable.isRunning()) {
                disconnectDisposable.dispose()
            }

            view?.showConnectingView()
            connectDisposable = vpnService.connect()
                .defaultSchedulers(schedulerProvider)
                .subscribe({
                    //no-op listenToConnectStates will change the view
                }) { throwable ->
                    updateDisconnectedView()
                    when (throwable) {

                        is VpnNotPreparedException -> view?.showVpnPermissionsDialog()

                        is NetworkNotAvailableException -> view?.showNoNetworkMessage()

                        is UnknownErrorException ->
                            throwable.message?.let { message ->
                                view?.showErrorMessage(message)
                            } ?: run {
                                view?.showUnknownErrorMessage()
                            }

                        else -> Timber.e(throwable, "Unknown error when connecting to the vpn")
                    }
                }.addTo(disposables)
        }
    }

    private fun listenToVpnStates() {
        vpnService.listenToConnectState()
            .distinctUntilChanged()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe({
                connectionState = it
            }, {
                Timber.e(it, "Error while reading connect state")
            }).addTo(disposables)
    }

    private fun listenToConnectionRequestEvent() {
        // Subscribe to connection events
        connectionRequestEventDisposable = connectionEventBus
            .asObservable()
            .subscribeOn(schedulerProvider.io())
            .observeOn(schedulerProvider.ui())
            .subscribe {
                connectToVPN()
            }
    }

    private fun fetchGeoInfo() {
        disposables.add(
            vpnService.fetchGeoInfo()
                .subscribe({ }, //do nothing, just fetches and updates the info
                    { t: Throwable? -> Timber.e("Error updating geoInfo $t") })
        )
    }

    private var connectionState: ConnectionState by
    Delegates.observable(ConnectionState.UNKNOWN) { _, _, newState ->
        when (newState) {
            ConnectionState.CONNECTED -> updateConnectedView()

            ConnectionState.CONNECTING -> view?.showConnectingView()

            ConnectionState.DISCONNECTED -> updateDisconnectedView()

            ConnectionState.UNKNOWN -> { /* no-op */
            }
        }
    }
}