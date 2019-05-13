package com.wlvpn.consumervpn.presentation.features.splash

import android.os.Bundle
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import javax.inject.Inject

class SplashActivity : PresenterOwnerActivity<SplashContract.Presenter>(), SplashContract.View {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injector.INSTANCE.initViewComponent(this).inject(this)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun navigateToLogin() {
        featureNavigator.navigateToLogin()
    }

    override fun navigateToHome() {
        featureNavigator.navigateToHome()
    }
}