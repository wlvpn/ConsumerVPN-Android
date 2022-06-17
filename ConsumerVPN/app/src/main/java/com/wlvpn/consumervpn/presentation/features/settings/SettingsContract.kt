package com.wlvpn.consumervpn.presentation.features.settings

import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.model.Settings.GeneralConnection.StartupConnectOption
import com.wlvpn.consumervpn.domain.model.VpnProtocol
import com.wlvpn.consumervpn.presentation.features.BaseContract

interface SettingsContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onStartupConnectChanged(startupConnectOption: StartupConnectOption)

        fun onScrambleChanged(scramble: Boolean)

        fun onVpnProtocolChanged(vpnProtocol: VpnProtocol)

        fun onProtocolChanged(protocol: Protocol)

        fun onPortChanged(port: Port)

        fun onAutoReconnect(reconnect: Boolean)

        fun onKillSwitchPreferenceClick()

        fun onSupportPreferenceClick()

        fun onAboutPreferenceClick()

        fun onLogOutMenuItemClick()

        fun onLogoutClick()
    }

    interface View : BaseContract.View {

        fun updateSettings(settings: Settings.GeneralConnection)

        fun showIkev2Preferences()

        fun showOpenVpnPreferences()

        fun setVpnProtocolPreferenceListOptions(options: List<VpnProtocol>)

        fun setPortPreferenceListOptions(options: List<Port>)

        fun setProtocolPreferenceListOptions(options: List<Protocol>)

        fun setStartupConnectPreferenceListOptions(options: List<StartupConnectOption>)

        fun setLoadingVisibility(visibility: Boolean)

        fun showFastestServerNotImplementedMessage()

        fun showSupport()

        fun showAbout()

        fun showLogin()

        fun showLogoutDialog()

        fun showKillSwitchDialog()

        fun toolbarVisibility(isVisible: Boolean)
    }
}