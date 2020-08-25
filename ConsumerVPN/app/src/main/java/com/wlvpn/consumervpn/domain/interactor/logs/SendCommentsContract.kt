package com.wlvpn.consumervpn.domain.interactor.logs

import com.wlvpn.consumervpn.domain.failure.Failure
import io.reactivex.Completable

interface SendCommentsContract {

    interface Interactor {
        fun execute(comments: String, includeLogs: Boolean): Completable
    }

    //Failures
    class EmptyCommentsFailure : Failure("User comments are empty")
}