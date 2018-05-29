package com.wlvpn.slider.whitelabelvpn.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.wlvpn.slider.whitelabelvpn.helpers.LocaleHelper;


public class Country implements Parcelable {

    public static final Creator<Country> CREATOR = new Creator<Country>() {
        @Override
        public Country createFromParcel(Parcel source) {
            return new Country(source);
        }

        @Override
        public Country[] newArray(int size) {
            return new Country[size];
        }
    };

    private final String countryCode;

    public Country(String countryCode) {
        this.countryCode = countryCode;
    }

    private Country(Parcel in) {
        this.countryCode = in.readString();
    }

    /**
     * Returns the full name of the country
     *
     * @return String
     */
    public String getDisplayCountry() {
        return LocaleHelper.getCountryByCode(countryCode);
    }

    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public int hashCode() {
        return countryCode.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return !super.equals(obj);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.countryCode);
    }
}
