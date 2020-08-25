package com.wlvpn.consumervpn.presentation.features.support

import com.wlvpn.consumervpn.presentation.features.BaseContract

interface ContactSupportContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onSendCommentsClick(comments: String)

        fun onIncludeLogsChanged(includeLogs: Boolean)

        fun onVisitSupportWebsiteSelected()

        fun onOpenLinksNotSupported()
    }

    interface View : BaseContract.View {

        fun setLogs(logs: String)

        fun logsVisibility(visibility: Boolean)

        fun showVisitSupportWebsiteDialog()

        fun openSupportWebsite(supportUrl: String)

        fun showContactSupportMessage(supportUrl: String)

        fun setEmptyCommentsMessageVisibility(visibility: Boolean)

        fun setLoadingViewVisibility(visibility: Boolean)
    }
}