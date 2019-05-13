package com.wlvpn.consumervpn.presentation.features.login

import com.wlvpn.consumervpn.data.exception.NetworkNotAvailableException
import com.wlvpn.consumervpn.data.exception.UnknownErrorException
import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import com.wlvpn.consumervpn.presentation.util.isRunning
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables

class LoginPresenter(
    private val userAuthenticationService: UserAuthenticationService,
    private val schedulerProvider: SchedulerProvider
) : LoginContract.Presenter {

    override var view: LoginContract.View? = null
    private val disposables = CompositeDisposable()
    private var loginDisposable = Disposables.disposed()

    override fun start() {
        //If login is still running, show progress
        view?.progressDialogVisibility(isVisible = loginDisposable.isRunning())
    }

    override fun cleanUp() {
        disposables.clear()
        super.cleanUp()
    }

    override fun onLoginClick(username: String, password: String) {
        view?.hideKeyboard()

        if (username.isNotEmpty() && password.isNotEmpty()) {
            view?.dismissErrorMessage()

            if (loginDisposable.isDisposed) {

                view?.progressDialogVisibility(true)

                val credentials = Credentials(username, password)

                loginDisposable = userAuthenticationService.authenticate(credentials)
                    .defaultSchedulers(schedulerProvider)
                    .subscribe({
                        view?.showHome()
                    }) {
                        view?.progressDialogVisibility(false)
                        when (it) {
                            is NetworkNotAvailableException -> view?.showNoNetworkMessage()

                            is UnknownErrorException ->
                                it.message?.let { message ->
                                    view?.showErrorMessage(message)
                                } ?: run {
                                    view?.showUnknownErrorMessage()
                                }

                            else -> view?.showUnknownErrorMessage()
                        }

                    }.also { disposables.add(it) }
            }
        } else {
            view?.showEmptyUserOrPasswordMessage()
        }
    }

    override fun onForgotPasswordClick() {
        view?.showForgotPassword()
    }

    override fun onOpenForgotPasswordNotSupported() {
        view?.showExternalLinksNotSupportedMessage()
    }

    override fun onSignUpClick() {
        view?.showSignUp()
    }

    override fun onOpenSignUpdNotSupported() {
        view?.showExternalLinksNotSupportedMessage()
    }
}