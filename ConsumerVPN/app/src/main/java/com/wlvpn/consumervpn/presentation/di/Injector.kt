package com.wlvpn.consumervpn.presentation.di

import androidx.appcompat.app.AppCompatActivity
import com.wlvpn.consumervpn.presentation.ConsumerApplication
import com.wlvpn.consumervpn.presentation.di.component.ViewComponent
import com.wlvpn.consumervpn.presentation.di.component.ApplicationComponent
import com.wlvpn.consumervpn.presentation.di.component.DaggerApplicationComponent
import com.wlvpn.consumervpn.presentation.di.module.ActivityModule
import com.wlvpn.consumervpn.presentation.di.module.AppModule

/**
 * Utility DI class, handles injection and holds ApplicationComponent
 */
enum class Injector {
    INSTANCE;

    /**
     * ApplicationComponent will live here instead of the application, so we don't need to do funny casting like this:
     * (applicationContext as CustomApplication).applicationComponent
     */
    var applicationComponent: ApplicationComponent? = null

    /**
     * Creates a new applicationComponent.
     *
     * @param app a reference to [ConsumerApplication] need in [AppModule]
     */
    fun initAppComponent(app: ConsumerApplication) {
        INSTANCE.applicationComponent = DaggerApplicationComponent.builder().appModule(AppModule(app)).build()
    }

    /**
     * Creates a new [ViewComponent].
     *
     * @param context a reference to a AppCompatActivity
     * @return a ViewComponent
     */
    fun initViewComponent(context: AppCompatActivity): ViewComponent {
        val holder = LifecycleAwarePresentationComponent.create(context)

        if (holder.component == null) {
            holder.component = INSTANCE.applicationComponent?.presenterComponent
        }

        //We need a valid ViewComponent, applicationComponent is only null if the app is actually destroyed.
        return requireNotNull(holder.component?.plus(ActivityModule(context))){
            "Trying to create dagger components with a destroyed Application/ComponentHolder."
        }
    }
}