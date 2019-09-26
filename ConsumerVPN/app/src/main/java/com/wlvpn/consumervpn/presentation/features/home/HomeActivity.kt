package com.wlvpn.consumervpn.presentation.features.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.account.AccountExpiredDialogFragment
import com.wlvpn.consumervpn.presentation.features.home.connection.ConnectionFragment
import com.wlvpn.consumervpn.presentation.features.home.servers.ServersFragment
import com.wlvpn.consumervpn.presentation.features.settings.SettingsPreferenceFragment
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import kotlinx.android.synthetic.main.activity_home.*

private const val FRAGMENT_TAG_KEY = "HOME_FRAGMENT_KEY"

class HomeActivity :
    PresenterOwnerActivity<HomeContract.Presenter>(),
    HomeContract.View,
    AccountExpiredDialogFragment.OnAccountExpiredDialogResult,
    BottomNavigationView.OnNavigationItemSelectedListener {

    private var currentFragmentTag = ""

    companion object {
        const val REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY = "REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY"
        const val CONNECT_BOTTOM_NAVIGATION_KEY = "CONNECT_BOTTOM_NAVIGATION_KEY"
        const val SERVERS_BOTTOM_NAVIGATION_KEY = "SERVERS_BOTTOM_NAVIGATION_KEY"
        const val SETTINGS_BOTTOM_NAVIGATION_KEY = "SETTINGS_BOTTOM_NAVIGATION_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        // Sets initial fragment tag
        currentFragmentTag = savedInstanceState?.let { bundle ->
            bundle.getString(FRAGMENT_TAG_KEY, ConnectionFragment.TAG)
        } ?: run {
            ConnectionFragment.TAG
        }

        //View logic, no need to expose its contract
        setupViews()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(FRAGMENT_TAG_KEY, currentFragmentTag)
        super.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        checkNavigationChangeRequest()
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

    override fun showConnectionView() {
        showFragment(ConnectionFragment.TAG)
    }

    override fun showServersView() {
        showFragment(ServersFragment.TAG)
    }

    override fun showSettingsView() {
        showFragment(SettingsPreferenceFragment.TAG)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_connection -> {
                presenter.onConnectFragmentChangeRequest()
                return true
            }

            R.id.action_servers -> {
                presenter.onServerFragmentChangeRequest()
                return true
            }

            R.id.action_settings -> {
                presenter.onSettingsFragmentChangeRequest()
                return true
            }
        }

        return false
    }

    private fun showFragment(fragmentTag: String) {
        var fragment = supportFragmentManager.findFragmentByTag(fragmentTag)

        if (fragment == null) {
            fragment = when (fragmentTag) {
                ConnectionFragment.TAG -> {
                    ConnectionFragment()
                }
                ServersFragment.TAG -> {
                    ServersFragment()
                }
                SettingsPreferenceFragment.TAG -> {
                    SettingsPreferenceFragment()
                }
                else -> throw NotImplementedError()
            }
            currentFragmentTag = fragmentTag
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, fragment, fragmentTag)
                .commit()
        }
    }

    private fun setupViews() {
        setSupportActionBar(toolbar)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)

        showFragment(currentFragmentTag)
    }

    private fun checkNavigationChangeRequest() {
        if (intent != null && intent.hasExtra(REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY)) {
            when (intent.getStringExtra(REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY)) {
                CONNECT_BOTTOM_NAVIGATION_KEY -> bottomNavigationView.selectedItemId = R.id.action_connection
                SERVERS_BOTTOM_NAVIGATION_KEY -> bottomNavigationView.selectedItemId = R.id.action_servers
                SETTINGS_BOTTOM_NAVIGATION_KEY -> bottomNavigationView.selectedItemId = R.id.action_settings
            }
        }
    }

}