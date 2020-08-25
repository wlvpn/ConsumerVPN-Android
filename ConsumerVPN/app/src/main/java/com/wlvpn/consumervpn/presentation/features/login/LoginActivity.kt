package com.wlvpn.consumervpn.presentation.features.login

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CLICK_DELAY_MILLISECONDS = 500L

class LoginActivity :
    PresenterOwnerActivity<LoginContract.Presenter>(),
    LoginContract.View,
    TextView.OnEditorActionListener {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private val clickDisposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        editTextPassword.editText?.setOnEditorActionListener(this)

        clickDisposables.add(buttonLogin.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe {
                presenter.onLoginClick(
                    editTextUsername.editText?.text.toString(),
                    editTextPassword.editText?.text.toString()
                )
            })

        clickDisposables.add(buttonSignUp.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe { presenter.onSignUpClick() })

        clickDisposables.add(buttonForgotPassword.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe { presenter.onForgotPasswordClick() })

    }

    override fun onDestroy() {
        clickDisposables.clear()
        super.onDestroy()
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun showForgotPassword() {
        featureNavigator.navigateToForgotPasswordWebView {
            presenter.onOpenForgotPasswordNotSupported()
        }
    }

    override fun showExternalLinksNotSupportedMessage() {
        Toast.makeText(this, getString(R.string.error_links_not_supported), Toast.LENGTH_SHORT).show()
    }

    override fun showEmptyUserOrPasswordMessage() {
        val error = getString(R.string.error_login_username_password_invalid)
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        editTextUsername.error = error
        editTextPassword.error = error
    }

    override fun dismissErrorMessage() {
        editTextUsername.error = null
        editTextPassword.error = null
    }

    override fun progressDialogVisibility(isVisible: Boolean) {
        progressBar.visibility = if (isVisible) View.VISIBLE else View.GONE
    }

    override fun showErrorMessage(errorMessage: String) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun showHome() {
        featureNavigator.navigateToHome()
        finish()
    }

    override fun showSignUp() {
        featureNavigator.navigateToSignUp {
            presenter.onOpenSignUpdNotSupported()
        }
    }

    override fun hideKeyboard() {
        val view = this.currentFocus
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null && imm != null) {
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    override fun onEditorAction(textView: TextView?, actionId: Int, keyEvent: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_DONE || (keyEvent != null
                    && keyEvent.action == KeyEvent.ACTION_DOWN
                    && keyEvent.keyCode == KeyEvent.KEYCODE_ENTER)
        ) {
            presenter.onLoginClick(
                editTextUsername.editText?.text.toString(),
                editTextPassword.editText?.text.toString()
            )

            return true
        }

        return false
    }

    override fun showInvalidCredentialsMessage() {
        showErrorMessage(getString(R.string.error_login_username_password_invalid))
    }

    override fun showNoNetworkMessage() {
        showErrorMessage(getString(R.string.error_no_connection))
    }

    override fun showUnknownErrorMessage() {
        showErrorMessage(getString(R.string.error_unknown_error))
    }
}