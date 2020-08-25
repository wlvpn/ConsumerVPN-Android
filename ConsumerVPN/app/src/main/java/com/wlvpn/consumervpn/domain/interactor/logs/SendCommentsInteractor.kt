package com.wlvpn.consumervpn.domain.interactor.logs

import com.wlvpn.consumervpn.domain.delegate.log.LogDelegate
import com.wlvpn.consumervpn.domain.delegate.log.LogbackLogDelegate
import com.wlvpn.consumervpn.domain.gateway.ContactSupportGateway
import com.wlvpn.consumervpn.domain.gateway.LogsGateway
import com.wlvpn.consumervpn.domain.model.SystemInformation
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import io.reactivex.Completable

private const val COMMENTS_WITH_LOGS_FORMAT = "%s \n %s"

class SendCommentsInteractor(
    private val systemInfo: SystemInformation,
    private val diagnosisMessagePattern: String,
    private val logsGateway: LogsGateway,
    private val supportGateway: ContactSupportGateway,
    private val settingsRepository: GeneralConnectionSettingsRepository,
    private val credentialsRepository: CredentialsRepository
) : SendCommentsContract.Interactor, LogDelegate by LogbackLogDelegate(
    systemInfo,
    diagnosisMessagePattern,
    logsGateway,
    settingsRepository,
    credentialsRepository
) {

    override fun execute(comments: String, includeLogs: Boolean): Completable =
        when {
            comments.isBlank() ->
                Completable.error(SendCommentsContract.EmptyCommentsFailure())

            includeLogs ->
                getApplicationLogs()
                    .map { logs -> COMMENTS_WITH_LOGS_FORMAT.format(comments, logs) }
                    .flatMapCompletable { supportGateway.sendCommentsToSupport(it) }

            else -> supportGateway.sendCommentsToSupport(comments)
        }
}