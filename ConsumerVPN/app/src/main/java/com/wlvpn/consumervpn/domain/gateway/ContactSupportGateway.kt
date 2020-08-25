package com.wlvpn.consumervpn.domain.gateway

import io.reactivex.Completable

interface ContactSupportGateway {

    fun sendCommentsToSupport(comments: String): Completable
}