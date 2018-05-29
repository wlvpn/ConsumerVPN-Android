package com.wlvpn.slider.whitelabelvpn.utilities

import rx.Observable
import rx.Observable.Transformer
import rx.Scheduler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers


@JvmOverloads
fun <T> applySchedulers(
        subscribeScheduler: Scheduler = Schedulers.io(),
        observeScheduler: Scheduler = AndroidSchedulers.mainThread()
): Observable.Transformer<T, T> = Transformer { observable ->
    observable.subscribeOn(subscribeScheduler)
            .observeOn(observeScheduler)
}
