package com.wlvpn.consumervpn.domain.gateway

import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import io.reactivex.Completable
import io.reactivex.Maybe

interface ExternalServersGateway {

    fun fetchAllServers(): Maybe<List<Server>>

    fun fetchAllServersSortByCountry(): Maybe<List<Server>>

    fun fetchAllServersSortByCity(): Maybe<List<Server>>

    fun fetchAllServerLocations(): Maybe<List<ServerLocation>>

    fun fetchServersByLocation(location: ServerLocation): Maybe<List<Server>>

    fun fetchServersByCountry(countryCode: String): Maybe<List<Server>>

    fun fetchServerLocationsByCountryQuery(query: String): Maybe<List<ServerLocation>>

    fun fetchServerLocationsByCityQuery(query: String): Maybe<List<ServerLocation>>

    fun fetchServersByCountryQuery(query: String): Maybe<List<Server>>

    fun fetchServersByCityQuery(query: String): Maybe<List<Server>>

    fun refreshServers(): Completable

    fun scheduleRefreshServers(): Completable

    fun cancelScheduledRefreshServers(): Completable

}