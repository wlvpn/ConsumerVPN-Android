package com.wlvpn.consumervpn.presentation.features.home.connection

import com.wlvpn.consumervpn.data.failure.NetworkNotAvailableFailure
import com.wlvpn.consumervpn.data.failure.UnknownErrorException
import com.wlvpn.consumervpn.data.model.CityAndCountryServerLocation
import com.wlvpn.consumervpn.data.model.CountryServerLocation
import com.wlvpn.consumervpn.data.model.FastestServerLocation
import com.wlvpn.consumervpn.domain.model.ConnectionState
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.domain.service.vpn.exception.VpnNotPreparedFailure
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
        view?.toolbarVisibility(false)
        getCurrentVpnState()
        listenToVpnStates()
        listenToConnectionRequestEvent()
        // Always refresh the location at the beginning to avoid false positive locations on connect
        fetchGeoInfo()
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
                when (val location = it.location) {
                    is FastestServerLocation -> view?.setDisconnectedToFastest()
                    is CityAndCountryServerLocation -> {
                        view?.setDisconnectedLocation(location.country, location.city)
                    }
                    is CountryServerLocation -> view?.setDisconnectedLocationToFastest(location.country)
                }
                view?.showDisconnectedView()
            }) {
                Timber.e(it, "Error showing state")
            }.addTo(disposables)
    }

    private fun updateConnectedView() {
        vpnService.getConnectedServer()
            .defaultSchedulers(schedulerProvider)
            .subscribe({
                when (val serverLocation = it.location) {
                    is CityAndCountryServerLocation -> {
                        view?.showConnectedServer(
                            it.host.ipAddress,
                            serverLocation.country,
                            serverLocation.city
                        )
                    }
                    is CountryServerLocation -> {
                        view?.showConnectedServer(
                            it.host.ipAddress,
                            serverLocation.country
                        )
                    }
                }
                view?.showConnectedView()
            }) {
                Timber.e(it, "Unknown error when obtaining connected server")
            }.addTo(disposables)

    }

    private fun connectToVPN() {
        if (connectionState == ConnectionState.DISCONNECTED || connectionState == ConnectionState.DISCONNECTED_ERROR) {

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

                        is VpnNotPreparedFailure -> view?.showVpnPermissionsDialog()

                        is NetworkNotAvailableFailure -> view?.showNoNetworkMessage()

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
                .defaultSchedulers(schedulerProvider)
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

            ConnectionState.DISCONNECTED_ERROR -> {
                view?.showConnectionErrorMessage()
                updateDisconnectedView()
            }

            ConnectionState.UNKNOWN -> { /* no-op */
            }
        }
    }
}