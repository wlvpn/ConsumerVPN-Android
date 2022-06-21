# Pops

`POP` stands for _point of presence_.  A pop is generally a location somewhere in the world that houses our VPN servers.

Fetching pops is how your app can obtain location information for our pops. This information could be useful in filtering  to specific countries and/or cities.

## `VpnPop`

Class: [com.gentlebreeze.vpn.sdk.model.VpnPop][1]

Object class that contains information about a certain pop

- **`name`**: `String` value containing the name of the pop
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

### `fetchAllPops(...)`
```kotlin
 /** Returns a callback with list of pops**/
 fun fetchAllPops(): ICallback<List<VpnPop>>
```
Get a list of all pops.

```kotlin
fun fetchAllPops(
   /** the sort order the list will be returned**/
  sortPop: SortPop
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Get a list of all pops in specific order
### `fetchPopsFirstByCityQuery(...)`
```kotlin
fun fetchPopsFirstByCityQuery(
   /** query to search by **/
   query: String,
   /** the sort order the list will be returned**/
   sortPop: SortPop
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Fetch a list of all pops sorted by query in specific order First by city.
### `fetchPopsFirstByCountryQuery(...)`
```kotlin
fun fetchPopsFirstByCountryQuery(
   /** query to search by **/
   query: String,
   /** the sort order the list will be returned**/
   sortPop: SortPop
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Fetch a list of all pops sorted by query in specific order First by country.
### `fetchPopsByCountryCodeFilterByCityQuery(...)`
```kotlin
 fun fetchPopsByCountryCodeFilterByCityQuery(
   /** Two letter ISO country code representation (e.g. US, UK) **/
     countryCode: String,
   /** query to search by **/
     query: String,
   /** the sort order the list will be returned**/
     sortPop: SortPop
     /** Returns a callback with list of pops**/
 ): ICallback<List<VpnPop>>
```
Fetch a list of all pops by country query, sorted by query in specific order.
### `fetchPopsByCityQuery(...)`
```kotlin
fun fetchPopsByCityQuery(
   /** Two letter ISO country code representation (e.g. US, UK) **/
   countryCode: String,
   /** query to search by **/
   query: String
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>

```
Fetch a list of all pops filtered by country code, sorted by city query in specific order.
### `fetchPopsByCountryQuery(...)`
```kotlin
fun fetchPopsByCountryQuery(
   /** query to search by **/
   query: String
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Fetch a list of all pops sorted by country query in specific order.
### `fetchPopsByCountryQueryAndVpnProtocol(...)`
```kotlin
fun fetchPopsByCountryQueryAndVpnProtocol(
   /** query to search by **/
   query: String,
   /** VPN Protocol to filter by **/
   vpnProtocol: String
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Fetch a list of all pops sorted by country query in specific order, filtered by protocol.
### `fetchAllPopsByCountryCode(...)`
```kotlin
fun fetchAllPopsByCountryCode(
   /** Two letter ISO country code representation (e.g. US, UK) **/
   countryCode: String
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Get a lists of all pops in a country.
```kotlin
fun fetchAllPopsByCountryCode(
   /** Two letter ISO country code representation (e.g. US, UK) **/
   countryCode: String,
   /** the sort order the list will be returned**/
   sortPop: SortPop
   /** Returns a callback with list of pops**/
): ICallback<List<VpnPop>>
```
Get a lists of all pops in a country in a specific order.
### `fetchPopByName(...)`
```kotlin
fun fetchPopByName(
   /** pop name to filter by **/
   name: String
   /** Returns a callback with a single pop **/
): ICallback<VpnPop>
```
Fetches a pop by its name.
### `fetchPopByCountryCodeAndCity(...)`
```kotlin
fun fetchPopByCountryCodeAndCity(
   /** Two letter ISO country code representation (e.g. US, UK) **/
   countryCode: String,
   /** country city to filter by **/
   city: String
   /** Returns a callback with a single pop**/
): ICallback<VpnPop>
```
Fetches a pop by its city name.
### `fetchPopByCountryCodeAndCityAndVpnProtocol(...)`
```kotlin
fun fetchPopByCountryCodeAndCityAndVpnProtocol(
   /** Two letter ISO country code representation (e.g. US, UK) **/
   countryCode: String,
   /** country city to filter by **/
   city: String,
   vpnProtocol: String
   /** Returns a callback with a single pop**/
): ICallback<VpnPop>
```
Fetches a pop by its city name and protocol

The returned callback will respond with either of these two:
- **`OnNext`**: A List of `VpnPop` object (or a single `VpnPop`)
- **`OnError`**: throws a `Throwable` object

All callbacks need must call `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle methods.

### Example

```kotlin
class PopListActivity : AppCompatActivity() {
    
    private val vpnPopCallback: ICallback<List<VpnPop>>? = null
    
    // Setup yor activity controls and interactions
    
    override fun onDestroy() {
        vpnPopCallback?.unsubscribe()

        super.onDestroy()
    }
    
    fun getVpnPopCallback(): ICallback<List<VpnPop>>? {
        return MyApplication.vpnSdk
                ?.fetchAllPops(SortPop(SortPopOption.COUNTRY, SortOrder.DESC))
                ?.onNext { vpnPopsList ->
                    // Manipulate vpnPop List like set it into a Recycler View Adapter
                }
                ?.onError { throwable ->
                    throwable.printStackTrace()
                    // Handle any error scenario in here
                }
                ?.subscribe()
    }
}
```

## Disclaimer

This is not an official Google product. 

[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-pop/index.html
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-pop-option/index.html
[3]: javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-order/index.html