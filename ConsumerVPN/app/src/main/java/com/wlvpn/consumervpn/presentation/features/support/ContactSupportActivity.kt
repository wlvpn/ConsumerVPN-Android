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
import com.wlvpn.consumervpn.databinding.ActivityContactSupportBinding
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import java.util.concurrent.TimeUnit

private const val CLICK_DELAY_MILLISECONDS = 500L

class ContactSupportActivity : PresenterOwnerActivity<ContactSupportContract.Presenter>(),
    ContactSupportContract.View {

    private val clickDisposables = CompositeDisposable()

    lateinit var featureNavigator: FeatureNavigator

    private lateinit var binding: ActivityContactSupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContactSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Injector.INSTANCE.initViewComponent(this).inject(this)

        binding.checkBoxIncludeDiagnostics.clicks()
            .observeOn(AndroidSchedulers.mainThread())
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .subscribe {
                presenter.onIncludeLogsChanged(binding.checkBoxIncludeDiagnostics.isChecked)
            }
            .addTo(clickDisposables)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contact_support, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menuSend ->
                presenter.onSendCommentsClick(binding.problemDescriptionEditText.text.toString())
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun setLogs(logs: String) {
        binding.textViewDiagnosticInformation.text = logs
    }

    override fun logsVisibility(visibility: Boolean) {
        binding.checkBoxIncludeDiagnostics.isChecked = visibility
        binding.textViewDiagnosticInformation.visibility =
            if (visibility) View.VISIBLE else View.GONE
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
        binding.supportUserNotesTextInputLayout.error = when {
            visibility -> getString(R.string.support_label_empty_notes_error_message)
            else -> null
        }
    }

    override fun setLoadingViewVisibility(visibility: Boolean) {
        binding.progressBar.root.visibility = if (visibility) View.VISIBLE else View.GONE
    }
}