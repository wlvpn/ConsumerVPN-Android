package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import com.wlvpn.consumervpn.data.model.CityAndCountryServerLocation
import com.wlvpn.consumervpn.data.model.CountryServerLocation

sealed class ServerRowItem {
    open var isSelected: Boolean = false

    open var isExpanded: Boolean = false

    open val id = -1
}

class ServerRowListState {
    var currentSortType = ServerRowListType.CountryList
}

enum class ServerRowListType(val value: Int) {
    CountryList(0),

    CityList(1);
}

class ServerFastestRow(
    override var isSelected: Boolean,
    override var isExpanded: Boolean
) : ServerRowItem() {

    override val id: Int
        get() = "ServerFastestRow".hashCode()
}

data class ServerCountryRow(
    override var isSelected: Boolean,
    override var isExpanded: Boolean,
    var cityCount: Int,
    val location: CountryServerLocation
) : ServerRowItem() {

    override val id: Int
        get() = ((location.country + location.countryCode).hashCode())
}

data class ServerCityRow(
    override var isSelected: Boolean,
    override var isExpanded: Boolean,
    val location: CityAndCountryServerLocation
) : ServerRowItem() {

    override val id: Int
        get() = ((location.city + location.countryCode + location.country).hashCode())

}