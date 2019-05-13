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
        enum class StartupConnectOption { LAST_SERVER, FASTEST_SERVER, FASTEST_IN_LOCATION, NONE }
    }

    /*
    * TODO: We might want to refactor this to `ServerToConnect` having optionals is misleading
    *   we can set ServerToConnect as a sealed class with Connection option childrens:
    *                           ServerToConnect
    *                                 |
    *                                /|\
    *          _____________________/ | \_____________________________
    *          |                      |                              |
    *          |                      |                              |
    *   FastestServer       FastestInLocation(Location)     SpecificHost(Server)
    */
    data class ConnectionRequest(
        //Default to no server
        var server: Server? = null,
        //Default to no server
        var location: ServerLocation? = null,
        //Default to Fastest
        var connectionOption: ConnectOption = ConnectOption.FASTEST_SERVER
    ) : Settings() {
        enum class ConnectOption { FASTEST_SERVER, FASTEST_IN_LOCATION, WITH_SERVER }
    }

}