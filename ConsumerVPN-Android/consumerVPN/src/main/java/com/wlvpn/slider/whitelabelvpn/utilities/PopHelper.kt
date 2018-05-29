package com.wlvpn.slider.whitelabelvpn.utilities

import com.gentlebreeze.vpn.sdk.model.VpnPop

fun VpnPop.getFormattedLocation(): String = "${this.city}, ${this.country}"