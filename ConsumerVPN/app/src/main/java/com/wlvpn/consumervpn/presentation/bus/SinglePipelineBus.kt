package com.wlvpn.consumervpn.presentation.bus

import io.reactivex.Notification
import io.reactivex.Observable
import io.reactivex.subjects.UnicastSubject

/**
 * A Single pipeline event bus. Uses an UnicastSubject to ensure the event is cleared after
 * is posted.
 *
 * Use children of [Event] to send events.
 */
open class SinglePipelineBus<T : Event> {

    private var lock = Any()
    private var subject: UnicastSubject<Notification<T>> = UnicastSubject.create()

    /**
     * Post the given event to subscribers.
     * This a synchronized.
     *
     * @param event An event of type [T] to send
     */
    fun post(event: T) = synchronized(lock) {
        subject.onNext(Notification.createOnNext(event))
    }

    /**
     * Returns the bus as an observable.
     * This method will never return an error.
     *
     * @return this bus as an observable
     */
    fun asObservable(): Observable<T> = subject
        .onErrorReturn { Notification.createOnError(it) }
        .filter { !it.isOnError }
        .map { it.value }
}



