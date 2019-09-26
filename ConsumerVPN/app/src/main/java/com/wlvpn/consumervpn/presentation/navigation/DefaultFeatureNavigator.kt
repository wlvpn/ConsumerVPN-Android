package com.wlvpn.consumervpn.presentation.navigation

import android.app.Activity
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.net.Uri
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.features.about.AboutActivity
import com.wlvpn.consumervpn.presentation.features.home.HomeActivity
import com.wlvpn.consumervpn.presentation.features.login.LoginActivity

/**
 * A navigator that uses context to start activities or fragments.
 * @param context the context starting the navigation.
 */
class DefaultFeatureNavigator(private val context: Activity) : FeatureNavigator {

    override fun navigateToLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        context.startActivity(intent)
    }

    override fun navigateToHome() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
    }

    override fun navigateToForgotPasswordWebView(onWebViewNotSupported: () -> Unit) {
        openWebViewWithUrl(context.getString(R.string.url_forgot_password), onWebViewNotSupported)
    }

    override fun navigateToSignUp(onWebViewNotSupported: () -> Unit) {
        openWebViewWithUrl(context.getString(R.string.url_sign_up), onWebViewNotSupported)
    }

    override fun navigateToAbout() {
        val intent = Intent(context, AboutActivity::class.java)
        context.startActivity(intent)
    }

    override fun navigateToServersView() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(
                HomeActivity.REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY,
                HomeActivity.SERVERS_BOTTOM_NAVIGATION_KEY
            )
        context.startActivity(intent)
    }

    override fun navigateToConnectView() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(
                HomeActivity.REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY,
                HomeActivity.CONNECT_BOTTOM_NAVIGATION_KEY
            )
        context.startActivity(intent)
    }

    override fun navigateToSettings() {
        val intent = Intent(context, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            .putExtra(
                HomeActivity.REQUESTED_BOTTOM_NAVIGATION_CHANGE_KEY,
                HomeActivity.SETTINGS_BOTTOM_NAVIGATION_KEY
            )
        context.startActivity(intent)
    }

    private fun openWebViewWithUrl(url: String, onWebViewNotSupported: () -> Unit) {
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
        } catch (e: Exception) {
            onWebViewNotSupported()
        }
    }

}