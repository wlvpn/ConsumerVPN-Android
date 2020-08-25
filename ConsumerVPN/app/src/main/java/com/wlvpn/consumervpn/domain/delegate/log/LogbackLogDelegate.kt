package com.wlvpn.consumervpn.domain.delegate.log

import com.wlvpn.consumervpn.domain.gateway.LogsGateway
import com.wlvpn.consumervpn.domain.model.Credentials
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.model.SystemInformation
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import io.reactivex.Single
import io.reactivex.functions.Function3
import java.util.*

class LogbackLogDelegate(
    private val systemInfo: SystemInformation,
    private val diagnosisMessagePattern: String,
    private val logsGateway: LogsGateway,
    private val settingsRepository: GeneralConnectionSettingsRepository,
    private val credentialsRepository: CredentialsRepository
) : LogDelegate {

    override fun getApplicationLogs(): Single<String> =
        Single.zip(
            credentialsRepository.getCredentials().toSingle()
                // In case the Encryption tool fails
                .onErrorResumeNext {
                    Single.just(
                        Credentials("", "")
                    )
                },
            logsGateway.getStoredLogs(),
            settingsRepository.getGeneralSettings().toSingle(
                Settings.GeneralConnection() // Default value if no settings was changed before
            ),
            Function3 { credentials: Credentials,
                        logs: String,
                        settings: Settings.GeneralConnection ->
                diagnosisMessagePattern.format(
                    Date(),
                    credentials.username,
                    systemInfo.toString().replace(",", ",\n"),
                    settings.toString().replace(",", ",\n"),
                    logs
                )
            })
}