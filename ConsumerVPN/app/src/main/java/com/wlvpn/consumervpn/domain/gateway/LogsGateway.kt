package com.wlvpn.consumervpn.domain.gateway

import io.reactivex.Single

interface LogsGateway {
    fun getStoredLogs(): Single<String>
}