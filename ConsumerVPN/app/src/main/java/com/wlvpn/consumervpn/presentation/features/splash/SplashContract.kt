package com.wlvpn.consumervpn.presentation.features.splash

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface SplashContract {

    interface Presenter : BaseContract.Presenter<View>

    interface View : BaseContract.View {

        /**
         * Navigates view to Login feature
         */
        fun navigateToLogin()

        /**
         * Navigates view to Home feature
         */
        fun navigateToHome()

    }
}