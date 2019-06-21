package com.wlvpn.consumervpn.domain.model

data class Server(
    val host: ServerHost = ServerHost(),
    val location: ServerLocation,
    val isInMaintenance: Boolean = false,
    val scheduledMaintenance: Long = 0,
    val capacity: Int = -1
)

/*
 *  This interface should be implemented in order to provide an specific location type.
 *  It is an empty interface because it needs to be open enough to add a new ServerLocation
 *  definition as needed, due to this, there is no property that could be applied
 *  to every ServerLocation type.
 */
interface ServerLocation

data class ServerHost(
    val name: String = "",
    val ipAddress: String = ""
)