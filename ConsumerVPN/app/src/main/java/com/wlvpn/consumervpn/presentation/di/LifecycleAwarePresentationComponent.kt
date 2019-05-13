package com.wlvpn.consumervpn.presentation.di

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.wlvpn.consumervpn.presentation.di.component.PresentationComponent

typealias LifecycleAwareHolder = ViewModel

/**
 * This class persists a PresentationComponent through configurations changes.
 *
 * Thanks to [LifecycleAwareHolder].
 */
class LifecycleAwarePresentationComponent : LifecycleAwareHolder() {

    var component: PresentationComponent? = null

    override fun onCleared() {
        component = null
        super.onCleared()
    }

    companion object {
        /**
         * "Creates" this holder, uses ViewModelProviders to instantiate it.
         */
        fun <C : AppCompatActivity> create(context: C): LifecycleAwarePresentationComponent {
            return ViewModelProviders.of(context).get(LifecycleAwarePresentationComponent::class.java)
        }
    }

}