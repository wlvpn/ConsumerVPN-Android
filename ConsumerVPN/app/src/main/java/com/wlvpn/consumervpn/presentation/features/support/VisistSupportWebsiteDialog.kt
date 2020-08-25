package com.wlvpn.consumervpn.presentation.features.support

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wlvpn.consumervpn.R

class VisitSupportWebsiteDialog : DialogFragment() {

    companion object {
        val TAG = this::class.java.name
    }

    var onVisitWebsiteClick: (() -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.support_dialog_visit_support_website_title))
            .setMessage(getString(R.string.support_dialog_visit_support_website_message))
            .setPositiveButton(
                getString(R.string.support_dialog_visit_support_website_positive_button)
            ) { _, _ ->
                onVisitWebsiteClick?.let { it() }
            }
            .setNegativeButton(
                getString(R.string.support_dialog_visit_support_website_negative_button)
            ) { _, _ -> /*No-op*/ }
            .create()
    }
}