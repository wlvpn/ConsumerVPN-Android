package com.wlvpn.consumervpn.presentation.features.home.connection

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Group
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.widget.ConnectionStateView
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerActivity
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerFragment
import com.wlvpn.consumervpn.presentation.util.bindView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CLICK_DELAY_MILLISECONDS = 500L
private const val PREPARE_VPN_SERVICE: Int = 1000

class ConnectionFragment
    : PresenterOwnerFragment<ConnectionContract.Presenter>(),
    ConnectionContract.View {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private val disconnectedStateLayout: View by bindView(R.id.disconnected_state_layout)
    private val connectionStateLayout: View by bindView(R.id.connection_state_layout)
    private val viewConnecting: ConnectionStateView by bindView(R.id.view_connection_state)
    private val locationTextView: TextView by bindView(R.id.text_view_location)
    private val ipTextView: TextView by bindView(R.id.text_view_ip)
    private val connectedLocationTextView: TextView by bindView(R.id.text_view_connected_location)
    private val buttonConnect: Button by bindView(R.id.button_connect)
    private val buttonDisconnect: Button by bindView(R.id.button_disconnect)
    private val groupConnected: Group by bindView(R.id.group_connected)

    private val clickDisposables = CompositeDisposable()

    companion object {
        val TAG = "${BuildConfig.APPLICATION_ID}:${this::class.java.name}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        //TODO this will change, we needed for now in order to unblock this view work
        Injector.INSTANCE.initViewComponent(activity as AppCompatActivity).inject(this)
    }

    override fun toolbarVisibility(isVisible: Boolean) {
        if (isVisible) {
            (activity as PresenterOwnerActivity<*>).supportActionBar?.show()
        } else {
            (activity as PresenterOwnerActivity<*>).supportActionBar?.hide()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_connection, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickViews()
    }

    override fun onDestroy() {
        clickDisposables.clear()
        super.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PREPARE_VPN_SERVICE) {
            when (resultCode) {
                Activity.RESULT_OK -> presenter.onPermissionsGranted()
                Activity.RESULT_CANCELED -> presenter.onPermissionsDenied()
                else -> {
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun showVpnPermissionsDialog() {

        val context = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.M -> super.getContext()
            else -> activity
        }

        val intent = VpnService.prepare(context)

        startActivityForResult(intent, PREPARE_VPN_SERVICE)
    }

    override fun setDisconnectedLocation(countryName: String, cityName: String) {
        locationTextView.text = getString(
            R.string.home_connection_location_placeholder,
            cityName,
            countryName
        )
    }

    override fun setDisconnectedToFastest() {
        locationTextView.text = getString(R.string.home_connection_location_best_available)
    }

    override fun setDisconnectedLocationToFastest(countryName: String) {
        locationTextView.text = getString(R.string.home_connection_location_best_country_placeholder, countryName)
    }

    override fun showDisconnectedView() {
        connectionStateLayout.visibility = View.GONE
        disconnectedStateLayout.visibility = View.VISIBLE
    }

    override fun showConnectingView() {
        if (viewConnecting.state != ConnectionStateView.ConnectionAnimationState.CONNECTING) {
            viewConnecting.setConnectionState(
                ConnectionStateView.ConnectionAnimationState.CONNECTING
            )
        }
        groupConnected.visibility = View.INVISIBLE
        groupConnected.updatePreLayout(connectionStateLayout as ConstraintLayout)
        connectionStateLayout.visibility = View.VISIBLE
        disconnectedStateLayout.visibility = View.GONE
    }

    override fun showConnectedServer(hostIpAddress: String, countryName: String) {
        val locationText = getString(
            R.string.home_connection_location_placeholder,
            "", countryName
        )
        ipTextView.text = hostIpAddress
        connectedLocationTextView.text = locationText
    }

    override fun showConnectedServer(hostIpAddress: String, countryName: String, cityName: String) {
        val locationText = getString(
            R.string.home_connection_location_placeholder,
            cityName, countryName
        )
        ipTextView.text = hostIpAddress
        connectedLocationTextView.text = locationText
    }

    override fun showConnectedView() {
        if (viewConnecting.state != ConnectionStateView.ConnectionAnimationState.CONNECTED) {
            viewConnecting.setConnectionState(
                ConnectionStateView.ConnectionAnimationState.CONNECTED
            )
        }
        groupConnected.visibility = View.VISIBLE
        groupConnected.updatePreLayout(connectionStateLayout as ConstraintLayout)
        connectionStateLayout.visibility = View.VISIBLE
        disconnectedStateLayout.visibility = View.GONE
    }

    override fun showServersView() {
        featureNavigator.navigateToServersView()
    }

    override fun showErrorMessage(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun showLogin() {
        featureNavigator.navigateToLogin()
        activity?.finish()
    }

    override fun showNoNetworkMessage() {
        showErrorMessage(getString(R.string.error_no_connection))
    }

    override fun showUnknownErrorMessage() {
        showErrorMessage(getString(R.string.error_unknown_error))
    }

    override fun showConnectionErrorMessage() {
        showErrorMessage(getString(R.string.error_vpn_connection))
    }

    private fun setupClickViews() {
        clickDisposables.add(buttonConnect.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                presenter.onConnectClick()
            })

        clickDisposables.add(buttonDisconnect.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                presenter.onDisconnectClick()
            })

        clickDisposables.add(locationTextView.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                presenter.onLocationClick()
            })
    }

}