package com.wlvpn.slider.whitelabelvpn.models;

import android.os.Parcel;

public class Connectable implements IConnectable {

    private String countryCode;

    private String country;

    private String city;

    private String hostname;

    private String ipAddress;

    private Connectable(
            String countryCode,
            String country,
            String city,
            String hostname,
            String ipAddress
    ) {
        this.countryCode = countryCode;
        this.country = country;
        this.city = city;
        this.hostname = hostname;
        this.ipAddress = ipAddress;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(Connectable connectable) {
        return new Builder(connectable);
    }

    @Override
    public String getCountryCode() {
        return countryCode;
    }

    @Override
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public String getCity() {
        return city;
    }

    @Override
    public String getHostname() {
        return hostname;
    }

    @Override
    public String getIpAddress() {
        return ipAddress;
    }

    /**
     * Formats the Location and country for display
     *
     * @return String
     */
    public String getFormattedLocation() {
        return getCity() + ", " + getCountry();
    }

    /**
     * Builder for immutable filter objects.
     */
    public static class Builder {

        private String countryCode;

        private String country;

        private String city;

        private String hostname;

        private String ipAddress;

        public Builder() {
        }

        public Builder(Connectable connectable) {
            this.countryCode = connectable.getCountryCode();
            this.country = connectable.getCountry();
            this.city = connectable.getCity();
            this.hostname = connectable.getHostname();
            this.ipAddress = connectable.getIpAddress();
        }

        public Builder countryCode(String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        public Builder country(String country) {
            this.country = country;
            return this;
        }

        public Builder city(String city) {
            this.city = city;
            return this;
        }

        public Builder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }

        public Builder ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        public Connectable build() {
            if (countryCode == null &&
                    country == null &&
                    city == null &&
                    hostname == null) {
                return null;
            }
            return new Connectable(
                    countryCode,
                    country,
                    city,
                    hostname,
                    ipAddress
            );
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.countryCode);
        dest.writeString(this.country);
        dest.writeString(this.city);
        dest.writeString(this.hostname);
        dest.writeString(this.ipAddress);
    }

    protected Connectable(Parcel in) {
        this.countryCode = in.readString();
        this.country = in.readString();
        this.city = in.readString();
        this.hostname = in.readString();
        this.ipAddress = in.readString();
    }

    public static final Creator<Connectable> CREATOR = new Creator<Connectable>() {
        @Override
        public Connectable createFromParcel(Parcel source) {
            return new Connectable(source);
        }

        @Override
        public Connectable[] newArray(int size) {
            return new Connectable[size];
        }
    };
}

