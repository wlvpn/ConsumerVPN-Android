package com.wlvpn.consumervpn.presentation.di.module

import android.app.Application
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.gateway.ContactSupportGateway
import com.wlvpn.consumervpn.domain.gateway.LogsGateway
import com.wlvpn.consumervpn.domain.interactor.logs.GetApplicationLogsContract
import com.wlvpn.consumervpn.domain.interactor.logs.GetApplicationLogsInteractor
import com.wlvpn.consumervpn.domain.interactor.logs.SendCommentsContract
import com.wlvpn.consumervpn.domain.interactor.logs.SendCommentsInteractor
import com.wlvpn.consumervpn.domain.model.SystemInformation
import com.wlvpn.consumervpn.domain.repository.CredentialsRepository
import com.wlvpn.consumervpn.domain.repository.GeneralConnectionSettingsRepository
import dagger.Module
import dagger.Provides

@Module
class InteractorModule {

    @Provides
    fun providesLogsInteractor(
        systemInfo: SystemInformation,
        application: Application,
        logsGateway: LogsGateway,
        settingsRepository: GeneralConnectionSettingsRepository,
        credentialsRepository: CredentialsRepository
    ): GetApplicationLogsContract.Interactor = GetApplicationLogsInteractor(
        systemInfo,
        application.getString(R.string.support_label_diagnosis_message_format),
        logsGateway,
        settingsRepository,
        credentialsRepository
    )

    @Provides
    fun providesSendCommentsInteractor(
        application: Application,
        systemInfo: SystemInformation,
        logsGateway: LogsGateway,
        supportGateway: ContactSupportGateway,
        settingsRepository: GeneralConnectionSettingsRepository,
        credentialsRepository: CredentialsRepository
    ): SendCommentsContract.Interactor = SendCommentsInteractor(
        systemInfo,
        application.getString(R.string.support_label_diagnosis_message_format),
        logsGateway,
        supportGateway,
        settingsRepository,
        credentialsRepository
    )
}