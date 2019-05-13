package com.wlvpn.consumervpn.domain.gateway

import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Protocol
import io.reactivex.Maybe

interface ExternalSettingsGateway {
    fun getAvailablePorts(protocol: Protocol, scramble: Boolean): Maybe<List<Port>>
}