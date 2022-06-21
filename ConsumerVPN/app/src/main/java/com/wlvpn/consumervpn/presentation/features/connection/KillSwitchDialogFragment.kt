package com.wlvpn.consumervpn.presentation.features.connection

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wlvpn.consumervpn.R

/**
 * Kill Switch Dialog Fragment
 */
class KillSwitchDialogFragment : DialogFragment(), DialogInterface.OnClickListener {

    /**
     * Companion will work for static values
     */
    companion object {
        /**
         * Static Identifier Dialog Name
         */
        const val TAG = "consumer:KillSwitchDialogTag"

        fun newInstance(): KillSwitchDialogFragment {
            return KillSwitchDialogFragment()
        }

    }

    var onResultCallback: OnKillSwitchDialogResult? = null

    /**
     * On Create Dialog. Use this to create dialog instead of on create
     *
     * @param savedInstanceState Current saved instance. Always marked it as nullable
     * @return The new dialog. You can use dialog builder
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return context?.let { context ->
            AlertDialog.Builder(context)
                .setTitle(R.string.kill_switch_dialog_label_title)
                .setMessage(R.string.kill_switch_dialog_label_message)
                .setPositiveButton(R.string.kill_switch_dialog_button_take_me_there, this)
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
     * @param which  Will contain identifier of click button
     */
    override fun onClick(dialog: DialogInterface?, which: Int) {
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                onResultCallback?.onKillSwitchDialogResponse(DialogInterface.BUTTON_POSITIVE, TAG)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                onResultCallback?.onKillSwitchDialogResponse(DialogInterface.BUTTON_NEGATIVE, TAG)
            }
        }

        dismiss()

        // Avoids leak notification
        dialog?.let {
            onDismiss(dialog)
        }
    }
}