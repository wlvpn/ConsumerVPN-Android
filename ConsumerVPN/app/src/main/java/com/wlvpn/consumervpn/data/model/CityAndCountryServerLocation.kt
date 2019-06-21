package com.wlvpn.consumervpn.data.model

import com.wlvpn.consumervpn.domain.model.ServerLocation

data class CityAndCountryServerLocation(val city: String,
                                        val country: String,
                                        val countryCode: String)
    : ServerLocation