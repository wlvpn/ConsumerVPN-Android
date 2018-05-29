package com.wlvpn.slider.whitelabelvpn.widget;

import android.content.Context;
import android.support.annotation.RawRes;
import android.widget.RelativeLayout;

import com.eftimoff.androipathview.PathView;
import com.wlvpn.slider.whitelabelvpn.R;
import com.wlvpn.slider.whitelabelvpn.views.SliderPageView;

@SuppressWarnings("SameParameterValue")
public class SliderImagePage extends RelativeLayout implements SliderPageView {

    private final PathView pathView;

    public SliderImagePage(Context context) {
        super(context);
        inflate(getContext(), R.layout.view_encryption, this);
        pathView = findViewById(R.id.encryption_image);
    }

    public void setPathViewSource(@RawRes int svg) {
        pathView.setSvgResource(svg);
    }

    @Override
    public void setXOffset(float offset) {
        pathView.setPercentage(1f - Math.abs(clamp(offset, -1f, 1f)));
    }

    private static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

}
