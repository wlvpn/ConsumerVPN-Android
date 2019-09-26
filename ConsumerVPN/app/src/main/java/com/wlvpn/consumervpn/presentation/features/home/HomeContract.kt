package com.wlvpn.consumervpn.presentation.features.home

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface HomeContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onExpiredAccountRetryClick()

        fun onConnectFragmentChangeRequest()

        fun onServerFragmentChangeRequest()

        fun onSettingsFragmentChangeRequest()

    }

    interface View : BaseContract.View {

        fun showExpiredAccountDialog()

        fun progressDialogVisibility(isVisible: Boolean)

        fun showConnectionView()

        fun showServersView()

        fun showSettingsView()

    }
}