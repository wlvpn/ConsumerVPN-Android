package com.wlvpn.slider.whitelabelvpn.layouts;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;
import com.gentlebreeze.vpn.sdk.model.VpnConnectionInfo;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.settings.CipherPref;

import javax.inject.Inject;

public class ConnectedLayout extends ConstraintLayout {

    private static final int PROTOCOL_OPTION_SECURE = 0;
    private static final int PROTOCOL_OPTION_BALANCED = 1;
    private static final int PROTOCOL_OPTION_FAST = 2;

    @Inject
    public SettingsManager settingsManager;

    private TextView publicIpText;
    private TextView visibleLocationText;
    private TextView protocolOptionText;
    private TextView encryptionLabelText;
    private PathView encryptionImage;

    public ConnectedLayout(Context context) {
        super(context);

        ConsumerVpnApplication.component().inject(this);

        inflate(getContext(), R.layout.layout_connected, this);

        publicIpText = findViewById(R.id.fragment_connected_tv_public_ip);
        visibleLocationText = findViewById(R.id.connected_visible_location_text);
        protocolOptionText = findViewById(R.id.fragment_connected_tv_protocol_option);
        encryptionLabelText = findViewById(R.id.connected_tv_encryption);
        encryptionImage = findViewById(R.id.connected_ic_encryption);

        initCipherOption();
        initServerConnectionDisplay();
    }

    public void setPublicIpText(String ipText) {
        publicIpText.setText(ipText);
    }

    public void setVisibleLocationText(String locationText) {
        visibleLocationText.setText(locationText);
    }

    public void setProtocolOptionText(@StringRes int stringRes) {
        protocolOptionText.setText(stringRes);
    }

    public void setEncryptionLabelText(@StringRes int stringRes) {
        encryptionLabelText.setText(stringRes);
    }

    public void setEncryptionImage(@RawRes int rawRes) {
        encryptionImage.setSvgResource(rawRes);

        encryptionImage.getPathAnimator()
                .delay(100)
                .duration(400)
                .interpolator(new AccelerateDecelerateInterpolator())
                .start();
    }

    private void initServerConnectionDisplay() {
        VpnConnectionInfo vpnConnectionInfo = ConsumerVpnApplication.getVpnSdk().getConnectionInfo();
        String location = String.format(
                "%s, %s", vpnConnectionInfo.getCity(), vpnConnectionInfo.getCountry()
        );
        setPublicIpText(vpnConnectionInfo.getIpAddress());
        setVisibleLocationText(location);
    }

    private void initCipherOption() {
        CipherPref cipherPref = settingsManager.getCipherPref();
        switch (cipherPref.getCipher()) {
            case PROTOCOL_OPTION_FAST:
                setProtocolOptionText(R.string.encryption_none_title);
                setEncryptionLabelText(R.string.fastest_title);
                setEncryptionImage(R.raw.ic_fastest);
                break;
            case PROTOCOL_OPTION_BALANCED:
                setProtocolOptionText(R.string.encryption_fast_title);
                setEncryptionLabelText(R.string.fast_title);
                setEncryptionImage(R.raw.ic_fast);
                break;
            case PROTOCOL_OPTION_SECURE:
                setProtocolOptionText(R.string.encryption_secure_title);
                setEncryptionLabelText(R.string.secure_title);
                setEncryptionImage(R.raw.ic_secure);
                break;
            default:
                //Do Nothing
        }
    }
}