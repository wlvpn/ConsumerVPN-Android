package com.wlvpn.consumervpn.domain.delegate.log

import io.reactivex.Single

interface LogDelegate {

    fun getApplicationLogs(): Single<String>

}