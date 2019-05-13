package com.wlvpn.consumervpn.data.gateway.servers

import com.evernote.android.job.JobManager
import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.gentlebreeze.vpn.sdk.model.VpnPop
import com.gentlebreeze.vpn.sdk.model.VpnServer
import com.gentlebreeze.vpn.sdk.sort.SortOrder
import com.gentlebreeze.vpn.sdk.sort.SortPop
import com.gentlebreeze.vpn.sdk.sort.SortPopOption
import com.wlvpn.consumervpn.data.exception.map.NetworkThrowableMapper
import com.wlvpn.consumervpn.data.exception.map.ThrowableMapper
import com.wlvpn.consumervpn.data.job.ServersRefreshJob
import com.wlvpn.consumervpn.data.toDomainServerLocation
import com.wlvpn.consumervpn.data.util.onErrorMapThrowable
import com.wlvpn.consumervpn.data.util.toSingle
import com.wlvpn.consumervpn.domain.gateway.ExternalServersGateway
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerHost
import com.wlvpn.consumervpn.domain.model.ServerLocation
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.rxkotlin.toObservable

class ExternalServersGateway(
    private val vpnSdk: IVpnSdk
) : ExternalServersGateway, ThrowableMapper by NetworkThrowableMapper() {

    override fun fetchAllServers(): Maybe<List<Server>> =
        vpnSdk.fetchAllPops()
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchAllServersSortByCountry(): Maybe<List<Server>> =
        vpnSdk.fetchPopsFirstByCountryQuery("", SortPop(SortPopOption.COUNTRY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchAllServersSortByCity(): Maybe<List<Server>> =
        vpnSdk.fetchPopsFirstByCityQuery("", SortPop(SortPopOption.CITY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServersByLocation(location: ServerLocation): Maybe<List<Server>> =
        vpnSdk.fetchPopByCountryCodeAndCity(location.countryCode, location.city ?: "")
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServersByCountry(countryCode: String): Maybe<List<Server>> =
        vpnSdk.fetchAllPopsByCountryCode(countryCode)
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServerLocationsByCountryQuery(query: String): Maybe<List<ServerLocation>> =
        vpnSdk.fetchPopsFirstByCountryQuery(query, SortPop(SortPopOption.COUNTRY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .map { it.toDomainServerLocation() }.toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServerLocationsByCityQuery(query: String): Maybe<List<ServerLocation>> =
        vpnSdk.fetchPopsFirstByCityQuery(query, SortPop(SortPopOption.CITY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .map { it.toDomainServerLocation() }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServersByCountryQuery(query: String): Maybe<List<Server>> =
        vpnSdk.fetchPopsFirstByCountryQuery(query, SortPop(SortPopOption.COUNTRY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchServersByCityQuery(query: String): Maybe<List<Server>> =
        vpnSdk.fetchPopsFirstByCityQuery(query, SortPop(SortPopOption.CITY, SortOrder.ASC))
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .flatMap { vpnPop ->
                vpnSdk.fetchAllServersByPop(vpnPop)
                    .toSingle()
                    .flatMapObservable {
                        it.map { vpnServer ->
                            vpnPopAndServerToDomainServer(vpnPop, vpnServer)
                        }.toObservable()
                    }
            }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    override fun fetchAllServerLocations(): Maybe<List<ServerLocation>> =
        vpnSdk.fetchAllPops()
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .flatMapObservable { it.toObservable() }
            .map { it.toDomainServerLocation() }
            .toList()
            .flatMapMaybe {
                if (it.isEmpty()) Maybe.empty()
                else Maybe.just(it)
            }

    private fun vpnPopAndServerToDomainServer(vpnPop: VpnPop, vpnServer: VpnServer): Server {
        return Server(
            ServerHost(vpnServer.name, vpnServer.ipAddress),
            ServerLocation(
                vpnPop.city,
                vpnPop.country,
                vpnPop.countryCode
            ),
            vpnServer.isInMaintenance,
            vpnServer.scheduledMaintenance,
            vpnServer.capacity
        )
    }

    override fun refreshServers(): Completable {
        return vpnSdk.updateServerList()
            .toSingle()
            .onErrorMapThrowable { mapThrowable(it) }
            .ignoreElement()
    }

    override fun scheduleRefreshServers() =
        Completable.create { emitter ->
            ServersRefreshJob.schedule()
            emitter.onComplete()
        }

    override fun cancelScheduledRefreshServers() =
        Completable.create { emitter ->
            JobManager.instance().cancelAllForTag(ServersRefreshJob.JOB_ID)
            emitter.onComplete()
        }
}