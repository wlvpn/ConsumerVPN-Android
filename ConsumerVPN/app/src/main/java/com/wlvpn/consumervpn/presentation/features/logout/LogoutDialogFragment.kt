package com.wlvpn.consumervpn.presentation.features.logout

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R

class LogoutDialogFragment : DialogFragment(),
    DialogInterface.OnClickListener {

    companion object {

        /**
         * Static Identifier Dialog Name
         */
        val TAG = "${BuildConfig.APPLICATION_ID}:${this::class.java.name}"

        fun newInstance(): LogoutDialogFragment {
            val fragment = LogoutDialogFragment()

            val bundle = Bundle()

            fragment.arguments = bundle

            return fragment
        }
    }

    var onResultCallback: OnLogoutDialogResult? = null

    /**
     * On Create Dialog. Use this to create dialog instead of on create
     *
     * @param savedInstanceState Current saved instance. Always marked it as nullable
     * @return The new dialog. You can use dialog builder
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.logout_dialog_label_title)
                .setMessage(R.string.logout_dialog_label_message)
                .setPositiveButton(R.string.generic_button_confirm, this)
                .setNegativeButton(R.string.generic_button_cancel, this)
                .setCancelable(false)
                .create()
        } ?: run {
            super.onCreateDialog(savedInstanceState)
        }
    }

    /**
     * Will be executed when either positive/negative buttons get clicked on the dialog
     *
     * @param dialog The dialog interface
     * @param which  Will contain identifier of rowClick button
     */
    override fun onClick(dialog: DialogInterface?, which: Int) {
        onResultCallback?.onLogoutDialogResponse(which)

        dismiss()
        // avoids a leak notification
        dialog?.let {
            onDismiss(dialog)
        }
    }

}