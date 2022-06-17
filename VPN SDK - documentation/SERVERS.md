# Servers

Fetching servers is how your app can obtain specific information about our VPN servers.

## `VpnServer`

Class: [com.gentlebreeze.vpn.sdk.model.VpnServer][1]

Model that with information about a certain server.

- **`capacity`**: `int` The server capacity
- **`ipAddress`**: `String` The server IP Address
- **`isInMaintenance`**: `boolean` reveals if the selected server is in maintenance.
- **`name`**: `String` The server name identifier.
- **`popName`**: `String` The name of the pop that houses this server.
- **`scheduledMaintenance`**: `long` The date of the next scheduled maintenance.

## `SortServerOption`

Class: [com.gentlebreeze.vpn.sdk.sort.SortServerOption][2]

Enum class that holds options for sorting a list of servers.

- **`COUNTRY`**: Orders the list by the server's country.
- **`CITY`**: Orders the list by the server's city.
- **`NAME`**: Orders the list by the server's name.

## `SortOrder` (Enum)

Class: [com.gentlebreeze.vpn.sdk.sort.SortOrder][3]

Enum class that holds options for ordering alphabetically a list of servers.

- **`ASC`**: Orders the list alphabetically ascending.
- **`DESC`**: Orders the list alphabetically descending.

## `SortServer`

Class: [com.gentlebreeze.vpn.sdk.sort.SortServer][4]

Sort server is a helper model to define a sorting behaviour for fetching methods

### Constructor

To Initialize use `SortServerOption` and `SortOrder` values

## Server List Fetching Related Methods

### `fetchAllServers(...)`
```kotlin
/** Returns a callback with list of servers **/
fun fetchAllServers(): ICallback<List<VpnServer>>
```
Gets a list of all servers.
```kotlin
fun fetchAllServers(
   /** Defines the list sorting and ordering.  **/
   sortServer: SortServer
   /** Returns a callback with list of servers **/
): ICallback<List<VpnServer>>
```
Gets a list of all servers in specific order.
### `fetchAllServersByCountryCode(...)`
```kotlin
fun fetchAllServersByCountryCode(
   /** Two letter ISO country code eg. (US, MX) **/
   countryCode: String
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Gets a list of all servers filtered by country code.
```kotlin
fun fetchAllServersByCountryCode(
   /** Two letter ISO country code eg. (US, MX) **/
   countryCode: String,
   /** Defines the list sorting and ordering.  **/
   sortServer: SortServer
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Gets a list of all servers filtered by country code in a specific order.
### `fetchAllServersByCountryAndCity(...)`
```kotlin
fun fetchAllServersByCountryAndCity(
   /** Two letter ISO country code eg. (US, MX) **/
   countryCode: String, 
   /** The city name **/
   city: String
   /** Defines the list sorting and ordering.  **/
   sortServer: SortServer
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Gets a list of all servers filtered by country code and city in a specific order.
```kotlin
fun fetchAllServersByCountryAndCity(
   /** Two letter ISO country code eg. (US, MX) **/
   countryCode: String,
   /** The city name **/
   city: String
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Get a list of all servers filtered by country code and city.
### `fetchAllServersByPop(...)`
```kotlin
fun fetchAllServersByPop(
   /** a pop to filter the server list **/
   pop: VpnPop
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Get a list of all servers in a given pop.
```kotlin
fun fetchAllServersByPop(
   /** a pop to filter the server list **/
   pop: VpnPop,
   /** Defines the list sorting and ordering.  **/
   sortServer: SortServer
   /** Returns a callback with a list of servers **/
): ICallback<List<VpnServer>>
```
Get a list of all servers in a given pop in specific order.

The returned callback will respond with either of these two:
- **`OnNext`**: A List of `VpnServer`
- **`OnError`**: throws a `Throwable` object

Don't forget to call  `Callback.unsubscribe()`  in the activity's `onDestroy` or 
`onPause` lifecycle methods.

## Single Server Fetching Related Methods

### `fetchServerByName(...)`
```kotlin
fun fetchServerByName(
   /** The name of the server **/
   name: String
   /** Returns a callback with a list of servers **/
): ICallback<VpnServer>
```
Fetches a server by its name.
        
Don't forget to call  `Callback.unsubscribe()`  in the activity's `onDestroy` or 
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
        return MyApplication.vpnSdk?
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

[1]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-server/index.html
[2]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-server-option/index.html
[3]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-order/index.html
[4]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-server/index.html