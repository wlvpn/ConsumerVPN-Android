package com.wlvpn.consumervpn.presentation.util

import io.reactivex.*
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction

/**
 * Simplifies zipping two Singles into a Pair
 */
fun <U, T> Single<T>.zipPair(other: SingleSource<U>): Single<Pair<T, U>> =
    zipWith(other, BiFunction { t1, t2 -> Pair(t1, t2) })

fun <T> Observable<T>.defaultSchedulers(schedulerProvider: SchedulerProvider): Observable<T> =
    subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui())

fun <T> Single<T>.defaultSchedulers(schedulerProvider: SchedulerProvider): Single<T> =
    subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui())

fun Completable.defaultSchedulers(schedulerProvider: SchedulerProvider): Completable =
    subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui())

fun <T> Maybe<T>.defaultSchedulers(schedulerProvider: SchedulerProvider): Maybe<T> =
    subscribeOn(schedulerProvider.computation()).observeOn(schedulerProvider.ui())

fun Disposable.isRunning(): Boolean = !isDisposed
