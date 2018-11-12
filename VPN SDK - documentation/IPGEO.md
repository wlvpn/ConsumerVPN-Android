# IP Geolocation

Fetching and getting current user ip geolocation information will help you to 
determine the current user's location using only the reported global IP address.

## `VpnGeoData`

Class: [com.gentlebreeze.vpn.sdk.model.VpnGeoData][1]

Object class that contains information about a certain ip Geo Location

- **`geoCity`**: `String` value that contains current IP user's current city
- **`geoCountryCode`**: `String` value containing current IP Country code identifier
- **`geoIp`**: `String` value that contains current device global IP address
- **`geoLatitude`**: `Double` value containing the IP geo longitude coordinate
- **`geoLongitude`**: `Double` value containing the IP geo latitude coordinate

## VpnGeoData Fetching Related Methods

1. `ICallback<VpnGeoData> fetchGeoInfo()`
    - This method will obtain relevant information about the current IP
    
All callbacks will return:
- **`OnSuccess`**: returns a `VpnGeoData` object
- **`OnError`**: throws a `Throwable` object

All callbacks must be `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle method

**`Important`**: Geolocation will change to reflect current connection values. For 
example if a user is connected only to the ISP (Internet Service Provider) these values
will represent the location reported by the ISP. Otherwise if the user is connected
to the VPN these values will represent the VPN location instead.

If the fetch method fails any `best available` connection will connect to the previous 
city. In case that there is no previous city or country selected, the country by default 
will be the `US`

### Examples

### Java example
        
```java
public class IPGeoActivity extends AppCompatActivity {
    
    private ICallback<VpnGeoData> vpnGeoDataCallback;
    
    // Setup yor activity controls and interactions
    
    @Override
    public void onDestroy() {
        if (vpnGeoDataCallback != null) {
            vpnGeoDataCallback.unsubscribe();
            vpnGeoDataCallback = null;
        }
        
        super.onDestroy();
    }
    
    public ICallback<VpnGeoData> getVpnGeoDataCallback() {
        return MyApplication.getVpnSdk()
                .fetchGeoInfo()
                .subscribe(vpnGeoData -> {
                    // Manipulate the geo info data in here
                    return Unit.Instance;
                }, throwable -> {
                    // Handle any error scenario in here
                });
    }
}
```

#### Kotlin example

```kotlin
class IPGeoActivity : AppCompatActivity() {
    
    private val vpnGeoDataCallback: ICallback<VpnGeoData>? = null
    
    // Setup yor activity controls and interactions
    
    override fun onDestroy() {
        vpnGeoDataCallback?.unsubscribe()
        vpnGeoDataCallback = null

        super.onDestroy()
    }
    
    fun getVpnPopCallback(): ICallback<VpnGeoData> {
    
        return MyApplication.vpnSdk!!.fetchGeoInfo()
                .subscribe({vpnGeoData:VpnGeoData -> 
                    // Manipulate the geo info data in here
                    Unit
                }, {throwable: Throwable -> 
                    // Handle any error scenario in here
                    Unit
                })
    }
}
```

[1]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-geo-data/index.html