# CHANGELOG

## v1.5.3 

### Breaking Changes

* [Dagger v2.21][Dagger] is required for this version to work correctly

* Added a new logTag, this new tag is used to override the Log output with any desired suffix

```kotlin

    //Example using a builder
     SdkConfig = SdkConfig.Builder(
            BuildConfig.ACCOUNT_NAME,
            BuildConfig.API_KEY,
            BuildConfig.AUTH_SUFFIX)
            .client(BuildConfig.CLIENT)
            .apiHost(application.getString(R.string.endpoint_main_api))
            .ipGeoUrl(BuildConfig.IP_GEO)
            .apiLoginEndpoint(BuildConfig.LOGIN_API)
            .apiTokenRefreshEndpoint(BuildConfig.REFRESH_API)
            .apiProtocolListEndpoint(BuildConfig.PROTOCOL_LIST_API)
            .apiServerListEndpoint(BuildConfig.SERVER_LIST_API)
            .logTag(MY_LOG_TAG)
            .build()

    //Example using the default constructor
    SdkConfig(
              val accountName: String,
              override val apiKey: String,
              val authSuffix: String = accountName,
              override val client: String = BuildConfig.CLIENT,
              override val apiHost: String = BuildConfig.ENDPOINT,
              override val ipGeoUrl: String = BuildConfig.IP_GEO,
              override val apiLoginEndpoint: String = BuildConfig.LOGIN_API,
              override val apiTokenRefreshEndpoint: String = BuildConfig.TOKEN_REFRESH_API,
              override val apiProtocolListEndpoint: String = BuildConfig.PROTOCOL_LIST_API,
              override val apiServerListEndpoint: String = BuildConfig.SERVER_LIST_API,
              val logTag: String
```
      

* `fetchAvailableVpnPortOptions(vpnProtocolOptions, scrambleEnabled)` was deprecated
use instead `fetchAvailableVpnPortOptions(vpnProtocolOptions, vpnConnectionProtocolOptions, scrambleEnabled)`

* Permissions will no longer be requested automatically
    ```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"/>
    ```

* Update servers will no longer update protocols only servers

* Connections to the VPN now require a new argument `vpnRevokedNotification`.
This notification will be shown whenever the system revokes the permission for the
VPN to keep running.It is only shown when the VPN is connected and the system revokes
necessary permissions
    ```kotlin
    ICallback<Boolean> connect(vpnNotification, vpnRevokedNotification, vpnConnectionConfiguration)
    ```

### Bug fixes

* Fix for the Vpn detailed state description not showing
while listening for the disconnected state

* OpenVpn ports will now fetch ports where the cipher is not null using the new function
`fetchAvailableVpnPortOptions(vpnProtocolOptions, vpnConnectionProtocolOptions, scrambleEnabled)`

* Fixed VPN notification dismissal after several network changes

* Fixed Pops sorting functionality for search queries, now it prioritize strings starting with the query
Added the following methods for query search sorting:

```kotlin
    //Fetch a list of all pops sorted by query in specific order, first by city
    fetchPopsFirstByCityQuery(query: String, sortPop: SortPop)

    //Fetch a list of all pops sorted by query in specific order, first by country
    fetchPopsFirstByCountryQuery(query: String, sortPop: SortPop)

    //Fetch a list of all pops by country query, sorted and filtered by city query in specific order
    fetchPopsByCountryCodeFilterByCityQuery(countryCode: String, query: String, sortPop: SortPop)

    Fetch a list of all pops sorted by country query in specific order
    fetchPopsByCountryQuery(query: String)

    Fetch a list of all pops filtered by country code, sorted by city query in specific order
    fetchPopsByCityQuery(countryCode: String, query: String)
```

### Feature

* Added a listener for connection state for centralized operations.
Allows to execute custom centralized operations
that react to the VPN connection states. For example, it can be used to notify Widgets,
Quick Tiles Settings or Services. Important: Only use centralized single operations
otherwise is better to use `listenToConnectState()`
    ```kotlin
        listenToCentralizedConnectionState(vpnStateConnectionCallback)
    ```

* Added fetch available ports with connection settings.
Replaces `fetchAvailableVpnPortOptions(vpnProtocolOptions, scrambleEnabled)`
    ``` kotlin
        fetchAvailableVpnPortOptions(vpnProtocolOptions, vpnConnectionProtocolOptions, scrambleEnabled)
    ```

* Deprecated `connect(VpnPop)` in replacement of only `connectToNearest(VpnPop)`.

* Deprecated `connect(countryCode)` in replacement of only `connectToNearest(notification)`. Balancing is
    performed according to saved location in configuration object

* Deprecated `connect(notification)` in replacement of only `connectToNearest(notification)`.

* Added `connectToNearest(VpnPop)` to perform a default server balance, closest to farthest, no country limit

* Added `connectToNearest(notification)` to perform a default server balance, closest to farthest, no country limit

* Added `connectToNearestRestrictedByCountry(VpnPop)` to perform server balance limited to a given country

* Added `connectToNearestRestrictedByCountry(countryCode)` to perform server balance limited to a given country

* Added `connectToNearestRestrictedByCountry(notification)` to perform server balance limited to a given country


* Added update protocol and servers call. Replaces `updateServerList`

    ```kotlin
        fun updateServerProtocolList()
    ```

* Added update protocol list

    ```kotlin
        fun updateProtocolList()
    ```

* Added blocking thread capabilities to `ICallback` class
    ```kotlin
    fun blockCurrentThread()
    ```

* The `VpnPop -> country` parameter will be stored in English
instead of using the device `Locale`. To update the internal database use:
    ```kotlin
    fun updateServerList()
    ```

* Added two new fetch calls to retrieve all servers filtered by
country code `fetchAllServersByCountryCode`.

    ```kotlin
    fun fetchAllServersByCountryCode(countryCode:String): ICallback<List<VpnServer>>

    fun fetchAllServersByCountryCode(countryCode:String): ICallback<List<VpnServer>>
    ```

* The IDE will now show available docs in autocomplete window. eg. (Android Studio)

## v1.5.2

### Feature

New argument to override the MTU value when the user is on a mobile connection.

For some IPv6 only networks is necessary to override the MTU value to 1280
 to avoid issues with fragmentation, a new argument for 
 VPNConnectionConfiguration was added  `.shouldOverrideMobileMtu(true);` to
 add that behavior

```java
VpnConnectionConfiguration.Builder(
    credentials.getUsername(),
    credentials.getPassword())
    .shouldOverrideMobileMtu(true);
```


## v1.5.1

### Feature

The VPN initialization callback is now supported in Fragment's `startActivityForResult` 

Introduction of Split tunneling per application and discovery accessibility of the LAN,
 see VpnConnectionConfiguration.Builder

Introduction of Builder pattern for the VPN configuration

```java
    VpnConnectionConfiguration.Builder(
                        credentials.getUsername(),
                        credentials.getPassword())
                .connectionProtocol(VpnConnectionProtocolOptions.OPENVPN)
                .vpnProtocol(VpnProtocolOptions.PROTOCOL_UDP)
                .debugLevel(0)
                .scrambleOn(false)
                .port(VpnPortOptions.PORT_443)
                .build();
```
                
### Bug Fixes
* Fixed empty Observable for logout.subscribe(), the callback is executed after the logout is made. 

## v1.5.0

### Bug Fixes

* Supports empty values for the StrongSwan protocol in case is not supported by the API
* The token is properly refreshed using the access token 
* The methods to check if an access token is valid `isAccessTokenValid` informs of expiration 
three days prior expiration for renewal.
* The load balancing now always chose the closest city 
* The MTU is set to 1280 by default in mobile networks to avoid issues with IPv6
  
## v1.4

### Feature

Added in the long awaited Strongswan integration to the SDK. This will allow for connections
using the IKEv2/IPSec protocols implemented by Strongswan. This feature can be activated with 
the new option in the VpnConnectionConfiguration object. For now OpenVPN will remain the default
connection option due to better server support.

Here is the latest VpnConnectionConfiguration constructor with defaults: 

    VpnConnectionConfiguration(
        username: String,
        password: String,
        scrambleOn: Boolean = false,
        reconnectOn: Boolean = true,
        port: VpnPortOptions = VpnPortOptions.PORT_443,
        protocol: VpnProtocolOptions = VpnProtocolOptions.PROTOCOL_UDP,
        connectionProtocol: VpnConnectionProtocolOptions = VpnConnectionProtocolOptions.OPENVPN,
        debugLevel: Int = 5
    )
    
To activate Strongswan simply change the connectionProtocol value to `VpnConnectionProtocolOptions.IKEV2`.
This will require the API to support this connection protocol so ensure Strongswan is setup before
attempting.

This version also upgrades the database version and runs a migration to prepare for the new attributes
in the api to support the IKEv2 `remoteId` and `gateway` attributes.

    

## v1.3

### Features

* Added in debug level configuration option to `VpnConnectionConfiguration` default is level 3 with an available
range of 0 to 11 with 0 having no logging.
* Added in request and response logging for debugging purposes
* Deprecated `getGeoInfo()` and `fetchIpGeoLocation()` in replacement of only `fetchGeoInfo()`. 
This will eliminate the confusion of what geo call to use and will handle geo state when 
connected and disconnected behind the scenes.
* Added in `fetchAvailableVpnPortOptions()` to get the available ports you can make for connections.
* Fetch calls have been upgraded with sort options. This will add in ORDER BY statements to your queries.
See the SortPop and SortServer objects for reference. They can be used like so:
    ```kotlin
    // NAME is table column, ASC is table row order
    fetchAllServers(SortServer(SortServerOption.NAME, SortOrder.ASC))
    ```
* Added in 3 new Auth helpers:
    ```kotlin
        // Is current stored access token still valid?
        fun isAccessTokenValid(): Boolean
        
        // Is access token valid and not expired?
        fun isUserLoggedIn(): Boolean
        
        // Refresh the token using the stored refresh token
        fun refreshToken()
    ```
    
### Bug Fixes

* Login request will now properly pass along error when the login attempt has failed
* Fixed an issue where configuration generation sometimes created duplicate options
* Improved state handling and suppressed connection events that could lead to a faulty disconnect event
* Better state pushes to avoid redundancy

## v1.2

### Features

* Added data usage record to the `listenToConnectState` method
* Added in new method to retrieve information about the connected server:
    ```kotlin
    fetchConnectionInfo() : ICallback<VpnConnectionInfo>
    ```
* Added in new connect overload method that allows you to connect without selecting
a server or pop. SDK will use your geolocation to determine best server and pop.
    ```kotlin
    fun connect(notification: VpnNotification,
                             configuration: VpnConnectionConfiguration): ICallback<Boolean>
    ```
* Added in a new connect overload method that allows you to connect by two letter country code
    ```kotlin
    fun connect(countryCode: String,
                             notification: VpnNotification,
                             configuration: VpnConnectionConfiguration): ICallback<Boolean>
    ```

## v1.1

### Features

* Added the following fetch calls
    ```kotlin
    fun fetchServerByName(name: String): ICallback<VpnServer>

    fun fetchPopByName(name: String): ICallback<VpnPop>

    fun fetchAllPopsByCountryCode(countryCode: String) : ICallback<List<VpnPop>>

    fun fetchPopByCountryCodeAndCity(countryCode: String, city: String): ICallback<VpnPop>
    ```
* Failed login attempts will now return an error message in the throwable
* Added Server capacity to VpnServer model
* DeviceInfo is now publicly accessible through `getDeviceInfo()`
* AuthInfo is now publicly accessible through `getAuthInfo()`
* AccountInfo is now publicly accessible through `getAccountInfo()`
* GeoInfo is now publicly accessible through `getGeoInfo()`

### Bug Fixes

* SdkConfig should only require `accountName` and `apiKey` when implementing in java
* Fixed bug where `fetchAllServers()` and `fetchAllServersByPop()` would only return 1 result
* Fixed the Callback subscribe method so the callbacks are optional in both java and kotlin
* The SDK `init()` method no longer requires calling the companion object when implementing in java

[Dagger]: https://github.com/google/dagger