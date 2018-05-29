package com.wlvpn.slider.whitelabelvpn.utilities;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.view.View;

public class AnimationUtil {

    /**
     * Fade in View animation helper
     *
     * @param view              The view to be fade in
     * @param duration          Duration for the animation in milliseconds
     * @param animationListener Nullable animation listener to listen to start end or animation repeat
     */
    public static void fadeInView(final View view,
                                  long duration,
                                  @Nullable AnimationListener animationListener) {
        view.setAlpha(0.0f);
        view.setVisibility(View.VISIBLE);

        ViewCompat.animate(view)
                .alpha(1.0f)
                .withEndAction(() -> {
                    view.setVisibility(View.VISIBLE);
                    view.clearAnimation();
                })
                .setDuration(duration)
                .setListener(animationListener)
                .start();
    }

    /**
     * Fade in View animation helper
     *
     * @param view              The view to be fade out
     * @param duration          Duration for the animation in milliseconds
     * @param animationListener Nullable animation listener to listen to start end or animation repeat
     */
    public static void fadeOutView(final View view,
                                   long duration,
                                   @Nullable AnimationListener animationListener) {
        //noinspection Convert2Lambda
        ViewCompat.animate(view)
                .alpha(0.0f)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        view.setVisibility(View.GONE);
                        view.clearAnimation();
                    }
                })
                .setDuration(duration)
                .setListener(animationListener)
                .start();
    }

    /**
     * This class will be a helper to avoid implementing all animation methods
     * on traditional Animation.AnimationListener
     */
    public static class AnimationListener
            implements ViewPropertyAnimatorListener {

        @Override
        public void onAnimationStart(View view) {
        }

        @Override
        public void onAnimationEnd(View view) {
        }

        @Override
        public void onAnimationCancel(View view) {
        }
    }
}
