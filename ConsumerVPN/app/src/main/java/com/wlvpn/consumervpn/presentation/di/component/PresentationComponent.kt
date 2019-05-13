package com.wlvpn.consumervpn.presentation.di.component

import com.wlvpn.consumervpn.presentation.di.module.ActivityModule
import com.wlvpn.consumervpn.presentation.di.module.PresenterModule
import com.wlvpn.consumervpn.presentation.di.scope.PerPresentation
import dagger.Subcomponent

/**
 * Sub component of [ApplicationComponent], this holds presentation logic modules
 * (presenters, VM, controllers, etc). Lives in LifecycleAwarePresentationComponent.
 * Part of PerPresentation scope.
 *
 * WARNING: Do NOT add modules referencing activity or fragment context. This component is retained in an instance of
 * LifecycleAwarePresentationComponent that survives configuration changes. References to context will guaranty
 * memory leaks.
 */
@PerPresentation
@Subcomponent(modules = [PresenterModule::class])
interface PresentationComponent {

    /**
     * A method thar returns a freshly created [ViewComponent].
     * NOTE: this doesn't hold any reference to the context, is pass through to create a ViewComponent.
     *
     * @param module need to init ViewComponent
     * @return a new instance of ViewComponent
     */
    fun plus(module: ActivityModule): ViewComponent

    //no-op, there should not any injectors to any view, use [ViewComponent] instead.
}