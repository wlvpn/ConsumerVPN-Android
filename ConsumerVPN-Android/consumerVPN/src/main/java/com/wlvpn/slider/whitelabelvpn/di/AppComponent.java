package com.wlvpn.slider.whitelabelvpn.di;

import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.activities.CountryListActivity;
import com.wlvpn.slider.whitelabelvpn.activities.LoginActivity;
import com.wlvpn.slider.whitelabelvpn.activities.MainActivity;
import com.wlvpn.slider.whitelabelvpn.activities.PopListActivity;
import com.wlvpn.slider.whitelabelvpn.activities.SettingsActivity;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectedLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.ConnectingLayout;
import com.wlvpn.slider.whitelabelvpn.layouts.SliderLayout;
import com.wlvpn.slider.whitelabelvpn.receivers.SwitchServerReceiver;
import com.wlvpn.slider.whitelabelvpn.receivers.VpnConnectionReceiver;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {

    void inject(ConsumerVpnApplication target);

    void inject(SwitchServerReceiver target);

    void inject(VpnConnectionReceiver target);

    void inject(LoginActivity target);

    void inject(SettingsActivity target);

    void inject(MainActivity target);

    void inject(SliderLayout target);

    void inject(PopListActivity target);

    void inject(CountryListActivity target);

    void inject(ConnectedLayout target);

    void inject(ConnectingLayout target);
}