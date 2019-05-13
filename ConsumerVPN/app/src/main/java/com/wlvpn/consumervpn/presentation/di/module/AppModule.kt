package com.wlvpn.consumervpn.presentation.di.module

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import com.gentlebreeze.http.connectivity.ConnectivityNetworkStateProvider
import com.gentlebreeze.http.connectivity.INetworkStateProvider
import com.gentlebreeze.vpn.sdk.IVpnSdk
import com.gentlebreeze.vpn.sdk.VpnSdk
import com.gentlebreeze.vpn.sdk.config.SdkConfig
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.VpnNotificationStatusController
import com.wlvpn.consumervpn.presentation.bus.Event
import com.wlvpn.consumervpn.presentation.bus.SinglePipelineBus
import com.wlvpn.consumervpn.presentation.di.scope.PerApplication
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationChannel
import com.wlvpn.consumervpn.presentation.notification.vpn.VpnNotificationFactory
import com.wlvpn.consumervpn.presentation.util.*
import dagger.Module
import dagger.Provides
import javax.inject.Named

const val IS_DEVICE_TV_PROPERTY = "IS_DEVICE_TV_PROPERTY"
const val CONNECT_BUS_PROPERTY = "CONNECT_BUS_PROPERTY"
const val COMMON_SHARED_PREFERENCES_NAMESPACE = "COMMON_SHARED_PREFERENCES"


/**
 * A module that holds with dependencies that needs a application context.
 *
 * Use @PerApplication if your dependency needs to survive in application life cycle (Like a "singleton").
 * Otherwise, it will be treated as a global dependency visible in sub components and not retained. (Each inject will
 * create a new instance)
 */
@Module
class AppModule(private val application: Application) {

    @Provides
    @PerApplication
    fun providesApplication(): Application = application

    @Provides
    @PerApplication
    fun providesVPNSdk(): IVpnSdk {
        val vpnSdkConfig = SdkConfig.Builder(
            application.getString(R.string.account_name),
            application.getString(R.string.api_key),
            application.getString(R.string.auth_suffix))
            .client(BuildConfig.CLIENT)
            .apiHost(application.getString(R.string.endpoint_main_api))
            .ipGeoUrl(application.getString(R.string.ip_geo_url))
            .apiLoginEndpoint(application.getString(R.string.login_api))
            .apiTokenRefreshEndpoint(application.getString(R.string.token_refresh_api))
            .apiProtocolListEndpoint(application.getString(R.string.protocol_list_api))
            .apiServerListEndpoint(application.getString(R.string.server_list_api))
            .build()

        return VpnSdk.init(application, vpnSdkConfig)
    }

    @Provides
    @PerApplication
    @Named(IS_DEVICE_TV_PROPERTY)
    fun providesIsDeviceTv() = isTv(application)

    @Provides
    fun providesSchedulerProvider(): SchedulerProvider = DefaultSchedulerProvider()

    @Provides
    @PerApplication
    internal fun provideNetworkStateProvider(context: Application): INetworkStateProvider {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return ConnectivityNetworkStateProvider(cm)
    }

    @Provides
    @PerApplication
    @Named(CONNECT_BUS_PROPERTY)
    fun providesConnectEvenBus(): SinglePipelineBus<Event.ConnectionRequestEvent> = SinglePipelineBus()

    @Provides
    @Named(COMMON_SHARED_PREFERENCES_NAMESPACE)
    fun providesCommonSharedPreferences(application: Application): SharedPreferences {
        return application.getSharedPreferences(
            application.packageName + COMMON_SHARED_PREFERENCES_NAMESPACE,
            Context.MODE_PRIVATE
        )
    }

    @Provides
    fun providesStartupStatus(
        application: Application,
        @Named(COMMON_SHARED_PREFERENCES_NAMESPACE) sharedPreferences: SharedPreferences
    ): StartupStatus =
        SharedPreferenceStartupStatus(application.packageName, sharedPreferences)

    @Provides
    fun providesNotificationManager(): NotificationManager =
        application.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @Provides
    fun providesVpnNotificationChannel(notificationManager: NotificationManager): VpnNotificationChannel =
        VpnNotificationChannel(
            application.getString(R.string.notification_vpn_connection_channel_title),
            notificationManager
        )

    @Provides
    fun providesVpnNotificationFactory(vpnNotificationChannel: VpnNotificationChannel): VpnNotificationFactory =
        VpnNotificationFactory(application, vpnNotificationChannel)

    @Provides
    @PerApplication
    fun providesVpnNotificationStatusController(
        vpnService: VpnService,
        schedulerProvider: SchedulerProvider,
        notificationManager: NotificationManager,
        vpnNotificationChannel: VpnNotificationChannel,
        vpnNotificationFactory: VpnNotificationFactory
    ) =
        VpnNotificationStatusController(
            application,
            vpnService,
            schedulerProvider,
            notificationManager,
            vpnNotificationChannel,
            vpnNotificationFactory
        )
}