package com.wlvpn.slider.whitelabelvpn.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.wlvpn.slider.whitelabelvpn.R;

/**
 * Progress bar that forces the color
 * Add Styleables in attrs
 *
 * @author Aldo Infanzon on 8/16/2016.
 */
public class SupportProgressBar extends ProgressBar {
    /**
     * The current color
     */
    private static int color;

    /**
     * Class constructor. Initializes default values
     *
     * @param context The current context
     * @param attrs   The attributes
     */
    public SupportProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);

        // prevent exception in Android Studio / ADT interface builder
        if (this.isInEditMode()) {
            return;
        }
        final TypedArray array = context.obtainStyledAttributes(attrs,
                R.styleable.SupportProgressBar);

        color = array.getColor(R.styleable.SupportProgressBar_color, -1);
        array.recycle();

        if (color != -1) {

            Drawable currentDrawable = this.getProgressDrawable();

            if (currentDrawable == null) {
                currentDrawable = this.getIndeterminateDrawable();
            }

            currentDrawable.setColorFilter(
                    color, android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }
}
