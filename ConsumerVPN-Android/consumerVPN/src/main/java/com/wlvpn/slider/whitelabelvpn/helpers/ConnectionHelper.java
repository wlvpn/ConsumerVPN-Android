package com.wlvpn.slider.whitelabelvpn.helpers;


import com.gentlebreeze.vpn.sdk.model.VpnConnectionConfiguration;
import com.gentlebreeze.vpn.sdk.model.VpnPop;
import com.gentlebreeze.vpn.sdk.model.VpnPortOptions;
import com.gentlebreeze.vpn.sdk.model.VpnProtocolOptions;
import com.gentlebreeze.vpn.sdk.model.VpnServer;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.auth.Credentials;
import com.wlvpn.slider.whitelabelvpn.auth.CredentialsManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.managers.VpnNotificationManager;
import com.wlvpn.slider.whitelabelvpn.settings.PortPref;
import com.wlvpn.slider.whitelabelvpn.settings.ProtocolPref;

import javax.inject.Inject;

import kotlin.Unit;
import timber.log.Timber;

public class ConnectionHelper {

    private final VpnNotificationManager vpnNotificationManager;
    private final SettingsManager settingsManager;
    private final CredentialsManager credentialsManager;

    @Inject
    public ConnectionHelper(VpnNotificationManager vpnNotificationManager,
                            CredentialsManager credentialsManager,
                            SettingsManager settingsManager) {
        this.vpnNotificationManager = vpnNotificationManager;
        this.credentialsManager = credentialsManager;
        this.settingsManager = settingsManager;
    }

    public void connectByCountryCode(final String countryCode) {
        ConsumerVpnApplication.getVpnSdk()
                .connect(
                        countryCode,
                        vpnNotificationManager.getVpnNotificationConfiguration(),
                        vpnNotificationManager.getVpnRevokeNotification(),
                        getVpnConnectionConfiguration()
                )
                .subscribe(aBoolean -> {
                    Timber.d("Connected by country selection");
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to connect");
                    return Unit.INSTANCE;
                });
    }

    /**
     * Let SDK handle the connection using the geolocation
     */
    public void connectByGeo() {
        ConsumerVpnApplication.getVpnSdk().connect(
                vpnNotificationManager.getVpnNotificationConfiguration(),
                vpnNotificationManager.getVpnRevokeNotification(),
                getVpnConnectionConfiguration())
                .subscribe(null, throwable -> {
                    Timber.e(throwable, "Failed to connect by geo");
                    return Unit.INSTANCE;
                });
    }

    /**
     * Connects to vpn through a vpn pop object
     * to connect to vpn pop
     *
     * @param vpnPop vpnPop to connect to
     */
    public void connectByVpnPop(VpnPop vpnPop) {
        ConsumerVpnApplication.getVpnSdk().connect(
                vpnPop,
                vpnNotificationManager.getVpnNotificationConfiguration(),
                vpnNotificationManager.getVpnRevokeNotification(),
                getVpnConnectionConfiguration()
        ).subscribe(null, throwable -> {
            Timber.e(throwable, "Failed to connect by pop");
            return Unit.INSTANCE;
        });
    }

    /**
     * Connect to vpn through a vpn server object
     *
     * @param vpnServer vpnServer to connect to
     */
    public void connectByVpnServer(VpnServer vpnServer) {
        ConsumerVpnApplication.getVpnSdk().connect(
                vpnServer,
                vpnNotificationManager.getVpnNotificationConfiguration(),
                getVpnConnectionConfiguration(),
                vpnNotificationManager.getVpnRevokeNotification()
        ).subscribe(null, throwable -> {
            Timber.e(throwable, "Failed to connect by server");
            return Unit.INSTANCE;
        });
    }


    /**
     * Get the vpn connection configuration
     *
     * @return VpnConnectionConfiguration
     */
    private VpnConnectionConfiguration getVpnConnectionConfiguration() {
        Credentials credentials = credentialsManager.getCredentials();
        return new VpnConnectionConfiguration(
                credentials.getUsername(),
                credentials.getPassword(),
                false, //Todo: Implement Scramble setting when scramble is enabled
                settingsManager.getAutoReconnectPref(),
                getPortPreference(),
                getProtocolPreference()
        );
    }

    /**
     * Convert internal port preference to Vpn preference
     *
     * @return VpnPortOptions
     */
    private VpnPortOptions getPortPreference() {
        switch (settingsManager.getPortPref().getPortPref()) {
            case PortPref.PORT_443:
                return VpnPortOptions.PORT_443;
            case PortPref.PORT_1194:
                return VpnPortOptions.PORT_1194;
            default:
                return VpnPortOptions.PORT_443;
        }
    }

    /**
     * Convrty internal protocol preference to Vpn Protocol preference
     *
     * @return VpnProtocolOptions
     */
    private VpnProtocolOptions getProtocolPreference() {
        switch (settingsManager.getProtocolPref().getProtocolPref()) {
            case ProtocolPref.UDP:
                return VpnProtocolOptions.PROTOCOL_UDP;
            case ProtocolPref.TCP:
                return VpnProtocolOptions.PROTOCOL_TCP;
            default:
                return VpnProtocolOptions.PROTOCOL_UDP;
        }
    }
}
