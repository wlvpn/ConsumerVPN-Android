package com.wlvpn.consumervpn.presentation.bus


/**
 *  Parent event for stateless child events.
 *  any child should be defined as an [object] to ensure no state is added.
 */
sealed class Event {
    object ConnectionRequestEvent : Event()
}


