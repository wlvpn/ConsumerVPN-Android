package com.wlvpn.consumervpn.presentation.features.connection

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.wlvpn.consumervpn.R

private const val CONNECTION_CITY = "CONNECTION_CITY"

private const val CONNECTION_COUNTRY = "CONNECTION_COUNTRY"

class NewConnectionDialogFragment : DialogFragment(),
    DialogInterface.OnClickListener {

    /**
     * Companion will work for static values
     */
    companion object {
        /**
         * Static Identifier Dialog Name
         */
        const val TAG = "consumer:NewConnectionDialogFragment"

        /**
         * To Display new connection using as target city and country
         * @param city The target city to display in connection message
         * @param country The target country to display in connection message
         */
        fun newInstance(city: String?, country: String?): NewConnectionDialogFragment {
            val fragment = NewConnectionDialogFragment()

            val bundle = Bundle()

            bundle.putString(CONNECTION_CITY, city)
            bundle.putString(CONNECTION_COUNTRY, country)

            fragment.arguments = bundle

            return fragment
        }

        /**
         * Default new instance to display Fastest Available as connection target
         */
        fun newInstance(): NewConnectionDialogFragment {
            return NewConnectionDialogFragment()
        }
    }

    var onResultCallback: OnConnectionDialogResult? = null

    /**
     * On Create Dialog. Use this to create dialog instead of on create
     *
     * @param savedInstanceState Current saved instance. Always marked it as nullable
     * @return The new dialog. You can use dialog builder
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let {
            val city = arguments?.getString(CONNECTION_CITY)
            val country = arguments?.getString(CONNECTION_COUNTRY)

            val message =
                if (city == null && country == null)
                    getString(R.string.home_dialog_connection_message_fastest_available)
                else if (city == null)
                    getString(R.string.home_dialog_connection_message_fastest_available_country, country)
                else
                    getString(R.string.home_dialog_connection_message_city_country, city, country)

            // This one needs to be done manually
            isCancelable = false

            AlertDialog.Builder(requireContext())
                .setTitle(R.string.home_dialog_connection_title)
                .setMessage(message)
                .setPositiveButton(R.string.home_dialog_connection_connect, this)
                .setNegativeButton(R.string.home_dialog_connection_cancel, this)
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
        when (which) {
            DialogInterface.BUTTON_POSITIVE -> {
                onResultCallback?.onConnectionDialogResponse(DialogInterface.BUTTON_POSITIVE, TAG)
            }

            DialogInterface.BUTTON_NEGATIVE -> {
                onResultCallback?.onConnectionDialogResponse(DialogInterface.BUTTON_NEGATIVE, TAG)
            }
        }

        dismiss()
        // avoids a leak notification
        dialog?.let {
            onDismiss(dialog)
        }
    }

}