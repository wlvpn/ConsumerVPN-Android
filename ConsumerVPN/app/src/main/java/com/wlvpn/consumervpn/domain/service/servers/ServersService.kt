package com.wlvpn.consumervpn.domain.service.servers

import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import io.reactivex.Completable
import io.reactivex.Maybe

interface ServersService {

    fun getAllServers(): Maybe<List<Server>>

    fun getAllServersByServerLocation(location: ServerLocation): Maybe<List<Server>>

    fun getAllServersLocations(): Maybe<List<ServerLocation>>

    fun getServersLocationsByQuery(query: String): Maybe<List<ServerLocation>>

    fun setSorting(sortingType: ServersSortingType)

    fun refreshServers(): Completable

    fun scheduleRefreshServers(): Completable

    fun cancelScheduledRefreshServers(): Completable

}