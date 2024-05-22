package com.wlvpn.consumervpn.data.util

import com.gentlebreeze.vpn.sdk.callback.ICallback
import io.reactivex.Observable
import io.reactivex.Single

/**
 *  This extension helps to transform ANY SDK [ICallback] into a Single observable object
 */
fun <T:Any> ICallback<T>.toSingle(): Single<T> =
    Single.create<T> {
        blockCurrentThread()
        subscribe(it::onSuccess) { throwable -> it.tryOnError(throwable) }
    }
        .doOnDispose { unsubscribe() }

fun <T:Any> ICallback<T>.toObservable(): Observable<T> =
    Observable.create<T> {
        blockCurrentThread()
        subscribe(it::onNext) { throwable -> it.tryOnError(throwable) }
    }.doOnDispose { unsubscribe() }