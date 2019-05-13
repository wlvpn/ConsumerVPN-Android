package com.wlvpn.consumervpn.presentation.navigation

/**
 * A class to navigate between all features.
 */
interface FeatureNavigator {

    /**
     * Navigates user to Login feature
     */
    fun navigateToLogin()

    /**
     * Navigates user to Home feature
     */
    fun navigateToHome()

    /**
     * Opens a web view or browser to show forget password page
     */
    fun navigateToForgotPasswordWebView(onWebViewNotSupported: () -> Unit)

    /**
     * Opens a web view or browser to show sign up page
     */
    fun navigateToSignUp(onWebViewNotSupported: () -> Unit)

    /**
     * Navigates to Settings feature
     */
    fun navigateToSettings()

    /**
     * Navigates to the About Screen
     * */
    fun navigateToAbout()

    /**
     * Navigates to server listType view
     */
    fun navigateToServersView()

    /**
     * Navigates to connection view
     */
    fun navigateToConnectView()


}