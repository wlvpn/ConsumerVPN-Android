package com.wlvpn.consumervpn.presentation.features.home.connection

import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
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

        fun setDisconnectedLocation(location: ServerLocation)

        fun showConnectedServer(server: Server)

        fun setDisconnectedToFastest()

        fun setDisconnectedLocationToFastest(countryName: String)

        fun showLogin()

        fun showServersView()

        fun showNoNetworkMessage()

        fun showUnknownErrorMessage()

    }

}