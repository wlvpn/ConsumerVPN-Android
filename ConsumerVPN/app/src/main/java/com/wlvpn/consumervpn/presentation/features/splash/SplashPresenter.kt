package com.wlvpn.consumervpn.presentation.features.splash

import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.StartupStatus
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import timber.log.Timber

class SplashPresenter(
    private val schedulerProvider: SchedulerProvider,
    private val userAuthenticationService: UserAuthenticationService,
    private val startupStatus: StartupStatus
) : SplashContract.Presenter {

    override var view: SplashContract.View? = null
    private val disposables = CompositeDisposable()
    private var checkAuthDisposable = Disposables.disposed()

    override fun start() {
        startupStatus.isFreshStart = true
        checkAuth()
    }

    override fun cleanUp() {
        super.cleanUp()
        disposables.clear()
    }

    private fun checkAuth() {
        if (checkAuthDisposable.isDisposed) {
            checkAuthDisposable = userAuthenticationService
                .isAuthenticated()
                .defaultSchedulers(schedulerProvider)
                .subscribe({
                    when {
                        it -> view?.navigateToHome()
                        else -> view?.navigateToLogin()
                    }
                }) { Timber.e(it, "Error while checking authorization.") }
                .also { disposables.add(it) }
        }
    }

}