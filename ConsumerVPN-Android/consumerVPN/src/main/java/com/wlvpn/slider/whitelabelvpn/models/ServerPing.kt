package com.wlvpn.slider.whitelabelvpn.models

import com.gentlebreeze.vpn.sdk.model.VpnServer

data class ServerPing(
        val vpnServer: VpnServer,
        val ping: Int = 0
) {
    override fun hashCode(): Int = vpnServer.hashCode()

    override fun equals(other: Any?): Boolean = other?.hashCode() == vpnServer.hashCode()
}
