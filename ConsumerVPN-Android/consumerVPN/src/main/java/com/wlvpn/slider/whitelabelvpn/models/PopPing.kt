package com.wlvpn.slider.whitelabelvpn.models

import com.gentlebreeze.vpn.sdk.model.VpnPop

data class PopPing(
        var vpnPop: VpnPop,
        var ping: Int = 0,
        var capacityAverage: Int = 0
) {
    override fun hashCode(): Int = vpnPop.hashCode()

    override fun equals(other: Any?): Boolean = other?.hashCode() == vpnPop.hashCode()
}
