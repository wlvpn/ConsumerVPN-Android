package com.wlvpn.consumervpn.presentation.features.home.connection

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface ConnectionContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onLogoutClick()

        fun onSettingsClick()

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

        fun showSettings()

        fun showErrorMessage(message: String)

        fun setDisconnectedLocation(countryName: String, cityName: String)

        fun showConnectedServer(hostIpAddress: String, countryName: String)

        fun showConnectedServer(hostIpAddress: String, countryName: String, cityName: String)

        fun setDisconnectedToFastest()

        fun setDisconnectedLocationToFastest(countryName: String)

        fun showLogin()

        fun showServersView()

        fun showNoNetworkMessage()

        fun showUnknownErrorMessage()

    }

}