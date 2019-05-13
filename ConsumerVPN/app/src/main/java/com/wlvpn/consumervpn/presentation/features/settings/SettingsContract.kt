package com.wlvpn.consumervpn.presentation.features.settings

import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.model.Settings.GeneralConnection.StartupConnectOption
import com.wlvpn.consumervpn.presentation.features.BaseContract

interface SettingsContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onAppStartupLaunchChanged(launchOnStartup: Boolean)

        fun onStartupConnectChanged(startupConnectOption: StartupConnectOption)

        fun onScrambleChanged(scramble: Boolean)

        fun onProtocolChanged(protocol: Protocol)

        fun onPortChanged(port: Port)

        fun onAutoReconnect(reconnect: Boolean)

        fun onAboutPreferenceClick()
    }

    interface View : BaseContract.View {

        fun updateSettings(settings: Settings.GeneralConnection)

        fun setPortPreferenceListOptions(options: List<Port>)

        fun setProtocolPreferenceListOptions(options: List<Protocol>)

        fun setStartupConnectPreferenceListOptions(options: List<StartupConnectOption>)

        fun setLoadingVisibility(visibility: Boolean)

        fun showFastestServerNotImplementedMessage()

        fun showAbout()
    }
}