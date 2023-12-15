package com.wlvpn.consumervpn.presentation.features.home.connection

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface ConnectionContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onConnectClick()

        fun onDisconnectClick()

        fun onPermissionsDenied()

        fun onPermissionsGranted()

        fun onRefreshRequest()

        fun onLocationClick()

    }

    interface View : BaseContract.View {

        fun showDisconnectedView()

        fun showConnectingView()

        fun showConnectedView()

        fun showVpnPermissionsDialog()

        fun showErrorMessage(message: String)

        fun setDisconnectedLocation(countryName: String, cityName: String)

        fun showConnectedServer(countryName: String)

        fun showConnectedServer(countryName: String, cityName: String)

        fun showPublicIp(publicIpAddress: String)

        fun setDisconnectedToFastest()

        fun setDisconnectedLocationToFastest(countryName: String)

        fun showLogin()

        fun showServersView()

        fun showConnectionErrorMessage()

        fun showNoNetworkMessage()

        fun showUnknownErrorMessage()

        fun toolbarVisibility(isVisible: Boolean)

        fun requestNotificationPermission()

    }

}