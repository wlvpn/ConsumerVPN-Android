package com.wlvpn.consumervpn.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.features.about.AboutActivity
import com.wlvpn.consumervpn.presentation.features.home.HomeActivity
import com.wlvpn.consumervpn.presentation.features.login.LoginActivity
import com.wlvpn.consumervpn.presentation.features.settings.SettingsActivity

/**
 * A navigator that uses context to start activities or fragments.
 * @param context the context starting the navigation.
 */
class DefaultFeatureNavigator(private val context: Activity) : FeatureNavigator {

    override fun navigateToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        context.startActivity(intent)
    }

    override fun navigateToHome() {
        val intent = Intent(context, HomeActivity::class.java)
        context.startActivity(intent)
    }

    override fun navigateToForgotPasswordWebView(onWebViewNotSupported: () -> Unit) {
        openWebViewWithUrl(context.getString(R.string.url_forgot_password), onWebViewNotSupported)
    }

    override fun navigateToSignUp(onWebViewNotSupported: () -> Unit) {
        openWebViewWithUrl(context.getString(R.string.url_sign_up), onWebViewNotSupported)
    }

    override fun navigateToSettings() {
        context.startActivity(Intent(context, SettingsActivity::class.java))
    }

    override fun navigateToAbout() {
        val intent = Intent(context, AboutActivity::class.java)
        context.startActivity(intent)
    }

    private fun openWebViewWithUrl(url: String, onWebViewNotSupported: () -> Unit) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
        } catch (e: Exception) {
            onWebViewNotSupported()
        }
    }

    override fun navigateToServersView() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(HomeActivity.REQUESTED_TAB_CHANGE_KEY, HomeActivity.SERVERS_TAB_KEY)
        context.startActivity(intent)
    }

    override fun navigateToConnectView() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        intent.putExtra(HomeActivity.REQUESTED_TAB_CHANGE_KEY, HomeActivity.CONNECT_TAB_KEY)
        context.startActivity(intent)
    }

}