package com.wlvpn.slider.whitelabelvpn.settings;


import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SortPref {

    public static final int SORT_RESPONSE_TIME = 1;
    public static final int SORT_CITY = 2;
    public static final int SORT_COUNTRY = 3;
    public static final int SORT_SERVER = 4;

    @ServerSort
    private int sortMode;

    public SortPref(@ServerSort int sortMode) {
        this.sortMode = sortMode;
    }

    public int getSortMode() {
        return sortMode;
    }

    public void setSortMode(@ServerSort int sortMode) {
        this.sortMode = sortMode;
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({SORT_RESPONSE_TIME, SORT_CITY, SORT_COUNTRY, SORT_SERVER})
    public @interface ServerSort {
    }
}
