package com.wlvpn.consumervpn.domain.service.settings

import com.wlvpn.consumervpn.data.model.FastestServerLocation
import com.wlvpn.consumervpn.domain.gateway.ExternalSettingsGateway
import com.wlvpn.consumervpn.domain.model.Port
import com.wlvpn.consumervpn.domain.model.Server
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.repository.ConnectionRequestSettingsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import com.wlvpn.consumervpn.domain.service.settings.failure.LocationNotSelectedFailure
import io.reactivex.Completable
import io.reactivex.Single

class DefaultSettingsService(
        private val connectionSettingsRepository: ConnectionRequestSettingsRepository,
        private val generalConnectionSettingsRepository: GeneralConnectionSettingsRepository,
        private val externalSettingsGateWay: ExternalSettingsGateway
) : SettingsService {

    override fun getAllSettings(): Single<List<Settings>> =
            connectionSettingsRepository.getConnectionRequestSettings().cast(Settings::class.java)
                    .mergeWith(generalConnectionSettingsRepository.getGeneralSettings())
                    .toList()

    override fun getGeneralConnectionSettings(): Single<Settings.GeneralConnection> =
            generalConnectionSettingsRepository.getGeneralSettings()
                    //Add a default value to convert it to a single
                .toSingle(Settings.GeneralConnection())
                    .flatMap { settings ->
                        //Fetch available port if empty
                        if (settings.availablePorts.isEmpty()) {
                            addAvailablePortsToSettings(settings)
                        } else {
                            Single.just(settings)
                        }
                    }

    override fun getConnectionRequestSettings(): Single<Settings.ConnectionRequest> =
            connectionSettingsRepository.getConnectionRequestSettings()
                .toSingle()


    override fun updateGeneralSettings(
        updated: Settings.GeneralConnection
    ): Completable =
            getGeneralConnectionSettings()
                    .flatMapCompletable { storedSettings ->

                        //If scramble or protocol changed, obtain avaiable ports again
                        if (storedSettings.scramble != updated.scramble
                            || storedSettings.protocol != updated.protocol
                        ) {
                            addAvailablePortsToSettings(updated)
                                    .flatMapCompletable {
                                        //Save this new config
                                        generalConnectionSettingsRepository
                                                .setGeneralConnectionSettings(it)
                                    }
                        } else {
                            generalConnectionSettingsRepository
                                .setGeneralConnectionSettings(updated)
                        }
                    }

    override fun updateSelectedLocation(
        serverLocation: ServerLocation
    ): Completable = Completable.defer {

        val connectionRequest = Settings.ConnectionRequest(
            location = serverLocation
        )

        connectionSettingsRepository.setConnectionRequestSettings(connectionRequest)
    }

    override fun updateSelectedServer(server: Server): Completable =
        Completable.defer {
            val connectionRequest = Settings.ConnectionRequest(
                server = server,
                location = server.location
            )

            connectionSettingsRepository.setConnectionRequestSettings(connectionRequest)
        }

    override fun updateSelectedFastestAvailable(): Completable = Completable.defer {
        val connectionRequest = Settings.ConnectionRequest(location = FastestServerLocation())
        connectionSettingsRepository.setConnectionRequestSettings(connectionRequest)
    }

    override fun updateConnectionRequestWithStartupSettings(): Completable =
        getGeneralConnectionSettings()
            .flatMapCompletable { generalSettings ->
                when (generalSettings.startupConnectOption) {
                    //Set connection request to fastest
                    Settings.GeneralConnection.StartupConnectOption.FASTEST_SERVER ->
                        updateSelectedFastestAvailable()
                    //LAST_SERVER and NONE
                    else -> Completable.complete()
                }
            }

    private fun addAvailablePortsToSettings(
        generalConnection: Settings.GeneralConnection
    ): Single<Settings.GeneralConnection> =
    //Ask external settings for ports
        externalSettingsGateWay.getAvailablePorts(
            generalConnection.protocol,
            generalConnection.scramble
        )
                    //Add a default value to convert it to a single
            .onErrorReturn { listOf(Port()) }
            .toSingle(listOf(Port()))
                    .flatMap { availablePorts ->
                        generalConnection.port = availablePorts.first()
                        generalConnection.availablePorts = availablePorts
                        Single.just(generalConnection)
                    }

}