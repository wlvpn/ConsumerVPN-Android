package com.wlvpn.consumervpn.domain.model

data class Server(
    val host: ServerHost = ServerHost(),
    val location: ServerLocation,
    val isInMaintenance: Boolean = false,
    val scheduledMaintenance: Long = 0,
    val capacity: Int = -1
)

data class ServerLocation(
    val city: String? = null,
    val country: String,
    val countryCode: String
) {
    constructor(country: String, countryCode: String) :
            this("", country, countryCode)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as ServerLocation

        if (this.hashCode() != other.hashCode()) {
            return false
        }

        return true
    }

    override fun hashCode(): Int {
        return city.hashCode() + country.hashCode() + countryCode.hashCode()
    }
}

data class ServerHost(
    val name: String = "",
    val ipAddress: String = ""
)