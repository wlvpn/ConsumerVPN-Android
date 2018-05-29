package com.wlvpn.slider.whitelabelvpn.settings;

import android.support.annotation.IntDef;

import com.wlvpn.slider.whitelabelvpn.BuildConfig;

public class CipherPref {

    public static final int CIPHER_AES256 = 0;
    public static final int CIPHER_AES128 = 1;
    public static final int CIPHER_NONE = 2;

    @Cipher
    private final int cipher;

    public CipherPref(@Cipher int cipher) {
        this.cipher = cipher;
    }

    public int getCipher() {
        return cipher;
    }

    public String getValue() {
        switch (cipher) {
            case CIPHER_NONE:
                return "none";
            case CIPHER_AES128:
                return "AES-128-CBC";
            case CIPHER_AES256:
                return "AES-256-CBC";
            default:
                return "none";
        }
    }

    @IntDef({CIPHER_NONE, CIPHER_AES128, CIPHER_AES256})
    public @interface Cipher {
    }


    /**
     * Helper to translate cipher position for view pager and tabs
     *
     * @param cipher The stored position to translate
     * @return The new position
     */
    public static int getCipherToPosition(int cipher) {

        switch (cipher) {
            case CIPHER_AES256:
                break;
            case CIPHER_AES128:
                if (!BuildConfig.CONNECTION_TABS.get(0)) {
                    return 0;
                }

                break;

            case CIPHER_NONE:
                if (!BuildConfig.CONNECTION_TABS.get(0) && !BuildConfig.CONNECTION_TABS.get(1)) {
                    return 0;
                }

                if (!BuildConfig.CONNECTION_TABS.get(1) || !BuildConfig.CONNECTION_TABS.get(0)) {
                    return 1;
                }

                break;

            default:
                return -1;
        }

        return cipher;
    }

    /**
     * Helper to translate cipher tab position into settings
     *
     * @param position The stored position to translate
     * @return The new cipher option
     */
    public static int getPositionToCipher(int position) {

        switch (position) {
            case 0:
                if (!BuildConfig.CONNECTION_TABS.get(0) && !BuildConfig.CONNECTION_TABS.get(1)) {
                    return CIPHER_NONE;
                }

                if (!BuildConfig.CONNECTION_TABS.get(0) && !BuildConfig.CONNECTION_TABS.get(2)) {
                    return CIPHER_AES128;
                }

                if (!BuildConfig.CONNECTION_TABS.get(1) && !BuildConfig.CONNECTION_TABS.get(2)) {
                    return CIPHER_AES256;
                }

                if (!BuildConfig.CONNECTION_TABS.get(0)) {
                    return CIPHER_AES128;
                }

                break;
            case 1:
                if (!BuildConfig.CONNECTION_TABS.get(0) || !BuildConfig.CONNECTION_TABS.get(1)) {
                    return CIPHER_NONE;
                }

                break;

            case 2:
                break;

            default:
                return -1;
        }

        return position;
    }
}
