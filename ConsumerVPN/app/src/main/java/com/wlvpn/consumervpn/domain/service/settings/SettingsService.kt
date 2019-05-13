package com.wlvpn.consumervpn.domain.service.settings

import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.model.Settings
import io.reactivex.Completable
import io.reactivex.Single

interface SettingsService {

    fun getAllSettings(): Single<List<Settings>>

    fun getGeneralConnectionSettings(): Single<Settings.GeneralConnection>

    fun getConnectionRequestSettings(): Single<Settings.ConnectionRequest>

    fun updateGeneralSettings(updated: Settings.GeneralConnection): Completable

    //TODO Refactor this a long with Settings.ConnectionRequest model
    fun updateSelectedLocation(serverLocation: ServerLocation): Completable

    //TODO Refactor this a long with Settings.ConnectionRequest model
    fun updateSelectedServer(server: Server): Completable

    //TODO Refactor this a long with Settings.ConnectionRequest model
    fun updateSelectedFastestAvailable(): Completable

    fun updateConnectionRequestWithStartupSettings(): Completable

}