package com.wlvpn.slider.whitelabelvpn.holders;

public class EncryptionPagerHolder {

    private final int mImageResource;
    private final int mTitleResource;
    private final int mSubtitleResource;

    public EncryptionPagerHolder(int imageResourceId, int title, int subtitle) {
        mImageResource = imageResourceId;
        mTitleResource = title;
        mSubtitleResource = subtitle;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public int getTitleResource() {
        return mTitleResource;
    }

    public int getSubtitleResource() {
        return mSubtitleResource;
    }
}
