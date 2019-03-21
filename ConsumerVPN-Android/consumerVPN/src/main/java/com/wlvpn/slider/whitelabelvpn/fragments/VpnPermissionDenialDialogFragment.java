package com.wlvpn.slider.whitelabelvpn.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.activities.MainActivity;

import timber.log.Timber;

/**
 * Vpn permission denial dialog.
 */
public class VpnPermissionDenialDialogFragment extends DialogFragment {

    public static final String TAG = VpnPermissionDenialDialogFragment.class.getCanonicalName();

    private DialogFragmentCallBack dialogFragmentCallBack;

    @NonNull
    public static VpnPermissionDenialDialogFragment newInstance() {
        return new VpnPermissionDenialDialogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.WLVPNAlertDialogTheme);

        String body = getActivity().getString(
                R.string.vpn_permission_dialog_body,
                getActivity().getString(R.string.app_name));

        StringBuilder messageBuilder = new StringBuilder(body);

        // alwaysOnVPN is available since Android N
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            String vpnPermissionDenial =
                    getActivity().getString(R.string.vpn_permission_denial_dialog_always_on_vpn);

            messageBuilder.append(vpnPermissionDenial);

            builder.setNeutralButton(R.string.vpn_permission_action_go, (dialog, which) -> {
                try {
                    Intent settingsIntent = new Intent(Settings.ACTION_VPN_SETTINGS);
                    settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(settingsIntent, MainActivity.VPN_PREPARE);
                } catch (ActivityNotFoundException e) {
                    Timber.e(e, "VPN settings not found");
                    Toast.makeText(getContext(), R.string.vpn_permissions_not_found,
                            Toast.LENGTH_LONG).show();
                }
            });
        }

        return builder.setTitle(R.string.vpn_permission_dialog_title)
                .setMessage(messageBuilder.toString())
                .setPositiveButton(R.string.vpn_permission_action_retry, (dialog, which) -> {
                    try {
                        dismiss();
                        dialogFragmentCallBack.onDialogButtonClick();
                    } catch (NullPointerException e) {
                        Timber.e(e, "Rotation on Status activity");
                    }
                }).create();
    }

    public void setDialogFragmentCallBack(
            DialogFragmentCallBack dialogFragmentCallBack) {
        this.dialogFragmentCallBack = dialogFragmentCallBack;
    }

    /**
     * Interface to be used as a callback on an alert dialog positive button.
     */
    public interface DialogFragmentCallBack {
        void onDialogButtonClick();
    }

}
