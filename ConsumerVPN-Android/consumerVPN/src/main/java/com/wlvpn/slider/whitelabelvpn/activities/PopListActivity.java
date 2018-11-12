package com.wlvpn.slider.whitelabelvpn.activities;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.gentlebreeze.vpn.sdk.callback.ICallback;
import com.gentlebreeze.vpn.sdk.model.VpnPop;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.adapters.DividerItemDecoration;
import com.wlvpn.slider.whitelabelvpn.adapters.PopListRecyclerAdapter;
import com.wlvpn.slider.whitelabelvpn.comparators.PopCityComparator;
import com.wlvpn.slider.whitelabelvpn.comparators.PopCountryComparator;
import com.wlvpn.slider.whitelabelvpn.helpers.LocaleHelper;
import com.wlvpn.slider.whitelabelvpn.listener.PopClickListener;
import com.wlvpn.slider.whitelabelvpn.managers.ConnectableManager;
import com.wlvpn.slider.whitelabelvpn.managers.SettingsManager;
import com.wlvpn.slider.whitelabelvpn.models.Connectable;
import com.wlvpn.slider.whitelabelvpn.settings.SortPref;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import kotlin.Unit;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

/**
 * @see BaseActivity
 */
public class PopListActivity
        extends BaseActivity
        implements PopClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PopListRecyclerAdapter adapter;
    private ICallback<List<VpnPop>> vpnPopCallback;

    @Inject
    public SettingsManager settingsManager;

    @Inject
    public ConnectableManager connectableManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_pop_list);

        recyclerView = findViewById(R.id.activity_pop_list_recycler);
        progressBar = findViewById(R.id.activity_pop_list_progress_bar);

        adapter = new PopListRecyclerAdapter(this, getSortComparator());
        setupRecycler(adapter);
        requestPops("");
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            vpnPopCallback.unsubscribe();
            getMainSubscription().unsubscribe();
        } catch (Exception e) {
            Timber.e(e, "Failed to unsubscribe");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_menu_sort_country:
                setSort(new SortPref(SortPref.SORT_COUNTRY));
                break;
            case R.id.action_menu_sort_city:
                setSort(new SortPref(SortPref.SORT_CITY));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.server_list_options, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupRecycler(RecyclerView.Adapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        DividerItemDecoration divider =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        Drawable drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.row_divider);
        if (drawable != null) {
            divider.setDrawable(drawable);
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupSearchView(Menu menu) {
        final MenuItem item = menu.findItem(R.id.server_list_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        getMainSubscription().add(RxSearchView.queryTextChanges(searchView)
                .filter(charSequence -> !TextUtils.isEmpty(charSequence))
                .throttleLast(100, TimeUnit.MILLISECONDS)
                .debounce(200, TimeUnit.MILLISECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> requestPops(charSequence.toString()),
                        throwable -> Timber.e(throwable.getMessage())));
    }

    private void requestPops(@NonNull final String query) {
        showProgressBar();
        vpnPopCallback = ConsumerVpnApplication.getVpnSdk().fetchAllPops()
                .onNext(vpnPops -> {
                    List<VpnPop> filteredList = filterPopsBySearch(query, vpnPops);
                    if (adapter.getItemCount() == 0) {
                        adapter.setVpnPops(filteredList);
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.animateTo(filteredList);
                    }
                    hideProgressBar();
                    return Unit.INSTANCE;
                })
                .onError(throwable -> {
                    Timber.e(throwable, "Failed to fetch pops");
                    return Unit.INSTANCE;
                })
                .subscribe();
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onPopItemClick(VpnPop vpnPop) {
        if (vpnPop != null) {
            setVpnPopConnectable(vpnPop);
        } else { // On Best Available we clear preferences
            connectableManager.clear();
        }

        finish();
    }

    /**
     * Set the connectable by VpnPop
     *
     * @param vpnPop vpnPop to connect to
     */
    private void setVpnPopConnectable(VpnPop vpnPop) {
        connectableManager.setConnectable(Connectable.builder()
                .city(vpnPop.getCity())
                .countryCode(vpnPop.getCountryCode())
                .country(vpnPop.getCountry())
                .hostname("")
                .ipAddress("")
                .build());
    }

    /**
     * Filters pop by search
     *
     * @param query   search query
     * @param vpnPops list of vpn pops
     * @return List<VpnPop> filtered search results
     */
    private List<VpnPop> filterPopsBySearch(String query, List<VpnPop> vpnPops) {
        List<VpnPop> filteredList = new LinkedList<>();
        for (VpnPop vpnPop : vpnPops) {
            String cleanQuery = query.trim().toLowerCase(Locale.US);
            String city = vpnPop.getCity().toLowerCase(Locale.US);
            String country = vpnPop.getCountry().toLowerCase(Locale.US);
            String hostname = vpnPop.getName().toLowerCase(Locale.US);
            String countryName = LocaleHelper.getCountryByCode(vpnPop.getCountry())
                    .toLowerCase(Locale.US);

            if (city.contains(cleanQuery)
                    || country.contains(cleanQuery)
                    || countryName.contains(cleanQuery)
                    || hostname.contains(cleanQuery)) {

                filteredList.add(vpnPop);
            }
        }
        return filteredList;
    }

    private void setSort(@NonNull SortPref sortPref) {
        settingsManager.updateSortPref(sortPref);
        adapter.setComparator(getSortComparator());
        adapter.notifyDataSetChanged();
    }

    private Comparator<VpnPop> getSortComparator() {
        final int sortMode = settingsManager.getSortPref()
                .getSortMode();

        switch (sortMode) {
            case SortPref.SORT_CITY:
                return new PopCityComparator();
            case SortPref.SORT_COUNTRY:
                return new PopCountryComparator();
            default:
                throw new IllegalArgumentException("Unknown sort mode " + sortMode);
        }
    }
}
