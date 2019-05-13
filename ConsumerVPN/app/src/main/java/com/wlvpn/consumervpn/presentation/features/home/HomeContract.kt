package com.wlvpn.consumervpn.presentation.features.home

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface HomeContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onExpiredAccountRetryClick()

        fun onServerTabChangeRequest()

        fun onConnectTabChangeRequest()

    }

    interface View : BaseContract.View {

        fun showExpiredAccountDialog()

        fun progressDialogVisibility(isVisible: Boolean)

        fun showServerTab()

        fun showConnectTab()

    }
}