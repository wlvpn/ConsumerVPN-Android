package com.wlvpn.slider.whitelabelvpn.layouts;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnState;
import com.jakewharton.rxbinding.view.RxView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.activities.BaseActivity;
import com.wlvpn.slider.whitelabelvpn.activities.MainActivity;
import com.wlvpn.slider.whitelabelvpn.activities.PopListActivity;
import com.wlvpn.slider.whitelabelvpn.adapters.EncryptionPagerAdapter;
import com.wlvpn.slider.whitelabelvpn.holders.EncryptionPagerHolder;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.models.Connectable;
import com.wlvpn.slider.whitelabelvpn.settings.CipherPref;
import com.wlvpn.slider.whitelabelvpn.state.ConnectButtonState;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import kotlin.Unit;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

import static com.wlvpn.slider.whitelabelvpn.activities.BaseActivity.CLICK_DELAY;

public class SliderLayout extends ConstraintLayout {

    @Singleton
    @Inject
    public List<EncryptionPagerHolder> pagerItems;

    @Inject
    public EncryptionPagerAdapter pagerAdapter;

    @Inject
    public SettingsManager settingsManager;

    @Inject
    public ConnectableManager connectableManager;


    private ICallback<VpnState> callbackState;
    private final ConnectButtonState connectButtonState;

    public Subscription subscription;


    private Button locations;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    public SliderLayout(Context context) {
        super(context);

        ConsumerVpnApplication.component().inject(this);

        inflate(getContext(), R.layout.layout_slider, this);

        locations = findViewById(R.id.fragment_slider_button_location);
        viewPager = findViewById(R.id.fragment_slider_pager);
        tabLayout = findViewById(R.id.fragment_slider_tablayout);

        connectButtonState = new ConnectButtonState(ConnectButtonState.CONNECT_ENABLED);

        callbackState = registerVpnStateListener();
        initListeners();

        viewPager.setAdapter(pagerAdapter);
        setupTabLayout(pagerItems);

        setupEncryptionPager();
        registerConnectableUpdates();
        connectableManager.notifyUpdatedConnectable();
    }

    private void initListeners() {
        ((BaseActivity) getContext()).getMainSubscription().add(RxView.clicks(locations)
                .throttleFirst(CLICK_DELAY, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aVoid -> startServerListActivity()));
    }

    private ICallback<VpnState> registerVpnStateListener() {
        return ConsumerVpnApplication.getVpnSdk().listenToConnectState()
                .onNext(vpnState -> {
                    int connectionState = vpnState.getConnectionState();
                    switch (connectionState) {
                        case VpnState.DISCONNECTED:
                            setConnectState(connectableManager.getConnectable());
                            break;
                        case VpnState.CONNECTED:
                            setConnectButtonState(ConnectButtonState.CONNECTED);
                            break;
                    }
                    return Unit.INSTANCE;
                })
                .onError(throwable -> {
                    Timber.e(throwable, "Failed to listen to vpn state");
                    return Unit.INSTANCE;
                })
                .subscribe();
    }

    public void selectTab(int position) {
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }

    public void startServerListActivity() {
        Intent intent = new Intent(getContext(), PopListActivity.class);
        getContext().startActivity(intent);
    }

    public void setupTabLayout(List<EncryptionPagerHolder> pagerHolders) {
        for (EncryptionPagerHolder page : pagerHolders) {
            View tabView = LayoutInflater.from(getContext()).inflate(R.layout.view_encryption_tab, tabLayout, false);
            TextView title = tabView.findViewById(R.id.encryption_tab_title);
            TextView subtitle = tabView.findViewById(R.id.encryption_tab_subtitle);

            title.setText(page.getTitleResource());
            subtitle.setText(page.getSubtitleResource());
            tabLayout.addTab(tabLayout.newTab().setCustomView(tabView));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        if (subscription != null) {
            subscription.unsubscribe();
            subscription = null;
        }

        callbackState.unsubscribe();
    }

    /**
     * Initializes the encryption tabs and pages.
     * Due to initialization firing a Tab select event, you must
     * select a page first from settings.
     */
    private void setupEncryptionPager() {
        // Translate current cipher selected for the current flavor
        // To select correct tab and view on pager
        int position = CipherPref.getCipherToPosition(
                settingsManager.getCipherPref().getCipher());

        viewPager.setCurrentItem(position);

        selectTab(position);

        viewPager.addOnPageChangeListener(getPageChangeListener());
        tabLayout.addOnTabSelectedListener(getTabSelectListener());
    }

    private void setConnectButtonState(@ConnectButtonState.ConnectState int state) {
        connectButtonState.setConnectState(state);

        if (getContext() == null) {
            return;
        }

        MainActivity mainActivity = (MainActivity) getContext();

        switch (state) {
            case ConnectButtonState.CONNECT_ENABLED:
                mainActivity.setConnectButtonColor(R.drawable.button_connect);
                mainActivity.setConnectButtonText(R.string.connect);
                mainActivity.enableConnectButton();
                break;
            case ConnectButtonState.CONNECT_DISABLED:
                mainActivity.setConnectButtonColor(R.drawable.button_cancel);
                mainActivity.setConnectButtonText(R.string.connect);
                mainActivity.disableConnectButton();
                break;
            case ConnectButtonState.CONNECTED:
                mainActivity.setConnectButtonColor(R.drawable.button_disconnect);
                mainActivity.setConnectButtonText(R.string.connect);
                mainActivity.disableConnectButton();
                break;
            case ConnectButtonState.DISCONNECTING:
                mainActivity.setConnectButtonColor(R.drawable.button_cancel);
                mainActivity.setConnectButtonText(R.string.disconnecting);
                mainActivity.disableConnectButton();
                break;
            default:
                mainActivity.setConnectButtonColor(R.drawable.button_connect);
                mainActivity.setConnectButtonText(R.string.connect);
                mainActivity.enableConnectButton();
                break;
        }

    }

    /**
     * Checks current connection status and
     * updates the connect button
     */
    private void setConnectState(@Nullable Connectable connectable) {
        if (connectable != null) {
            locations.setText(connectable.getFormattedLocation());
        } else {
            String location = getContext().getApplicationContext()
                    .getString(R.string.best_available_location);
            locations.setText(location);
        }
        setConnectButtonState(ConnectButtonState.CONNECT_ENABLED);
    }

    /**
     * Listen for the connect state to change and
     * update connect button state and server button location
     */
    private void registerConnectableUpdates() {

        subscription = connectableManager.getConnectableObservableSubject()
                .subscribe(connectable -> setConnectState(connectable),
                        throwable -> Timber.e(throwable, "Failed to set connection state"));
    }

    /**
     * Listener for when the View Pager is swiped
     *
     * @return ViewPager.SimpleOnPageChangeListener
     */
    private ViewPager.SimpleOnPageChangeListener getPageChangeListener() {
        return new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectTab(position);
            }
        };
    }

    /**
     * Get the Tab Select listener
     * Check build.gradle to determine current options for the tabs
     *
     * @return TabLayout.OnTabSelectedListener
     */
    private TabLayout.OnTabSelectedListener getTabSelectListener() {
        return new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                // We need to translate current position in
                // case we do not have all tabs available
                @CipherPref.Cipher int cipher = CipherPref
                        .getPositionToCipher(tab.getPosition());

                settingsManager.updateCipher(new CipherPref(cipher));

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        };
    }
}
