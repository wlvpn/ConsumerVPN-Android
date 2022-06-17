# IP Geolocation

Fetching and getting current user ip geolocation information will help you to 
determine the current user's location using only the reported global IP address.

## `VpnGeoData`

Class: [com.gentlebreeze.vpn.sdk.model.VpnGeoData][1]

Model that contains information about a certain ip Geo Location.

- **`geoCity`**: `String` The user's city name.
- **`geoCountryCode`**: `String` The user's two letter ISO country code representation (e.g. US, UK)
- **`geoIp`**: `String` The device current public ip.
- **`geoLatitude`**: `Double` The user's longitude coordinate.
- **`geoLongitude`**: `Double` The user's latitude coordinate.

## VpnGeoData Fetching Related Methods

---

### `fetchGeoInfo()`
Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][2]

```kotlin
/** Returns a callback with a VpnGeoData object **/
fun fetchGeoInfo(): ICallback<VpnGeoData>
```
This method will obtain relevant information about the user's device public current IP.

The returned callback will respond with either of these two:
- **`OnSuccess`**: with a `VpnGeoData` object.
- **`OnError`**: with a `Throwable` object.

Don't forget to call  `Callback.unsubscribe()`  in the activity's `onDestroy` or 
`onPause` lifecycle methods.

If the fetch method fails any `best available` connection will connect to the previous 
city. In case that there is no previous city or country selected, the country by default 
will be the `US`

>**`Important`**: Geolocation will change to reflect current connection values. For
example if a user is connected only to the ISP (Internet Service Provider) these values
will represent the location reported by the ISP. Otherwise if the user is connected
to the VPN these values will represent the VPN location instead.

## Example

---

```kotlin
class IPGeoActivity : AppCompatActivity() {
    
    private val vpnGeoDataCallback: ICallback<VpnGeoData>? = null
    
    // Setup yor activity controls and interactions
    
    override fun onDestroy() {
        vpnGeoDataCallback?.unsubscribe()
        vpnGeoDataCallback = null

        super.onDestroy()
    }
    
    fun getVpnPopCallback(): ICallback<VpnGeoData>? {
    
        return MyApplication.vpnSdk?.fetchGeoInfo()
                ?.subscribe({vpnGeoData:VpnGeoData -> 
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
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk/-i-vpn-sdk/index.html