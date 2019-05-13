package com.wlvpn.consumervpn.domain.model

data class IpGeoLocationInfo(

    val city: String?,

    val countryCode: String,

    val ip: String,

    val latitude: Double,

    val longitude: Double

)