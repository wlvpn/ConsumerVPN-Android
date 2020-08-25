package com.wlvpn.consumervpn.presentation.features.support

import com.wlvpn.consumervpn.data.gateway.logs.failure.SendEmailNotSupportedFailure
import com.wlvpn.consumervpn.domain.interactor.logs.GetApplicationLogsContract
import com.wlvpn.consumervpn.domain.interactor.logs.SendCommentsContract
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.rxkotlin.addTo
import timber.log.Timber

class ContactSupportPresenter(
    private val getLogsInteractor: GetApplicationLogsContract.Interactor,
    private val sendCommentsInteractor: SendCommentsContract.Interactor,
    private val supportURL: String,
    private val schedulerProvider: SchedulerProvider
) : ContactSupportContract.Presenter {

    override var view: ContactSupportContract.View? = null

    private val disposables = CompositeDisposable()

    private var includeLogs: Boolean = false

    private var getLogsDisposable = Disposables.disposed()

    private var sendCommentsDisposable = Disposables.disposed()

    override fun start() {
        view?.logsVisibility(includeLogs)
        view?.setLoadingViewVisibility(sendCommentsDisposable.isDisposed)

        if (getLogsDisposable.isDisposed) {
            view?.setLoadingViewVisibility(true)
            getLogsDisposable = getLogsInteractor.execute()
                .defaultSchedulers(schedulerProvider)
                .subscribe({
                    view?.setLogs(it)
                    view?.setLoadingViewVisibility(false)
                }) { throwable ->
                    view?.setLoadingViewVisibility(false)
                    Timber.e(throwable, "Error getting logs")
                }.addTo(disposables)
        }
    }

    override fun cleanUp() {
        disposables.clear()
        super.cleanUp()
    }

    override fun onSendCommentsClick(comments: String) {
        view?.setEmptyCommentsMessageVisibility(false)
        view?.setLoadingViewVisibility(true)
        if (sendCommentsDisposable.isDisposed) {
            sendCommentsDisposable = sendCommentsInteractor.execute(comments, includeLogs)
                .defaultSchedulers(schedulerProvider)
                .subscribe({
                    view?.setLoadingViewVisibility(false)
                }) {
                    when (it) {
                        is SendEmailNotSupportedFailure -> {
                            Timber.e(it, "Sending email is not supported in this device.")
                            view?.showVisitSupportWebsiteDialog()
                        }
                        is SendCommentsContract.EmptyCommentsFailure ->
                            view?.setEmptyCommentsMessageVisibility(true)

                        else -> Timber.e(it, "Error sending comments to support.")
                    }
                }.addTo(disposables)
        }
    }

    override fun onIncludeLogsChanged(includeLogs: Boolean) {
        this.includeLogs = includeLogs
        view?.logsVisibility(includeLogs)
    }

    override fun onVisitSupportWebsiteSelected() {
        view?.openSupportWebsite(supportURL)
    }

    override fun onOpenLinksNotSupported() {
        view?.showContactSupportMessage(supportURL)
    }
}