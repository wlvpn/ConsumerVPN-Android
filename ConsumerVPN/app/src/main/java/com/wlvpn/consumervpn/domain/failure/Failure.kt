package com.wlvpn.consumervpn.domain.failure

/**
 * This a base class to all failure models, this are intended to not to be thrown, instead
 * we should just pas them as error models (through RxJava observables or anything else)
 */
open class Failure(message: String?) : Throwable(message)