package com.wlvpn.consumervpn.presentation.util

import java.util.*

fun <T> Stack<T>.peekOrNull() : T? {
    return if (!isEmpty()) { peek() }
    else null
}