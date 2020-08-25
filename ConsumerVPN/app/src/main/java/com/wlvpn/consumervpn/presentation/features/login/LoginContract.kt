package com.wlvpn.consumervpn.presentation.features.login

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface LoginContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onLoginClick(username: String, password: String)

        fun onForgotPasswordClick()

        fun onOpenForgotPasswordNotSupported()

        fun onSignUpClick()

        fun onOpenSignUpdNotSupported()
    }

    interface View : BaseContract.View {

        fun showForgotPassword()

        fun showExternalLinksNotSupportedMessage()

        fun showEmptyUserOrPasswordMessage()

        fun progressDialogVisibility(isVisible: Boolean)

        fun showErrorMessage(errorMessage: String)

        fun dismissErrorMessage()

        fun showHome()

        fun showSignUp()

        fun hideKeyboard()

        fun showInvalidCredentialsMessage()

        fun showNoNetworkMessage()

        fun showUnknownErrorMessage()
    }

}