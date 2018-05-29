package com.wlvpn.slider.whitelabelvpn;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.gentlebreeze.vpn.sdk.IVpnSdk;
import com.gentlebreeze.vpn.sdk.VpnSdk;
import com.gentlebreeze.vpn.sdk.config.SdkConfig;
import com.wlvpn.slider.whitelabelvpn.di.AppComponent;
import com.wlvpn.slider.whitelabelvpn.di.AppModule;
import com.wlvpn.slider.whitelabelvpn.di.DaggerAppComponent;

import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

@SuppressWarnings("all")
public class ConsumerVpnApplication extends Application {

    private static IVpnSdk vpnSdk;

    public static final String DB_NAME = "wlvpn.db";
    public static final int DB_VERSION = 1;

    @SuppressWarnings("all")//don't tell me what to do
    private static final AppModule APP_MODULE = new AppModule();

    private static final AppComponent COMPONENT = DaggerAppComponent.builder()
            .appModule(APP_MODULE)
            .build();

    public static AppComponent component() {
        return COMPONENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());

            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
        }
        AppModule appModule = new AppModule();
        appModule.setApplication(this);

        APP_MODULE.setApplication(this);

        COMPONENT.inject(this);

        vpnSdk = VpnSdk.Companion.init(this, new SdkConfig(
                BuildConfig.ACCOUNT_NAME,
                BuildConfig.API_KEY,
                BuildConfig.AUTH_SUFFIX,
                BuildConfig.CLIENT,
                BuildConfig.ENDPOINT,
                BuildConfig.IP_GEO,
                BuildConfig.LOGIN_API,
                BuildConfig.REFRESH_API,
                BuildConfig.PROTOCOL_LIST_API,
                BuildConfig.SERVER_LIST_API
        ));

        Timber.plant(new Timber.DebugTree());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
        CalligraphyContextWrapper.wrap(base);
    }

    public static IVpnSdk getVpnSdk() {
        return vpnSdk;
    }
}