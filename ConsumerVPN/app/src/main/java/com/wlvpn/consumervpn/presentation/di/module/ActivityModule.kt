package com.wlvpn.consumervpn.presentation.di.module

import android.app.Activity
import com.wlvpn.consumervpn.presentation.navigation.DefaultFeatureNavigator
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import dagger.Module
import dagger.Provides

/**
 * A module that with any dependency that needs a activity context.
 *
 * No need to annotate dependencies with @PerView.
 */
@Module
class ActivityModule(private val activity: Activity) {

    @Provides
    fun providesActivity(): Activity = activity

    @Provides
    fun providesFeatureNavigator(activity: Activity): FeatureNavigator = DefaultFeatureNavigator(activity)

}