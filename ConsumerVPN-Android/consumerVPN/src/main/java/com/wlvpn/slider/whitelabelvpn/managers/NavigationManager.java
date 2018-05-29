package com.wlvpn.slider.whitelabelvpn.managers;


import android.support.annotation.IntDef;

import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;

@Singleton
public class NavigationManager {

    public static final int LOCATION_SLIDER = 1;
    public static final int LOCATION_CONNECTING = 2;
    public static final int LOCATION_CONNECTED = 3;

    private BehaviorSubject<Integer> currentLocation;

    public NavigationManager() {
        //Default location is slider
        currentLocation = BehaviorSubject.create(LOCATION_SLIDER);
    }

    public void navigateTo(@Navigation int location) {
        @Navigation
        int currentLocationValue = currentLocation.getValue();
        if (currentLocationValue != location) {
            currentLocation.onNext(location);
        }
    }

    public Observable<Integer> getLocationObservable() {
        return currentLocation;
    }

    @IntDef({LOCATION_SLIDER, LOCATION_CONNECTING, LOCATION_CONNECTED})
    public @interface Navigation {
    }

    public int getCurrentLocation() {
        return currentLocation.getValue();
    }
}
