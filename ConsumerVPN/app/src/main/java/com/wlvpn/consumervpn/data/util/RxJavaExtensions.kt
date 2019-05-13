package com.wlvpn.consumervpn.data.util

import io.reactivex.*

fun <T> Single<T>.onErrorMapThrowable(mapper: (throwable: Throwable) -> Throwable): Single<T> =
    onErrorResumeNext { throwable: Throwable -> Single.error(mapper(throwable)) }

fun Completable.onErrorMapThrowable(mapper: (throwable: Throwable) -> Throwable): Completable =
    onErrorResumeNext { throwable: Throwable -> Completable.error(mapper(throwable)) }

fun <T> Maybe<T>.onErrorMapThrowable(mapper: (throwable: Throwable) -> Throwable): Maybe<T> =
    onErrorResumeNext { throwable: Throwable -> Maybe.error(mapper(throwable)) }

fun <T> Observable<T>.onErrorMapThrowable(mapper: (throwable: Throwable) -> Throwable): Observable<T> =
    onErrorResumeNext { throwable: Throwable -> Observable.error(mapper(throwable)) }

fun <T> Flowable<T>.onErrorMapThrowable(mapper: (throwable: Throwable) -> Throwable): Flowable<T> =
    onErrorResumeNext { throwable: Throwable -> Flowable.error(mapper(throwable)) }
