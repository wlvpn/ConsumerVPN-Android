package com.wlvpn.consumervpn.domain.model


sealed class Settings {

    data class GeneralConnection(
        var launchOnStartup: Boolean = false,
        var autoReconnect: Boolean = false,
        var scramble: Boolean = false,
        var protocol: Protocol = Protocol.TCP,
        var port: Port = Port(),
        var availablePorts: List<Port> = emptyList(),
        var startupConnectOption: StartupConnectOption = StartupConnectOption.NONE,
        var StartupConnectLocation: ServerLocation? = null
    ) : Settings() {
        enum class StartupConnectOption { LAST_SERVER, FASTEST_SERVER, NONE }
    }

    data class ConnectionRequest(
        //Default to no server
        var server: Server? = null,
        var location: ServerLocation
    ) : Settings()
}