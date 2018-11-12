package com.wlvpn.slider.whitelabelvpn.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gentlebreeze.http.api.NetworkUnavailableException;
import com.gentlebreeze.vpn.http.api.error.BaseErrorThrowable;
import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnLoginResponse;
import com.jakewharton.rxbinding.view.RxView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.auth.Credentials;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.helpers.PreferencesHelper;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kotlin.Unit;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * @see BaseActivity
 */
public class LoginActivity
        extends BaseActivity {

    @Inject
    public CredentialsManager credentialsManager;

    private View progressContainer;
    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotUsernamePasswordButton;
    private Button signUpButton;

    private ICallback<VpnLoginResponse> loginCallback;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_login);

        progressContainer = findViewById(R.id.progress_container);
        usernameEditText = findViewById(R.id.activity_login_et_username);
        passwordEditText = findViewById(R.id.activity_login_et_password);
        loginButton = findViewById(R.id.activity_login_button_sign_in);
        forgotUsernamePasswordButton = findViewById(R.id.login_tv_forgot_username_password);
        signUpButton = findViewById(R.id.activity_login_button_sign_up);

        passwordEditText.setOnEditorActionListener(getOnEditorActionListener());
    }

    /**
     * Resumes activity
     *
     * @see BaseActivity See base activity for subscriptions initialization
     */
    @Override
    protected void onResume() {
        super.onResume();
        initListeners();
    }

    private void unsubscribeLogin() {
        if (loginCallback != null) {
            loginCallback.unsubscribe();
            loginCallback = null;
        }
    }

    private void initListeners() {

        getMainSubscription().add(RxView.clicks(signUpButton)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> goToUri(Uri.parse(PreferencesHelper.getInstance()
                        .getSignUpUrl()))));

        getMainSubscription().add(RxView.clicks(forgotUsernamePasswordButton)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> goToUri(Uri.parse(PreferencesHelper.getInstance()
                        .getForgotPasswordUrl()))));

        getMainSubscription().add(RxView.clicks(loginButton)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> loginClicked()));

    }

    public void showProgressDialog(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void showAuthenticationMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void displayMessage(@StringRes int stringRes) {
        Toast.makeText(this, getString(stringRes), Toast.LENGTH_SHORT).show();
    }

    private void goToUri(Uri uri) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW).setData(uri));
        } catch (ActivityNotFoundException e) {
            Timber.e(e);
        }
    }

    /**
     * Validates the username/password and authenticates through SDK
     */
    public void loginClicked() {
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (!username.isEmpty() && !password.isEmpty()) {
            showProgressDialog(true);
            hideKeyboard();

            loginCallback = ConsumerVpnApplication.getVpnSdk()
                    .loginWithUsername(username, password)
                    .subscribe(vpnLoginResponse -> {
                        unsubscribeLogin();
                        credentialsManager.setCredentials(new Credentials(username, password));
                        goToMainActivity();
                        return Unit.INSTANCE;
                    }, throwable -> {
                        unsubscribeLogin();
                        showProgressDialog(false);
                        Timber.e(throwable, "Failed to login");
                        handleAuthError(throwable);
                        return Unit.INSTANCE;
                    });
        } else {
            displayMessage(R.string.username_password_invalid);
        }
    }

    /**
     * Set the correct login message on login error
     *
     * @param throwable login return throwable
     */
    private void handleAuthError(Throwable throwable) {
        if (throwable instanceof BaseErrorThrowable) {
            showAuthenticationMessage(throwable.getMessage());
        } else if (throwable instanceof NetworkUnavailableException) {
            displayMessage(R.string.no_connection);
        } else {
            displayMessage(R.string.login_unknown_error);
        }
    }

    private TextView.OnEditorActionListener getOnEditorActionListener() {
        return (textView, i, keyEvent) -> {
            if (i == EditorInfo.IME_ACTION_DONE || (keyEvent.getAction() == KeyEvent.ACTION_DOWN
                    && keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                loginClicked();
                return true;
            }
            return false;
        };
    }

}
