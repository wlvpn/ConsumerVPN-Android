package com.wlvpn.consumervpn.domain.repository

import com.wlvpn.consumervpn.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Maybe

interface ConnectionRequestSettingsRepository {

    fun getConnectionRequestSettings(): Maybe<Settings.ConnectionRequest>

    fun setConnectionRequestSettings(
        connectionRequest: Settings.ConnectionRequest
    ): Completable

    fun clear(): Completable

}