package com.wlvpn.consumervpn.presentation.features

import androidx.annotation.CallSuper

/**
 * Contract with basic functionality of all Views and Presenters.
 */
interface BaseContract {

    interface Presenter<View> {

        var view: View?

        fun bind(view: View) {
            this.view = view
        }

        fun start()

        fun unbind() {
            view = null
        }

        @CallSuper
        fun cleanUp() {
            if (view != null) {
                unbind()
            }
        }
    }

    interface View
}
