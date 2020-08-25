package com.wlvpn.consumervpn.presentation.features.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.Button
import android.widget.TextView
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import com.wlvpn.consumervpn.presentation.util.bindView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CLICK_DELAY_MILLISECONDS = 500L

class AboutActivity
    : PresenterOwnerActivity<AboutContract.Presenter>(), AboutContract.View {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private val textLinkTos: TextView by bindView(R.id.txt_main_url)
    private val textVersionName: TextView by bindView(R.id.txt_version_name)
    private val buttonLicenses: Button by bindView(R.id.about_licenses_button)

    private val clickDisposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        textLinkTos.movementMethod = LinkMovementMethod.getInstance()
        textVersionName.text = getString(R.string.about_version_name, BuildConfig.VERSION_NAME)
        setupClickViews()
    }

    override fun onDestroy() {
        clickDisposables.clear()
        super.onDestroy()
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    private fun setupClickViews() {
        clickDisposables.add(buttonLicenses.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                featureNavigator.navigateToLicensesView()
            })
    }

}
