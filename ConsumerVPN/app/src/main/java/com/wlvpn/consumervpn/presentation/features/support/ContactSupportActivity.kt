package com.wlvpn.consumervpn.presentation.features.support

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_contact_support.*
import java.util.concurrent.TimeUnit

private const val CLICK_DELAY_MILLISECONDS = 500L

class ContactSupportActivity : PresenterOwnerActivity<ContactSupportContract.Presenter>(),
    ContactSupportContract.View {

    private val clickDisposables = CompositeDisposable()

    lateinit var featureNavigator: FeatureNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_support)
        Injector.INSTANCE.initViewComponent(this).inject(this)

        checkBoxIncludeDiagnostics.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe { presenter.onIncludeLogsChanged(checkBoxIncludeDiagnostics.isChecked) }
            .addTo(clickDisposables)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact_support, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSend ->
                presenter.onSendCommentsClick(problemDescriptionEditText.text.toString())
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun setLogs(logs: String) {
        textViewDiagnosticInformation.text = logs
    }

    override fun logsVisibility(visibility: Boolean) {
        checkBoxIncludeDiagnostics.isChecked = visibility
        textViewDiagnosticInformation.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    override fun showVisitSupportWebsiteDialog() {
        VisitSupportWebsiteDialog().apply {
            onVisitWebsiteClick = {
                presenter.onVisitSupportWebsiteSelected()
            }
            show(supportFragmentManager, VisitSupportWebsiteDialog.TAG)
        }
    }

    override fun openSupportWebsite(supportUrl: String) {
        try {
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(supportUrl)
                startActivity(this)
            }
        } catch (exception: Exception) {
            presenter.onOpenLinksNotSupported()
        }
    }

    override fun showContactSupportMessage(supportUrl: String) {
        Toast.makeText(
            this,
            getString(R.string.support_toast_contact_support, supportUrl),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun setEmptyCommentsMessageVisibility(visibility: Boolean) {
        supportUserNotesTextInputLayout.error = when {
            visibility -> getString(R.string.support_label_empty_notes_error_message)
            else -> null
        }
    }

    override fun setLoadingViewVisibility(visibility: Boolean) {
        progressBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }
}