package com.wlvpn.slider.whitelabelvpn.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wlvpn.flags.FlagResource;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.listener.CountryClickListener;
import com.wlvpn.slider.whitelabelvpn.models.Country;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CountryListRecyclerAdapter extends RecyclerView.Adapter<CountryListRecyclerAdapter.ViewHolder> {

    private final List<Country> countryList;
    private final CountryClickListener clickListener;

    public CountryListRecyclerAdapter(CountryClickListener clickListener) {
        this.clickListener = clickListener;
        this.countryList = new ArrayList<>();
    }

    @Override
    public CountryListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View countryListView = inflater.inflate(R.layout.location_list_item, parent, false);
        return new CountryListRecyclerAdapter.ViewHolder(countryListView, countryList, clickListener);
    }

    @Override
    public void onBindViewHolder(CountryListRecyclerAdapter.ViewHolder holder, int position) {
        final Country country = countryList.get(position);
        holder.bind(country);
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public void setItems(List<Country> countryList) {
        this.countryList.clear();
        this.countryList.addAll(countryList);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView countryFlag;
        private TextView countryFlagLabel;

        private final List<Country> countryList;
        private final CountryClickListener clickListener;
        private final FlagResource flagResource;

        public ViewHolder(View itemView, List<Country> countryList, CountryClickListener clickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.countryList = countryList;
            this.clickListener = clickListener;
            this.flagResource = new FlagResource(itemView.getContext());

            countryFlag = itemView.findViewById(R.id.location_flag_icon);
            countryFlagLabel = itemView.findViewById(R.id.location);
        }

        public void bind(Country country) {
            String countryFormatted = country.getCountryCode().toLowerCase(Locale.US);
            int flagDrawableId = flagResource.forCountry(countryFormatted);
            countryFlagLabel.setText(country.getDisplayCountry());
            countryFlag.setImageResource(flagDrawableId);
        }

        @Override
        public void onClick(View v) {
            int position = getLayoutPosition();
            Country country = countryList.get(position);
            clickListener.onCountryClick(country);
        }
    }
}
