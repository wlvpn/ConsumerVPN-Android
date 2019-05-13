package com.wlvpn.consumervpn.presentation.features.home

import com.wlvpn.consumervpn.domain.model.Settings.GeneralConnection.StartupConnectOption
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.presentation.bus.Event
import com.wlvpn.consumervpn.presentation.bus.SinglePipelineBus
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.StartupStatus
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import com.wlvpn.consumervpn.presentation.util.isRunning
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class HomePresenter(
    private val userAuthenticationService: UserAuthenticationService,
    private val userAuthorizationService: UserAuthorizationService,
    private val serversService: ServersService,
    private val schedulerProvider: SchedulerProvider,
    private val settingsService: SettingsService,
    private val connectionEventBus: SinglePipelineBus<Event.ConnectionRequestEvent>,
    private val startupStatus: StartupStatus
) : HomeContract.Presenter {

    override var view: HomeContract.View? = null

    private val disposables = CompositeDisposable()

    private var loginDisposable = Disposables.disposed()

    private var startupCheckDisposable = Disposables.disposed()

    override fun start() {
        // Initializes or onPageSelected current jobs
        userAuthorizationService.scheduleRefreshToken()
        serversService.scheduleRefreshServers()

        // Review if the account is expired and inform the user about it
        if (loginDisposable.isDisposed) {
            disposables.add(
                userAuthorizationService.isAccountExpired()
                    .defaultSchedulers(schedulerProvider)
                    .subscribe({ isAccountExpired ->
                        if (isAccountExpired) {
                            view?.showExpiredAccountDialog()
                        } else if (startupStatus.isFreshStart) {
                            startupConnect()
                        }
                    }, { throwable ->
                        Timber.e(throwable)
                    })
            )
        }

        // If login authentication running make sure to show loading screen
        // while refreshing the account
        if (loginDisposable.isRunning()) {
            view?.progressDialogVisibility(true)
        }
    }

    override fun onExpiredAccountRetryClick() {
        if (loginDisposable.isDisposed) {

            view?.progressDialogVisibility(true)

            loginDisposable =
                userAuthenticationService.getCredentials()
                    .flatMapCompletable { credentials ->
                        userAuthenticationService.authenticate(credentials)
                    }
                    .andThen(userAuthorizationService.isAccountExpired())
                    .defaultSchedulers(schedulerProvider)
                    .subscribe({ isAccountExpired ->
                        view?.progressDialogVisibility(false)

                        if (isAccountExpired) {
                            view?.showExpiredAccountDialog()
                        } else if (startupStatus.isFreshStart) {
                            startupConnect()
                        }
                    }) { throwable ->
                        Timber.e(throwable)
                        view?.progressDialogVisibility(false)
                        // If login fails show dialog again
                        view?.showExpiredAccountDialog()
                    }.also { disposable ->
                        disposables.add(disposable)
                    }
        }
    }

    override fun onServerTabChangeRequest() {
        view?.showServerTab()
    }

    override fun onConnectTabChangeRequest() {
        view?.showConnectTab()
    }

    override fun cleanUp() {
        disposables.clear()
        super.cleanUp()
    }

    private fun startupConnect() {
        //Reset startup status
        startupStatus.reset()
        if (!startupCheckDisposable.isRunning()) {
            startupCheckDisposable = settingsService.getGeneralConnectionSettings()
                //Check first if user has selected startup connect option
                .map { it.startupConnectOption != StartupConnectOption.NONE }
                .filter { shouldAutoConnect -> shouldAutoConnect }
                //Update connection request
                .flatMapCompletable {
                    settingsService.updateConnectionRequestWithStartupSettings()
                        .andThen {
                            //Send connection request event
                            connectionEventBus.post(Event.ConnectionRequestEvent)
                            Completable.complete()
                        }
                }.defaultSchedulers(schedulerProvider)
                .subscribe({ /*No-op*/ })
                { Timber.e(it, "Error with startup connect") }
                .addTo(disposables)
        }
    }
}