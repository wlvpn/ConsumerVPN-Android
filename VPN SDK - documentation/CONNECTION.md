# VPN Connection

The connection API creates and secures a network tunneling connection for your VPN application.

## `AuthInfo`

Class: [com.gentlebreeze.vpn.sdk.model.AuthInfo][5]

A model class that holds information pertaining to a user's authentication.

- **`accessExpireEpoch`**: `long` The epoch time date when the access token expires.
- **`accessToken`**: `String` The user access token.
- **`accountUpdatedAt`**: `long` The epoch time date when the account was last updated.
- **`refreshToken`**: `String` The user refresh token.
- **`subEndEpoch`**: `long` The epoch time date when the user's subscription end.
- **`vpnAuthPassword`**: `String` The user's password.
- **`vpnAuthUsername`**: `String` The user's username.

## `VpnConfigurarion`

Class: [com.gentlebreeze.vpn.sdk.model.VpnConnectionConfiguration][2]

A model class that holds the configuration of a VPN connection.

- **`username`** `String` The user's username to access the VPN server.
- **`password`** `String` The user's password to access the VPN server.
- **`scrambleOn`** `Boolean` Indicates if scramble should be turned on/off.
- **`reconnectOn`** `Boolean` Indicates if the VPN should reconnect in a server dropout scenario.
- **`remoteId`** `String` The remote ID for IKEv2 connections.
- **`port`** `VpnPortOptions` The port value to perform the connection.
- **`protocol`** `VpnProtocolOptions` The internet protocol to perform the connection (UDP or TCP).
- **`connectionProtocol`** `VpnConnectionProtocolOptions` The VPN protocol 
  to use (WireGuard, OpenVPN or IKEv2).
- **`debugLevel`** `Integer`  The OpenVPN debug level (from 0 to 11)  
- **`isLocalLanEnabled`** `Boolean` Indicates to enable access to the local network 
  outside of the VPN tunnel. 
- **`splitTunnelApps`** `List<String>` The list of application packages that can connect
to the internet outside of the VPN tunnel.
- **`shouldOverrideMobileMtu`** `Boolean` indicates to override the MTU value when the user 
  is on a mobile connection.
- **`apiAuthMode`** `ApiAuthMode` The api auth method to use.
- **`shouldLoadBalanceRestrictByProtocol`** `Boolean` Indicates to apply a load balance with 
the supplied VPN protocol.

## Prepare related Methods

---

Before starting a connection, your app needs to ask for VPN permission from the user. 
Android will refuse to start the VPN service without the user's approval.

The following methods will help you to prepare for that condition.


### `prepareVpnService(...)`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
fun prepareVpnService(
        /** The activity that android will use to promp the VPN permissions dialog **/
        activity: Activity
    )
// OR
fun prepareVpnService(
    /** The fragment that android will use to promp the VPN permissions dialog **/
        fragment: androidx.fragment.app.Fragment
    )
```

### `isVpnServicePrepared()`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns true if the user approved the VPN permissions **/
fun isVpnServicePrepared(): Boolean
```

This will return true if the user has approved the VPN permission to your app.

## Connection related methods

---

### `connect(...)`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
fun connect(
   /** The desired server to connect **/
   server: VpnServer,
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean>
```
Connects to a VPN using the desired server.

### `connectToNearest(...)`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]


- #### By IpGeo

Connects to the best server relative to your geolocation with no preference for country or city.

```kotlin
fun connectToNearest(
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean>
```
- #### By VpnPop

Connects to a server from a given VpnPop if any available, after performing a load balance 
considering maintenance, server capacity and distance will be taken as the best suited 
for the connection. 

If there are no candidate servers available in the specified VpnPop, 
the nearest one from other VpnPop will be used, expanding the tolerance distance, first by checking
the nearest ones, then if the specified VpnPop's country does not have a single available server,
the search will be opened to the next near countries until finding one.
```kotlin
fun connectToNearest(
   /**  pop to attempt to connect to **/
   vpnPop: VpnPop,
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean> 
```

### `connectToNearestRestrictedByCountry`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

- #### By IpGeo

Connects to the best server relative to your geolocation with no preference for city but 
**restricted by the country obtained.**

```kotlin
fun connectToNearestRestrictedByCountry(
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean>
```

- #### By VpnPop

Connects to a server from a given VpnPop if any available, after performing a load balance 
considering maintenance, server capacity and distance one will be taken as the best suited for the 
connection.

If there are no candidate servers available in the specified VpnPop, the nearest one 
from other VpnPop will be used, expanding the tolerance distance, by checking the nearest ones, 
**but never outside the specified VpnPop's country.**
```kotlin
fun connectToNearestRestrictedByCountry(
   /** VpnPop to attempt to connect **/
    vpnPop: VpnPop,
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean>
```

- #### By Country Code

Connects to a server from a given country code if any available, after performing a load balance 
considering maintenance, server capacity and distance one will be taken as the best suited for the 
connection.

The nearest VpnPop will be used, expanding the tolerance distance, **but never outside
the specified country.**
```kotlin
fun connectToNearestRestrictedByCountry(
  /** Two letter ISO country code representation (e.g. US, UK) **/
    countryCode: String,
   /** Notification model builder helper to attach a persistent notification **/
   notification: VpnNotification,
   /** Notification model builder to show a notification when the system revokes the permission for the VPN to run **/
   vpnRevokedNotification: VpnNotification,
   /** A helper model with the configuration to run the VPN **/
   configuration: VpnConnectionConfiguration
   /** onSuccess responds with a success boolean.
   onError throws a Throwable object. **/
): ICallback<Boolean>
```
### `disconnect(...)`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** onSuccess responds with a success boolean.
onError throws a Throwable object. **/
 fun disconnect(): ICallback<Boolean>
```
Disconnects from a current VPN connection.

### `getConnectionState()`

```kotlin
/** Returns the last reported VPN state **/
fun getConnectionState(): Int
```
Gets the last known VPN state. See more about VPN states [here][9].

### `getConnectionDescription()`
Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns the description of current VPN state **/
fun getConnectionDescription(): VpnConnectionInfo
```

Gets the description of the current VPN State. See more about VPN states [here][9].

### `isConnected()`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns true if connected, false if disconnected and not attempting to connect. **/
fun isConnected(): Boolean
```

Determine if the device is connected to the VPN.

### `getConnectedDate()`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns the date of connection start time **/
fun getConnectedDate(): Date
```

Get the Date of the initial connection.

### `getConnectedTimeInSeconds()`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns the seconds passed since connected **/
fun getConnectedTimeInSeconds(): Long
```
Get Connected Time in Seconds.

### `getConnectionInfo()`

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

```kotlin
/** Returns the model with the connection info **/
fun getConnectionInfo(): VpnConnectionInfo
```
Get the current connection info



### `attemptToConnect.../attemtToDisconnect` variants

Interface: [com.gentlebreeze.vpn.sdk.IVpnSdk][8]

All the `connect...(...)` and `disconnect(...)` methods have a variant with the prefix `attemptTo...`; 
these variants do the exact same task as their counterpart, the main difference is that they don't 
return a `ICallback`, instead,they return an *RxJava2 completable* which doesn't complete until the VPN 
state changes to `Connected` for the connect methods and `Disconnected` for the disconnect method.


## Examples

---

In this example, we will attempt to connect to the VPN and setup a notification with a disconnect button.

#### AndroidManifest.xml

```xml
<manifest>
    <uses-permission android:name="android.permission.INTERNET" />
    <Application >
        <!-- Your other activities, services, etc -->
        
        <!-- This receiver is to handle the notification tile -->
        <receiver
            android:name=".MainActivity.VpnConnectionReceiver"
            android:exported="false">
            <intent-filter>
                <action android:name="com.myapp.action.DISCONNECT" />
            </intent-filter>
        </receiver>
    </Application>
</manifest>


```

### MainActivity

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener {
    
    // Setup yor activity controls and interactions
    
    val baseConnectionNotification: NotificationCompat.Builder
        get() {
            val bitmapIconLarge = BitmapFactory.decodeResource(
                    applicationContext.resources, R.drawable.ic_logo)

            return NotificationCompat.Builder(applicationContext,
                    "VpnNotificationChannel")
                    .setLocalOnly(false)
                    .setOngoing(true)
                    .setSmallIcon(R.drawable.ic_app_notification)
                    .setLargeIcon(bitmapIconLarge)
                    .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                    .setContentIntent(pendingOpenAppIntent)
                    .setUsesChronometer(true)
                    .setShowWhen(true)
                    .addAction(0, "Disconnect", pendingDisconnectIntent)
        }
     
     val vpnRevokedNotificationBuilder: NotificationCompat.Builder
             get() {
                 val bitmapIconLarge = BitmapFactory.decodeResource(
                         applicationContext.resources, R.drawable.ic_logo)
     
                 return NotificationCompat.Builder(applicationContext,
                         "VpnNotificationChannel")
                         .setSmallIcon(R.drawable.ic_app_notification)
                         .setLargeIcon(bitmapIconLarge)
                         .setContentTitle("VPN Revoked")
                         .setContentText("The system revoked the VPN permission")
             }    
      
    
    val vpnConnectionConfiguration: VpnConnectionConfiguration
        get() {
            // Create your own routine to saved and retrieve user credentials
            val (_, _, _, _, _, vpnAuthUsername, vpnAuthPassword) = 
                    MyApplication.vpnSdk!!.getAuthInfo()
            
            val username = vpnAuthUsername ?: PreferencesUtil.getUsername()
            val password = vpnAuthPassword ?: PreferencesUtil.getPassword()
            
            return VpnConnectionConfiguration.Builder(username,password)
                .scrambleOn(false)
                .reconnectOn(true)
                .port(VpnPortOptions.PORT_443)
                .vpnProtocol(VpnProtocolOptions.PROTOCOL_UDP)
                .connectionProtocol(VpnConnectionProtocolOptions.OPENVPN)
                .debugLevel(if (BuildConfig.DEBUG) 5 else 0)
                .build()
        }
    
    val pendingOpenAppIntent: PendingIntent
        get() {
            val intentOpenApp = Intent(applicationContext, MainActivity::class.java)
            intentOpenApp.action = Intent.ACTION_MAIN
            intentOpenApp.addCategory(Intent.CATEGORY_LAUNCHER)
            return PendingIntent.getActivity(applicationContext, 0,
                    intentOpenApp, FLAG_UPDATE_CURRENT)
        }
    
    val pendingDisconnectIntent: PendingIntent
        get() {
            val intentDisconnect = Intent(
                    applicationContext, VpnConnectionReceiver::class.java)
            intentDisconnect.action = VpnConnectionReceiver.ACTION_DISCONNECT
            return PendingIntent.getBroadcast(applicationContext, 0,
                    intentDisconnect, FLAG_UPDATE_CURRENT)
        }
        
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == RESULT_OK && requestCode == VPN_PREPARE) {
                startVpnConnection()
            } else {
                //Canceled
            }
        }
    
    override fun onClick(view: View) {
        // Create your own routine to saved and retrieve a selected pop
        val vpnPop = PreferencesUtil.getSelectedVpnPop()

        try {
            if (MyApplication.vpnSdk?.isVpnServicePrepared()) {

                val notification = VpnNotification(
                        baseConnectionNotification.build(), NOTIFICATION_ID_VPN_STATUS)
                        
                val notificationVpnRevoked = VpnNotification(
                        vpnRevokedNotificationBuilder.build(), NOTIFICATION_ID_VPN_REVOKED)

                MyApplication.vpnSdk?.connect(
                        vpnPop,
                        notification,
                        notificationVpnRevoked,
                        vpnConnectionConfiguration
                )?.subscribe({
                    // Check the current connection information
                    val connectionInfo = MyApplication.vpnSdk!!.getConnectionInfo()

                    Timber.i("Server IP Address: ${connectionInfo.ipAddress}")
                }, { throwable ->
                    // Handle any error on VPN connect failure
                })

            } else {
                MyApplication.vpnSdk?.prepareVpnService(this)
            }
        } catch (ex: ActivityNotFoundException) {
            ex.printStackTrace()
            // Handle here any error if was impossible to execute prepare vpn
        }
    }
    
    class VpnConnectionReceiver : BroadcastReceiver() {
        
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent != null && intent.action != null) {
                when (intent.action) {
                    ACTION_DISCONNECT -> disconnect(context)
                }
            }
        }
        
        private fun disconnect(context: Context) {
            
            val notificationManager = context
                    .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            notificationManager.cancel("VpnNotifications", MainActivity.NOTIFICATION_ID)
            
            MyApplication.vpnSdk?.disconnect()
                    ?.subscribe({ aBoolean ->
                        // Disconnected from Receiver
                    }) { throwable ->
                        // Failed to disconnect
                    }
        }
        
        companion object {
            const val ACTION_DISCONNECT = "com.myapp.action.DISCONNECT"
        }
    }
    
    companion object {
        const val NOTIFICATION_ID_VPN_STATUS = 1
        const val NOTIFICATION_ID_VPN_REVOKED = 2
    }
}
```



[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-notification/index.html
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-connection-configuration/index.html
[3]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-pop/index.html
[4]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-server/index.html
[5]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-auth-info/index.html
[6]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-connection-info/index.html
[7]: IPGEO.md
[8]: javadoc/sdk/com.gentlebreeze.vpn.sdk/-i-vpn-sdk/index.html
[9]: LISTENER.md