package com.wlvpn.slider.whitelabelvpn.utilities;


import java.util.List;

public final class ListUtil {

    private ListUtil() {
    }

    /**
     * Add a value to a list and check that the list size is trimmed to a maximum length.
     *
     * @param value     to be added
     * @param list      list to add the value to
     * @param maxLength maximum length of the list
     * @param <T>       the type of the list and value
     */
    static <T> void addValueAndTrim(T value, List<? super T> list, int maxLength) {
        list.add(value);

        while (list.size() > maxLength) {
            list.remove(0);
        }
    }

    /**
     * Add a value to a front of a list and check that the list size is trimmed to a maximum length.
     *
     * @param value     to be added
     * @param list      list to add the value to
     * @param maxLength maximum length of the list
     * @param <T>       the type of the list and value
     */
    static <T> void addValueToFrontAndTrim(T value, List<? super T> list, int maxLength) {
        list.add(0, value);

        if (list.size() > maxLength) {
            list.remove(list.size() - 1);
        }
    }

}
