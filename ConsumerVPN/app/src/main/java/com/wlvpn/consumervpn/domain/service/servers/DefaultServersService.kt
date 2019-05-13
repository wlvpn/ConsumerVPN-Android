package com.wlvpn.consumervpn.domain.service.servers

import com.wlvpn.consumervpn.domain.gateway.ExternalServersGateway
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import io.reactivex.Completable
import io.reactivex.Maybe

class DefaultServersService(
    private val externalServersGateway: ExternalServersGateway
) : ServersService {

    private var sortType = ServersSortingType.CountrySort

    override fun getAllServers(): Maybe<List<Server>> =
        externalServersGateway.fetchAllServers()

    override fun getAllServersByServerLocation(location: ServerLocation): Maybe<List<Server>> =
        externalServersGateway.fetchServersByLocation(location)

    override fun getAllServersLocations(): Maybe<List<ServerLocation>> =
        externalServersGateway.fetchAllServerLocations()

    override fun getServersLocationsByQuery(query: String): Maybe<List<ServerLocation>> =
        when (sortType) {
            ServersSortingType.CountrySort ->
                externalServersGateway.fetchServerLocationsByCountryQuery(query)
            ServersSortingType.CitySort ->
                externalServersGateway.fetchServerLocationsByCityQuery(query)
        }

    override fun setSorting(sortingType: ServersSortingType) {
        sortType = sortingType
    }

    override fun refreshServers(): Completable {
        return externalServersGateway.refreshServers()
    }

    override fun scheduleRefreshServers() = externalServersGateway.scheduleRefreshServers()

    override fun cancelScheduledRefreshServers() = externalServersGateway.cancelScheduledRefreshServers()
}