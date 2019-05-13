package com.wlvpn.consumervpn.presentation.features.about

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.widget.TextView
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import com.wlvpn.consumervpn.presentation.util.bindView

class AboutActivity
    : PresenterOwnerActivity<AboutContract.Presenter>(), AboutContract.View {

    private val textLinkTos: TextView by bindView(R.id.txt_main_url)
    private val textVersionName: TextView by bindView(R.id.txt_version_name)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        textLinkTos.movementMethod = LinkMovementMethod.getInstance()
        textVersionName.text = getString(R.string.about_version_name, BuildConfig.VERSION_NAME)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

}
