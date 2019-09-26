package com.wlvpn.consumervpn.presentation.di.component

import com.wlvpn.consumervpn.presentation.di.module.ActivityModule
import com.wlvpn.consumervpn.presentation.di.scope.PerView
import com.wlvpn.consumervpn.presentation.features.about.AboutActivity
import com.wlvpn.consumervpn.presentation.features.home.HomeActivity
import com.wlvpn.consumervpn.presentation.features.home.connection.ConnectionFragment
import com.wlvpn.consumervpn.presentation.features.home.servers.ServersFragment
import com.wlvpn.consumervpn.presentation.features.login.LoginActivity
import com.wlvpn.consumervpn.presentation.features.settings.SettingsPreferenceFragment
import com.wlvpn.consumervpn.presentation.features.splash.SplashActivity
import dagger.Subcomponent

/**
 * Sub component of [PresentationComponent], holds any view logic modules that could need a reference
 * to a context.
 *
 * Is a short life component which dies when the view dies (activity, fragment, custom view, dialog etc.).
 * Is safe to add modules with context references.(TODO needs more testing :p)
 *
 * Part of PerView Scope.
 */
@PerView
@Subcomponent(modules = [ActivityModule::class])
interface ViewComponent {

    fun inject(into: SplashActivity)

    fun inject(into: LoginActivity)

    fun inject(into: HomeActivity)

    fun inject(into: ConnectionFragment)

    fun inject(into: ServersFragment)

    fun inject(into: SettingsPreferenceFragment)

    fun inject(into: AboutActivity)

}