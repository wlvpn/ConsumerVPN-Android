package com.wlvpn.consumervpn.presentation.features.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.viewpager.widget.ViewPager
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.account.AccountExpiredDialogFragment
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import kotlinx.android.synthetic.main.activity_home.*

private const val CONNECT_TAB_POSITION = 0
private const val SERVER_TAB_POSITION = 1

class HomeActivity :
    PresenterOwnerActivity<HomeContract.Presenter>(),
    HomeContract.View,
    AccountExpiredDialogFragment.OnAccountExpiredDialogResult,
    ViewPager.OnPageChangeListener {

    private lateinit var homeTabsAdapter: HomeTabsAdapter

    companion object {
        const val REQUESTED_TAB_CHANGE_KEY = "REQUESTED_TAB_CHANGE_KEY"
        const val SERVERS_TAB_KEY = "SERVERS_TAB_KEY"
        const val CONNECT_TAB_KEY = "CONNECT_TAB_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        //View logic, no need to expose its contract
        setupViews()
    }

    override fun onResume() {
        super.onResume()
        checkTabRequestChange()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun showExpiredAccountDialog() {
        val dialogFragment = AccountExpiredDialogFragment()
        dialogFragment.onResultCallback = this
        dialogFragment.show(
            supportFragmentManager,
            AccountExpiredDialogFragment.TAG
        )
    }

    override fun onDialogResponse(resultCode: Int) {
        if (resultCode == AccountExpiredDialogFragment.RETRY_RESPONSE_CODE) {
            presenter.onExpiredAccountRetryClick()
        }
    }

    override fun progressDialogVisibility(isVisible: Boolean) {
        progressContainerHome.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showServerTab() {
        tabs.selectTab(tabs.getTabAt(SERVER_TAB_POSITION))
    }

    override fun showConnectTab() {
        tabs.selectTab(tabs.getTabAt(CONNECT_TAB_POSITION))
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        (homeTabsAdapter.getItem(position) as HomeViewPagerFragment<*>).onPageSelected()
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        homeTabsAdapter = HomeTabsAdapter(supportFragmentManager, applicationContext)
        viewPager.adapter = homeTabsAdapter
        viewPager.addOnPageChangeListener(this)
        tabs.setupWithViewPager(viewPager)
    }

    private fun checkTabRequestChange() {
        if (intent != null && intent.hasExtra(REQUESTED_TAB_CHANGE_KEY)) {
            when (intent.getStringExtra(REQUESTED_TAB_CHANGE_KEY)) {
                SERVERS_TAB_KEY -> presenter.onServerTabChangeRequest()
                CONNECT_TAB_KEY -> presenter.onConnectTabChangeRequest()
            }
        }
    }

}