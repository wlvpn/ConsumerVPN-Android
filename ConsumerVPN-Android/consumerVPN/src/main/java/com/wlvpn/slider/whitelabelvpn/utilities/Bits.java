package com.wlvpn.slider.whitelabelvpn.utilities;

import java.util.Locale;

/**
 * Utility for handling bytes.
 */
public final class Bits {

    private static final double LOG_1000 = Math.log(1000);
    private static final String SI_UNITS = "kMGTPE";
    private static final long BITS_PER_BYTE = 8L;

    private Bits() {
    }

    /**
     * Convert bytes to bits.
     *
     * @param bytes byte count to covert
     * @return number of bits represented by the bytes.
     */
    public static long bytesToBits(long bytes) {
        return bytes * BITS_PER_BYTE;
    }

    /**
     * Convert bits to SI format.
     *
     * @param bits number of bits
     * @return formatted bits
     * @see <a href="http://stackoverflow.com/a/3758880/1286667">Source Concept</a>
     */
    public static String toSI(long bits) {
        if (bits < 1000) {
            return bits + " b";
        } else {
            final int exp = (int) (Math.log(bits) / LOG_1000);
            final char unit = SI_UNITS.charAt(exp - 1);
            final double bytesInUnit = bits / Math.pow(1000, exp);
            return String.format(Locale.ENGLISH, "%.1f %sb", bytesInUnit, unit);
        }
    }

}
