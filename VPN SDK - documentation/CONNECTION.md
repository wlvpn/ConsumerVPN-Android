# VPN Connection

Connection API is the base for our VPN application. 
In general, it helps to create and secure network tunneling connection 
in your VPN application.

## `AuthInfo`

Class: [com.gentlebreeze.vpn.sdk.model.AuthInfo][5]

Model class that holds information pertaining to a user's authentication

- **`accessExpireEpoch`**: `long` value that holds expire epoch date
- **`accessToken`**: `String` value that contains user access token
- **`accountUpdatedAt`**: `long` value that contains last date where the account was updated
- **`refreshToken`**: `String` value that contains the refresh token
- **`subEndEpoch`**: `long` date value whe sub epoch ends
- **`vpnAuthPassword`**: `String` value that contains the authorization password to realize calls
- **`vpnAuthUsername`**: `String` value that contains the authorization user to realize calls

## Prepare related Methods

Prepare is a required operation by Android to request permission from the user
to allow our app to connect to VPN networks.

1. `void prepareVpnService(context)`
    - Our Sdk Method to prepare our app. The execution of this method and the user
      authorization is required to operate VPN connections in our app
        - **`context`**: `Context` activity context to process permission in an activity
    - Responds in `onActivityResult()` 
        - where  resultCode == `RESULT_OK` and requestCode == `VPN_PREPARE` is success
        
2. `boolean isVpnServicePrepared()`
    - Will tell us if the user already gave permission to our app to use VPN networks

## Connection related methods

**Related Classes**

Class: [com.gentlebreeze.vpn.sdk.model.vpnNotification][1]

Class: [com.gentlebreeze.vpn.sdk.model.VpnConnectionConfiguration][2]

Class: [com.gentlebreeze.vpn.sdk.model.VpnPop][3]

Class: [com.gentlebreeze.vpn.sdk.model.VpnServer][4]

1. `ICallback<Boolean> connect(vpnNotification, vpnRevokedNotification, vpnConnectionConfiguration)`
    - Simple vpn connection with a notification tile. Requires previous call of `fetchGeoInfo` 
    to work as a `Best Location` connection. See [IP Geolocation - fetchGeoInfo][6]
        - **`vpnNotification`**: `VpnNotification` Notification model builder helper to attached a persistent notification
        - **`vpnRevokedNotification`**: `VpnNotification` Notification model builder to show a notification when the system revokes the permission for the VPN to run
        - **`vpnConnectionConfiguration`**: `VpnConnectionConfiguration` is a helper model object to set the desire configuration to run our vpn
2. `ICallback<Boolean> connect(countryCode, vpnNotification, vpnRevokedNotification, vpnConnectionConfiguration)`
    - Connects to a VPN using the desire unique country code to restrict into a country location
        - **`countryCode`**: The unique identifier code for countries
        - **`vpnNotification`**: `VpnNotification` Notification model builder helper to attached a persistent notification
        - **`vpnRevokedNotification`**: `VpnNotification` Notification model builder to show a notification when the system revokes the permission for the VPN to run
        - **`vpnConnectionConfiguration`**: `VpnConnectionConfiguration` is a helper model object to set the desire configuration to run our vpn                                 
3. `ICallback<Boolean> connect(vpnPop, vpnNotification, vpnRevokedNotification, vpnConnectionConfiguration)`
    - Connects to a VPN restricted to a desire city location using a VPN Pop
        - **`vpnPop`**: The desire city VPN Pop to make our connection
        - **`vpnNotification`**: `VpnNotification` Notification model builder helper to attached a persistent notification
        - **`vpnRevokedNotification`**: `VpnNotification` Notification model builder to show a notification when the system revokes the permission for the VPN to run
        - **`vpnConnectionConfiguration`**: `VpnConnectionConfiguration` is a helper model object 
4. `ICallback<Boolean> connect(vpnServer, vpnNotification, vpnRevokedNotification, vpnConnectionConfiguration)`
    - Connects to a VPN restricted to an specific server
        - **`vpnServer`**: The desire VPN Server to make our connection
        - **`vpnNotification`**: `VpnNotification` Notification model builder helper to attached a persistent notification
        - **`vpnRevokedNotification`**: `VpnNotification` Notification model builder to show a notification when the system revokes the permission for the VPN to run
        - **`vpnConnectionConfiguration`**: `VpnConnectionConfiguration` is a helper model object 
 5. `ICallback<Boolean> disconnect()`
    - Will disconnect a current vpn connection
 
 All method callbacks will execute:
 - **`onSuccess`** responds with a success `boolean`.
 - **`onError`** throws a `Throwable` object.

### Examples

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

#### Java Example
        
```java
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {
    
    // Setup yor activity controls and interactions
    
    public final static int NOTIFICATION_ID_VPN_STATUS = 1;
    public final static int NOTIFICATION_ID_VPN_REVOKED = 2;
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (resultCode == RESULT_OK && requestCode == VPN_PREPARE) {
            startVpnConnection();
        } else {
            //Canceled
        }
    }
    
    @Override
    public void onClick(View view) {
        // Create your own routine to saved and retrieve a selected pop
        VpnPop vpnPop = PreferencesUtil.getSelectedVpnPop();
        
        try {
            if (MyApplication.getVpnSdk().isVpnServicePrepared()) {

                VpnNotification notificationVpnStatus = new VpnNotification(
                        getBaseConnectionNotification().build(), NOTIFICATION_ID_VPN_STATUS);
                
                VpnNotification notificationVpnRevoked = new VpnNotification(
                        getVpnRevokedNotification().build(), NOTIFICATION_ID_VPN_REVOKED);

                MyApplication.getVpnSdk().connect(
                        vpnPop,
                        notificationVpnStatus,
                        notificationVpnRevoked,
                        getVpnConnectionConfiguration()
                ).subscribe(null, throwable -> {
                    // Handle any error on vpn connect failure
                    return Unit.INSTANCE;
                });

            } else {
                MyApplication.getVpnSdk().prepareVpnService(this);
            }
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
            // Handle here any error if was impossible to execute prepare vpn 
        }
    }
    
    public NotificationCompat.Builder getBaseConnectionNotification() {
        
        Bitmap bitmapIconLarge = BitmapFactory.decodeResource(
                getApplicationContext().getResources(), R.drawable.ic_logo);
        
        return new NotificationCompat.Builder(getApplicationContext(),
                "VpnNotificationChannel")
                .setLocalOnly(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_app_notification)
                .setLargeIcon(bitmapIconLarge)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setContentIntent(getPendingOpenAppIntent())
                .setUsesChronometer(true)
                .setShowWhen(true)
                .addAction(0, "Disconnect", getPendingDisconnectIntent());
    }
    
     public NotificationCompat.Builder getVpnRevokedNotification() {
            
            Bitmap bitmapIconLarge = BitmapFactory.decodeResource(
                    getApplicationContext().getResources(), R.drawable.ic_logo);
            
            return new NotificationCompat.Builder(getApplicationContext(),
                    "VpnNotificationChannel")
                    .setOngoing(false)
                    .setSmallIcon(R.drawable.ic_app_notification)
                    .setLargeIcon(bitmapIconLarge)
                    .setContentTitle("VPN Revoked")
                    .setContentText("The system revoked the VPN permission");
        }
    
    private VpnConnectionConfiguration getVpnConnectionConfiguration() {
        VpnAuthInfo vpnAuthInfo = MyApplication.getVpnSdk().getAuthInfo();
        
        // Create your own routine to saved and retrieve user credentials
        String username = vpnAuthInfo.getVpnAuthUsername() != null ?
                vpnAuthInfo.getVpnAuthUsername() :
                PreferencesUtil.getUsername();
        String password = vpnAuthInfo.getVpnAuthPassword() != null ?
                vpnAuthInfo.getVpnAuthPassword() :
                PreferencesUtil.getPassword();
        
        return new VpnConnectionConfiguration(
                username,
                password,
                false, // Scramble
                true, // Auto Reconnect
                VpnPortOptions.PORT_443,
                VpnProtocolOptions.PROTOCOL_UDP,
                VpnConnectionProtocolOptions.OPENVPN,
                BuildConfig.DEBUG ? 5 : 0);
    }
    
    private PendingIntent getPendingOpenAppIntent() {
        final Intent intentOpenApp = new Intent(getApplicationContext(), MainActivity.class);
        intentOpenApp.setAction(Intent.ACTION_MAIN);
        intentOpenApp.addCategory(Intent.CATEGORY_LAUNCHER);
        return PendingIntent.getActivity(getApplicationContext(), 0,
                intentOpenApp, FLAG_UPDATE_CURRENT);
    }
    
    private PendingIntent getPendingDisconnectIntent() {
        final Intent intentDisconnect = new Intent(
                getApplicationContext(), VpnConnectionReceiver.class);
        intentDisconnect.setAction(VpnConnectionReceiver.ACTION_DISCONNECT);
        return PendingIntent.getBroadcast(getApplicationContext(), 0,
                intentDisconnect, FLAG_UPDATE_CURRENT);
    }
    
    public static class VpnConnectionReceiver extends BroadcastReceiver {
        
        public static final String ACTION_DISCONNECT = "com.myapp.action.DISCONNECT";
        
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction() != null) {
                switch (intent.getAction()) {
                    case ACTION_DISCONNECT:
                        disconnect(context);
                        break;
                }
            }
        }
        
        private void disconnect(Context context) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.cancel("VpnNotifications", MainActivity.NOTIFICATION_ID);

            MyApplication.getVpnSdk().disconnect()
                    .subscribe(aBoolean -> {
                        // Disconnected from Receiver
                        return Unit.INSTANCE;
                    }, throwable -> {
                        // Failed to disconnect
                        return Unit.INSTANCE;
                    });
        }
    }
}
```

#### Kotlin Example

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
            
            return VpnConnectionConfiguration(
                    username,
                    password,
                    false, // Scramble
                    true, // Auto Protect
                    VpnPortOptions.PORT_443,
                    VpnProtocolOptions.PROTOCOL_UDP,
                    VpnConnectionProtocolOptions.OPENVPN, 
                    if (BuildConfig.DEBUG) 5 else 0)
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
            if (MyApplication.vpnSdk!!.isVpnServicePrepared()) {

                val notification = VpnNotification(
                        baseConnectionNotification.build(), NOTIFICATION_ID_VPN_STATUS)
                        
                val notificationVpnRevoked = VpnNotification(
                        vpnRevokedNotificationBuilder.build(), NOTIFICATION_ID_VPN_REVOKED)

                MyApplication.vpnSdk!!.connect(
                        vpnPop,
                        notification,
                        notificationVpnRevoked,
                        vpnConnectionConfiguration
                ).subscribe(null, { throwable ->
                    // Handle any error on vpn connect failure
                    Unit
                })

            } else {
                MyApplication.vpnSdk!!.prepareVpnService(this)
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
            
            MyApplication.vpnSdk!!.disconnect()
                    .subscribe({ aBoolean ->
                        // Disconnected from Receiver
                        Unit
                    }) { throwable ->
                        // Failed to disconnect
                        Unit
                    }
        }
        
        companion object {
            val ACTION_DISCONNECT = "com.myapp.action.DISCONNECT"
        }
    }
    
    companion object {
        val NOTIFICATION_ID_VPN_STATUS = 1
        val NOTIFICATION_ID_VPN_REVOKED = 2
    }
}
```

[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-notification/index.html
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-connection-configuration/index.html
[3]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-pop/index.html
[4]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-server/index.html
[5]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-auth-info/index.html
[6]: IPGEO.md