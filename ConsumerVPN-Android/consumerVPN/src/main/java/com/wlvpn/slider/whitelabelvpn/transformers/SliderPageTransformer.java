package com.wlvpn.slider.whitelabelvpn.transformers;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.wlvpn.slider.whitelabelvpn.views.SliderPageView;

public class SliderPageTransformer implements ViewPager.PageTransformer {

    private final Handler handler;

    public SliderPageTransformer() {
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void transformPage(View page, float position) {
        final SliderPageView sliderPageView = (SliderPageView) page;
        handler.post(new OffsetRunner(sliderPageView, position));
    }

    private static final class OffsetRunner implements Runnable {
        private final SliderPageView sliderPageView;
        private final float position;

        public OffsetRunner(SliderPageView sliderPageView, float position) {
            this.sliderPageView = sliderPageView;
            this.position = position;
        }

        @Override
        public void run() {
            sliderPageView.setXOffset(position);
        }
    }
}