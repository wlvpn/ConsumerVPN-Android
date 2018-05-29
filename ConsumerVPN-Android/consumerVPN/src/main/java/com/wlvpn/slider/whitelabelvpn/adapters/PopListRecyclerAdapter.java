package com.wlvpn.slider.whitelabelvpn.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gentlebreeze.vpn.sdk.model.VpnPop;
import com.wlvpn.flags.FlagResource;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.listener.PopClickListener;
import com.wlvpn.slider.whitelabelvpn.utilities.SortedArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import static com.wlvpn.slider.whitelabelvpn.utilities.PopHelperKt.getFormattedLocation;

@SuppressWarnings("WeakerAccess")
public class PopListRecyclerAdapter extends RecyclerView.Adapter<PopListRecyclerAdapter.ViewHolder> {

    private static final int BEST_AVAILABLE_POSITION = 0;
    private static final int BEST_AVAILABLE_OFFSET = 1;

    private final SortedArrayList<VpnPop> vpnPops;
    private final PopClickListener clickListener;

    public PopListRecyclerAdapter(@NonNull PopClickListener clickListener,
                                  @NonNull Comparator<VpnPop> comparator) {

        this.vpnPops = new SortedArrayList<>(comparator);
        this.clickListener = clickListener;
    }

    @Override
    public PopListRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View locationListView = inflater.inflate(R.layout.location_list_item, parent, false);

        return new PopListRecyclerAdapter.ViewHolder(locationListView, clickListener);
    }

    @Override
    public void onBindViewHolder(PopListRecyclerAdapter.ViewHolder viewHolder, int position) {
        if (position == BEST_AVAILABLE_POSITION) {
            viewHolder.bind(viewHolder.location.getContext());
            return;
        }

        final VpnPop vpnPop = vpnPops.get(position - BEST_AVAILABLE_OFFSET);
        viewHolder.bind(vpnPop);
    }

    @Override
    public int getItemCount() {
        return vpnPops.size() + BEST_AVAILABLE_OFFSET;
    }

    public void setVpnPops(List<VpnPop> vpnPops) {
        this.vpnPops.clear();
        this.vpnPops.addAll(vpnPops);
        notifyDataSetChanged();
    }

    public void setComparator(@NonNull Comparator<VpnPop> comparator) {
        vpnPops.setComparator(comparator);
    }

    public void animateTo(List<VpnPop> vpnPops) {
        applyAndAnimateRemovals(vpnPops);
        applyAndAnimateAdditions(vpnPops);
        applyAndAnimateMovedItems(vpnPops);
    }

    private void applyAndAnimateRemovals(List<VpnPop> newVpnPops) {
        for (int i = vpnPops.size() - 1; i >= 0; i--) {
            final VpnPop vpnPop = vpnPops.get(i);
            if (!newVpnPops.contains(vpnPop)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<VpnPop> newVpnPops) {
        for (int i = 0, count = newVpnPops.size(); i < count; i++) {
            final VpnPop vpnPop = newVpnPops.get(i);
            if (!vpnPops.contains(vpnPop)) {
                addItem(i, vpnPop);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<VpnPop> newVpnPops) {
        for (int toPosition = newVpnPops.size() - 1; toPosition >= 0; toPosition--) {
            final VpnPop vpnPop = newVpnPops.get(toPosition);
            final int fromPosition = vpnPops.indexOf(vpnPop);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    private VpnPop removeItem(int position) {
        final VpnPop vpnPop = vpnPops.remove(position);
        notifyItemRemoved(position + BEST_AVAILABLE_OFFSET);
        return vpnPop;
    }

    private void addItem(int position, VpnPop vpnPop) {
        vpnPops.add(position, vpnPop);
        notifyItemInserted(position + BEST_AVAILABLE_OFFSET);
    }

    private void moveItem(int fromPosition, int toPosition) {
        final VpnPop vpnPop = vpnPops.remove(fromPosition);
        vpnPops.add(toPosition, vpnPop);
        notifyItemMoved(fromPosition + BEST_AVAILABLE_OFFSET, toPosition + BEST_AVAILABLE_OFFSET);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView location;
        private ImageView flag;

        private final PopClickListener clickListener;

        private VpnPop vpnPop;
        private final FlagResource flagResource;

        ViewHolder(View itemView,
                   PopClickListener clickListener) {

            super(itemView);
            itemView.setOnClickListener(this);

            this.clickListener = clickListener;
            this.flagResource = new FlagResource(itemView.getContext());

            location = itemView.findViewById(R.id.location);
            flag = itemView.findViewById(R.id.location_flag_icon);
        }

        public void bind(VpnPop vpnPop) {
            this.vpnPop = vpnPop;
            location.setText(getFormattedLocation(vpnPop));
            String country = vpnPop.getCountryCode().toLowerCase(Locale.US);
            int flagDrawableId = flagResource.forCountry(country);
            flag.setImageResource(flagDrawableId);
        }

        /**
         * Binder for Best Available Location
         *
         * @param context The view context
         */
        public void bind(Context context) {
            flag.setImageResource(R.mipmap.ic_launcher_round);
            location.setText(context
                    .getResources().getString(R.string.best_available));
        }

        @Override
        public void onClick(View view) {
            clickListener.onPopItemClick(vpnPop);
        }
    }
}




