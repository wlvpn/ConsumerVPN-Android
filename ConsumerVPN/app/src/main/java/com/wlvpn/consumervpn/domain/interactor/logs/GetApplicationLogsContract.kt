package com.wlvpn.consumervpn.domain.interactor.logs

import io.reactivex.Single

interface GetApplicationLogsContract {

    interface Interactor {

        fun execute(): Single<String>
    }
}