package com.wlvpn.slider.whitelabelvpn.managers;


import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.gentlebreeze.vpn.sdk.model.VpnServer;
import com.wlvpn.slider.whitelabelvpn.models.Connectable;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.PublishSubject;

@Singleton
public class ConnectableManager {

    private static final String SHARED_PREFERENCES_NAMESPACE = "Connectable-Area";
    private static final String SETTINGS_HOST = "settings:host";
    private static final String SETTINGS_COUNTRY_CODE = "settings:countryCode";
    private static final String SETTINGS_COUNTRY = "settings:country";
    private static final String SETTINGS_CITY = "settings:city";
    private static final String SETTINGS_IP = "settings:ip";

    private final PublishSubject<Connectable> connectablePublishSubject;
    private final SharedPreferences sharedPreferences;

    @Inject
    public ConnectableManager(Context context) {
        sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAMESPACE, Context.MODE_PRIVATE);
        connectablePublishSubject = PublishSubject.create();
    }


    /**
     * Get Selected Host
     *
     * @return String
     */
    public String getSelectedHost() {
        return sharedPreferences.getString(
                SETTINGS_HOST,
                null
        );
    }

    /**
     * Get Selected Country Code
     *
     * @return String
     */
    private String getSelectedCountryCode() {
        return sharedPreferences.getString(
                SETTINGS_COUNTRY_CODE,
                null
        );
    }

    /**
     * Get Selected Country
     *
     * @return String
     */
    private String getSelectedCountry() {
        return sharedPreferences.getString(
                SETTINGS_COUNTRY,
                null
        );
    }

    /**
     * Get Selected City
     *
     * @return String
     */
    private String getSelectedCity() {
        return sharedPreferences.getString(
                SETTINGS_CITY,
                null
        );
    }

    /**
     * Get selected ip
     *
     * @return string
     */
    private String getSelectedIp() {
        return sharedPreferences.getString(
                SETTINGS_IP,
                null
        );
    }

    /**
     * Updates the host
     *
     * @param host connection host string
     */
    private void updateHost(String host) {
        sharedPreferences.edit()
                .putString(SETTINGS_HOST, host)
                .apply();
    }

    /**
     * Updates country code
     *
     * @param countryCode location country code
     */
    private void updateCountryCode(String countryCode) {
        sharedPreferences.edit()
                .putString(SETTINGS_COUNTRY_CODE, countryCode)
                .apply();
    }

    /**
     * Updates country
     *
     * @param country location string
     */
    private void updateCountry(String country) {
        sharedPreferences.edit()
                .putString(SETTINGS_COUNTRY, country)
                .apply();
    }

    /**
     * Updates city
     *
     * @param city location string
     */
    private void updateCity(String city) {
        sharedPreferences.edit()
                .putString(SETTINGS_CITY, city)
                .apply();
    }

    /**
     * Updates IP
     *
     * @param ip ip string
     */
    private void updateIp(String ip) {
        sharedPreferences.edit()
                .putString(SETTINGS_IP, ip)
                .apply();
    }

    public void setConnectable(@NonNull Connectable connectable) {
        updateCountryCode(connectable.getCountryCode());
        updateCountry(connectable.getCountry());
        updateCity(connectable.getCity());
        updateHost(connectable.getHostname());
        updateIp(connectable.getIpAddress());
        notifyUpdatedConnectable();
    }

    public Connectable getConnectable() {
        try {
            return Connectable.builder()
                    .city(getSelectedCity())
                    .country(getSelectedCountry())
                    .countryCode(getSelectedCountryCode())
                    .hostname(getSelectedHost())
                    .ipAddress(getSelectedIp())
                    .build();
        } catch (IllegalStateException e) {
            return null;
        }
    }

    public void notifyUpdatedConnectable() {
        connectablePublishSubject.onNext(getConnectable());
    }

    public Observable<Connectable> getConnectableObservableSubject() {
        return connectablePublishSubject;
    }

    public void clear() {
        sharedPreferences.edit()
                .clear()
                .apply();
    }
}
