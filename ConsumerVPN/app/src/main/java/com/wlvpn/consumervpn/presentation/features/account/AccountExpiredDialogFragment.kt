package com.wlvpn.consumervpn.presentation.features.account

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wlvpn.consumervpn.R

/**
 * Expired Membership Dialog Fragment
 */
class AccountExpiredDialogFragment : DialogFragment(),
    DialogInterface.OnClickListener {

    /**
     * Companion will work for static values
     */
    companion object {
        /**
         * Static Identifier Dialog Name
         */
        const val TAG = "consumer:MembershipExpiredDialogFragment"

        const val RETRY_RESPONSE_CODE = R.string.home_dialog_expired_account_retry

        const val CANCEL_RESPONSE_CODE = R.string.home_dialog_expired_account_cancel
    }

    var onResultCallback: OnAccountExpiredDialogResult? = null

    /**
     * On Create Dialog. Use this to create dialog instead of on create
     *
     * @param savedInstanceState Current saved instance. Always marked it as nullable
     * @return The new dialog. You can use dialog builder
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return if (context != null) {
            AlertDialog.Builder(requireContext())
                .setTitle(R.string.home_dialog_expired_account_title)
                .setMessage(R.string.home_dialog_expired_account_message)
                .setPositiveButton(R.string.home_dialog_expired_account_retry, this)
                .setNegativeButton(R.string.home_dialog_expired_account_cancel, this)
                .setCancelable(false)
                .create()

        } else {
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
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                onResultCallback?.onDialogResponse(RETRY_RESPONSE_CODE)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                onResultCallback?.onDialogResponse(CANCEL_RESPONSE_CODE)
            }
        }

        dismiss()
    }

    interface OnAccountExpiredDialogResult {
        fun onDialogResponse(resultCode: Int)
    }

}