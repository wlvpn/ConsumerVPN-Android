# CHANGELOG

## v1.5.1

## Feature

The VPN initialization callback is now supported in Fragment's `startActivityForResult` 

Introduction of Split tunneling per application and discovery accessibility of the LAN,
 see VpnConnectionConfiguration.Builder

Introduction of Builder pattern for the VPN configuration

    ```Java
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

##Feature

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

## Features

* Added in debug level configuration option to `VpnConnectionConfiguration` default is level 3 with an available
range of 0 to 11 with 0 having no logging.
* Added in request and response logging for debugging purposes
* Deprecated `getGeoInfo()` and `fetchIpGeoLocation()` in replacement of only `fetchGeoInfo()`. 
This will eliminate the confusion of what geo call to use and will handle geo state when 
connected and disconnected behind the scenes.
* Added in `fetchAvailableVpnPortOptions()` to get the available ports you can make for connections.
* Fetch calls have been upgraded with sort options. This will add in ORDER BY statements to your queries.
See the SortPop and SortServer objects for reference. They can be used like so:
    ```
    //NAME is table column, ASC is table row order
    fetchAllServers(SortServer(SortServerOption.NAME, SortOrder.ASC))
    ```
* Added in 3 new Auth helpers:
    ```
        // Is current stored access token still valid?
        fun isAccessTokenValid(): Boolean
        
        // Is access token valid and not expired?
        fun isUserLoggedIn(): Boolean
        
        // Refresh the token using the stored refresh token
        fun refreshToken()
    ```
    
## Bug Fixes

* Login request will now properly pass along error when the login attempt has failed
* Fixed an issue where configuration generation sometimes created duplicate options
* Improved state handling and suppressed connection events that could lead to a faulty disconnect event
* Better state pushes to avoid redundancy

## v1.2

### Features

* Added data usage record to the `listenToConnectState` method
* Added in new method to retrieve information about the connected server:
    ```
    fetchConnectionInfo() : ICallback<VpnConnectionInfo>
    ```
* Added in new connect overload method that allows you to connect without selecting
a server or pop. SDK will use your geolocation to determine best server and pop.
    ```
    fun connect(notification: VpnNotification,
                             configuration: VpnConnectionConfiguration): ICallback<Boolean>
    ```
* Added in a new connect overload method that allows you to connect by two letter country code
    ```
    fun connect(countryCode: String,
                             notification: VpnNotification,
                             configuration: VpnConnectionConfiguration): ICallback<Boolean>
    ```

## v1.1

### Features

* Added the following fetch calls
    ```
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