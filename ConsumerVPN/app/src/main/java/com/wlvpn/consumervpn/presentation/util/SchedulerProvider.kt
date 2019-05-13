package com.wlvpn.consumervpn.presentation.util

import io.reactivex.Scheduler

/**
 * A custom scheduler Provider contract.
 */
interface SchedulerProvider {
    fun ui(): Scheduler
    fun computation(): Scheduler
    fun trampoline(): Scheduler
    fun newThread(): Scheduler
    fun io(): Scheduler
}