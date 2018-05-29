package com.wlvpn.slider.whitelabelvpn.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.wlvpn.slider.whitelabelvpn.holders.EncryptionPagerHolder;
import com.wlvpn.slider.whitelabelvpn.widget.SliderImagePage;

import java.util.List;

/**
 * EncryptionPagerAdapter
 * <p>
 * Handles the Pager Adapter for the Encryption picker
 * Will display a set of static views
 */
@SuppressWarnings("SameParameterValue")
public class EncryptionPagerAdapter extends PagerAdapter {

    private final List<EncryptionPagerHolder> tabs;

    public EncryptionPagerAdapter(List<EncryptionPagerHolder> tabs) {
        this.tabs = tabs;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SliderImagePage sliderImagePage = new SliderImagePage(container.getContext());
        sliderImagePage.setPathViewSource(tabs.get(position).getImageResource());
        container.addView(sliderImagePage);
        return sliderImagePage;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getCount() {
        return tabs.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object item) {
        container.removeView((View) item);
    }
}
