package com.wlvpn.slider.whitelabelvpn.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.gentlebreeze.vpn.sdk.model.VpnPop;
import com.jakewharton.rxbinding.support.v7.widget.RxSearchView;
import com.wlvpn.slider.whitelabelvpn.ConsumerVpnApplication;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.adapters.CountryListRecyclerAdapter;
import com.wlvpn.slider.whitelabelvpn.adapters.DividerItemDecoration;
import com.wlvpn.slider.whitelabelvpn.comparators.CountryComparator;
import com.wlvpn.slider.whitelabelvpn.listener.CountryClickListener;
import com.wlvpn.slider.whitelabelvpn.models.Country;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import kotlin.Unit;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class CountryListActivity
        extends BaseActivity
        implements CountryClickListener {

    public static final int SELECT_COUNTRY = 100;
    public static final String COUNTRY_SELECTION = "country_selection";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CountryListRecyclerAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ConsumerVpnApplication.component().inject(this);

        setContentView(R.layout.activity_country_list);

        recyclerView = findViewById(R.id.activity_country_list_recycler);
        progressBar = findViewById(R.id.activity_country_list_progress_bar);

        adapter = new CountryListRecyclerAdapter(this);
        setupRecycler(adapter);
        fetchCountries("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.country_list_menu, menu);
        setupSearchView(menu);
        return true;
    }

    private void setupRecycler(RecyclerView.Adapter adapter) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration divider =
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST);
        Drawable drawable = ContextCompat.getDrawable(getBaseContext(), R.drawable.row_divider);
        if (drawable != null) {
            divider.setDrawable(drawable);
            recyclerView.addItemDecoration(divider);
        }
        recyclerView.setAdapter(adapter);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    public void dismissActivityWithResult(Intent intent) {
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onCountryClick(Country country) {
        Intent intent = new Intent();
        intent.putExtra(COUNTRY_SELECTION, country);
        dismissActivityWithResult(intent);
    }

    private void setupSearchView(Menu menu) {
        final MenuItem item = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);

        getMainSubscription().add(RxSearchView.queryTextChanges(searchView)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(charSequence -> fetchCountries(charSequence.toString()),
                        throwable -> Timber.e(throwable.getMessage())));
    }

    private void fetchCountries(@NonNull final String query) {
        showProgressBar();
        ConsumerVpnApplication.getVpnSdk().fetchAllPops()
                .subscribe(vpnPops -> {
                    List<Country> countries
                            = filterCountriesByQuery(convertVpnPopToCountry(vpnPops), query);
                    setItems(countries);
                    hideProgressBar();
                    return Unit.INSTANCE;
                }, throwable -> {
                    Timber.e(throwable, "Failed to get Pop");
                    return Unit.INSTANCE;
                });
    }

    private void setItems(List<Country> countryList) {
        Collections.sort(countryList, new CountryComparator());
        adapter.setItems(countryList);
        adapter.notifyDataSetChanged();
    }

    /**
     * Filters the countries by search query
     *
     * @param countries list of countries
     * @param query     search query
     * @return List<Country> of filtered countries
     */
    private List<Country> filterCountriesByQuery(List<Country> countries, String query) {
        if (query.isEmpty()) {
            return countries;
        } else {
            List<Country> filteredCountries = new LinkedList<>();
            for (Country country : countries) {
                String display = country.getDisplayCountry().toLowerCase(Locale.US);
                if (display.contains(query.toLowerCase(Locale.US))) {
                    filteredCountries.add(country);
                }
            }
            return filteredCountries;
        }
    }

    /**
     * Converts VpnPops into Country
     *
     * @param vpnPops vpnPops
     * @return List<Country>
     */
    private List<Country> convertVpnPopToCountry(List<VpnPop> vpnPops) {
        Set<Country> countries = new HashSet<>();
        for (VpnPop vpnPop : vpnPops) {
            countries.add(new Country(vpnPop.getCountryCode()));
        }
        return new LinkedList<>(countries);
    }
}
