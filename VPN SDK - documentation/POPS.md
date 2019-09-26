# Pops

`POP` stands for _point of presence_.  A pop is generally a location somewhere in the world that houses our VPN servers.

Fetching pops is how your app can obtain location information for our pops. This information could be useful in filtering  to specific countries and/or cities.

## `VpnPop`

Class: [com.gentlebreeze.vpn.sdk.model.VpnPop][1]

Object class that contains information about a certain pop

- **`city`**: `String` value containing the city where this pop is located
- **`country`**: `String` value containing the country where this pop belongs.
This value will be stored in the selected Locale
- **`countryCode`**: `String` value containing the ISO two letter country code unique identifier
- **`latitude`**: `Double` value containing the location in decimal degrees latitude
- **`longitude`**: `Double` value containing the location in decimal degrees longitude

## `SortPopOption`

Class: [com.gentlebreeze.vpn.sdk.sort.SortPopOption][2]

Enum class that holds options for sorting a pops list

- **`COUNTRY`**: Use this sort enum value to order the list by country 
- **`CITY`**: Use this sort enum value to order the list by city
- **`NAME`**: Use this sort enum value to order the list by pop name

## `SortOrder` (Enum)

Class: [com.gentlebreeze.vpn.sdk.sort.SortOrder][3]

Enum class that holds options for ordering alphabetically a pops list

- **`ASC`**: Use this sort enum value to show ascending alphabetically order
- **`DESC`**: Use this sort enum value to descending alphabetically order

## `SortPop`

Sort pop is a helper model to define a sorting behaviour for fetching methods

### Constructor

To Initialize use `SortPopOption` and `SortOrder` values

## Pop Fetching Related Methods

1. `ICallback<List<VpnPop>> fetchAllPops()`
    - This method will request all available pops
2. `ICallback<List<VpnPop>> fetchAllPops(sortPop))`
    - This method will request all available pops sorted and ordered
        - **`sortPop`**: `SortPop` object model to define how the list will be sorted and ordered
3. `ICallback<List<VpnPop>> fetchAllPopsByCountryCode(countryCode)`
    - This method will request all available pops by a certain country
        - **`countryCode`**: `String` value with the country code unique identifier
4. `ICallback<List<VpnPop>> fetchAllPopsByCountryCode(countryCode, sortPop)`
    - This method will request all available pops by a certain country code sorted and ordered
        - **`countryCode`**: `String` value with the country code unique identifier
        - **`sortPop`**: `SortPop` object model to define how the list will be sorted and ordered
5. `ICallback<Boolean> updatePopsCountriesLanguage(locale)`
    - This method will update all pops country name to the desire language
        - **`locale`**: `Locale` The desire locale containing the desire country Ex. Locale("es", "MX")

All callbacks will return:
- **`OnNext`**: A List of `VpnPop` object
- **`OnError`**: throws a `Throwable` object

All callbacks must be `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle method

### Examples

### Java Example
        
```java
public class PopListActivity extends AppCompatActivity {
    
    private ICallback<List<VpnPop>> vpnPopCallback;
    
    // Setup your activity controls and interactions
    
    @Override
    public void onDestroy() {
        if (vpnPopCallback != null) {
            vpnPopCallback.unsubscribe();
        }
        
        super.onDestroy();
    }
    
    public ICallback<List<VpnPop>> getVpnPopCallback() {
        return MyApplication.getVpnSdk()
                .fetchAllPops(new SortPop(SortPopOption.COUNTRY, SortOrder.DESC))
                .onNext(vpnPopsList -> {
                    // Manipulate vpnPop List like set it into a Recycler View Adapter
                    return Unit.INSTANCE;
                })
                .onError(throwable -> {
                    throwable.printStackTrace();
                    // Handle any error scenario in here
                    return Unit.INSTANCE;
                })
                .subscribe();
    }
}
```

#### Kotlin Example

```kotlin
class PopListActivity : AppCompatActivity() {
    
    private val vpnPopCallback: ICallback<List<VpnPop>>? = null
    
    // Setup yor activity controls and interactions
    
    override fun onDestroy() {
        vpnPopCallback?.unsubscribe()

        super.onDestroy()
    }
    
    fun getVpnPopCallback(): ICallback<List<VpnPop>> {
        return MyApplication.vpnSdk!!
                .fetchAllPops(SortPop(SortPopOption.COUNTRY, SortOrder.DESC))
                .onNext { vpnPopsList ->
                    // Manipulate vpnPop List like set it into a Recycler View Adapter
                    Unit
                }
                .onError { throwable ->
                    throwable.printStackTrace()
                    // Handle any error scenario in here
                    Unit
                }
                .subscribe()
    }
}
```

## Disclaimer

This is not an official Google product. 

[1]: Javadocs/myClass.html
[2]: Javadocs/myClass.html
[3]: Javadocs/myClass.html