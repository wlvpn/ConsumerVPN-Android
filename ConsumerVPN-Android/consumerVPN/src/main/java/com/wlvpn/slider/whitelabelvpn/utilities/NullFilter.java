package com.wlvpn.slider.whitelabelvpn.utilities;

import rx.functions.Func1;

/**
 * Function for removing null objects from a stream.
 *
 * @param <T> stream object type
 */
public final class NullFilter<T> implements Func1<T, Boolean> {

    @Override
    public Boolean call(T t) {
        return t != null;
    }

}
