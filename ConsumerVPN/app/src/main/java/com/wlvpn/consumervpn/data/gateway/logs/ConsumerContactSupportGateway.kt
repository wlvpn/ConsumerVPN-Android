package com.wlvpn.consumervpn.data.gateway.logs

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import com.wlvpn.consumervpn.data.gateway.logs.failure.SendEmailNotSupportedFailure
import com.wlvpn.consumervpn.domain.gateway.ContactSupportGateway
import io.reactivex.Completable

private const val MAILTO = "mailto:"

class ConsumerContactSupportGateway(
    private val context: Context,
    private val supportEmail: String,
    private val supportEmailSubject: String
) : ContactSupportGateway {

    override fun sendCommentsToSupport(
        comments: String
    ): Completable = Completable.create { emitter ->
        Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse(MAILTO) // only email apps should handle this
            addFlags(FLAG_ACTIVITY_NEW_TASK)
            putExtra(Intent.EXTRA_EMAIL, arrayOf(supportEmail))
            putExtra(Intent.EXTRA_SUBJECT, supportEmailSubject)
            putExtra(Intent.EXTRA_TEXT, comments)

            resolveActivity(context.packageManager)?.let {
                context.startActivity(this)

                emitter.onComplete()

            } ?: emitter.onError(SendEmailNotSupportedFailure())
        }
    }
}