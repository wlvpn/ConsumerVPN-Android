package com.wlvpn.consumervpn.domain.repository

import com.wlvpn.consumervpn.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Maybe

interface GeneralConnectionSettingsRepository {

    fun getGeneralSettings(): Maybe<Settings.GeneralConnection>

    fun clear(): Completable

    fun setGeneralConnectionSettings(
        generalConnection: Settings.GeneralConnection
    ): Completable
}