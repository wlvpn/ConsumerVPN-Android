package com.wlvpn.consumervpn.domain.interactor.logs

import com.wlvpn.consumervpn.domain.delegate.log.LogDelegate
import com.wlvpn.consumervpn.domain.delegate.log.LogbackLogDelegate
import com.wlvpn.consumervpn.domain.gateway.LogsGateway
import com.wlvpn.consumervpn.domain.model.SystemInformation
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import io.reactivex.Single

class GetApplicationLogsInteractor(
    private val systemInfo: SystemInformation,
    private val diagnosisMessagePattern: String,
    private val logsGateway: LogsGateway,
    private val settingsRepository: GeneralConnectionSettingsRepository,
    private val credentialsRepository: CredentialsRepository
) : GetApplicationLogsContract.Interactor, LogDelegate by LogbackLogDelegate(
    systemInfo,
    diagnosisMessagePattern,
    logsGateway,
    settingsRepository,
    credentialsRepository
) {

    private val logs: Single<String> = getApplicationLogs().cache()

    override fun execute(): Single<String> = logs

}