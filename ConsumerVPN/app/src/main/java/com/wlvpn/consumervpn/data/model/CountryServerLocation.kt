package com.wlvpn.consumervpn.data.model

import com.wlvpn.consumervpn.domain.model.ServerLocation

data class CountryServerLocation(val country: String,
                                 val countryCode: String) : ServerLocation