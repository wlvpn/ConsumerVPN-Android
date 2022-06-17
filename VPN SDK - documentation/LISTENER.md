# VPN State Listening

The mechanism to receive connection events in your application.

## `VpnState`

Class: [com.gentlebreeze.vpn.sdk.model.VpnState] [1]

Model with information relevant to the current state of the VPN connection.

Properties:

- **`connectionDescription`**: `String` The detailed description of the current connection state.
- **`connectionState`**: `int` The current connection state.
- **`dataUsageRecord`** (Deprecated): `VpnDataUsage` value object with current connection usage
_(Data usage is now accessible by using the `listenToConnectionData()` callback)_

`Integer` general value constants for connection state information:

- **`CONNECTED`**: _0_
- **`CONNECTING`**: _1_
- **`DISCONNECTED`**: _2_
- **`DISCONNECTED_ERROR`**: _3_

## `VpnDataUsage`

Class: [com.gentlebreeze.vpn.sdk.model.VpnDataUsage][2]

Model that contains information about data usage measured in bytes.

- **`downBytes`**: `long` The current downloaded bytes.
- **`downBytesDiff`**: `long` The current downloaded bytes difference. 
- **`upBytes`**: `long` The current uploaded bytes.
- **`upBytesDiff`**: `long` The current uploaded bytes difference

## Listening Mechanisms

---

### `listenToConnectState()`

```kotlin
fun listenToConnectState(): ICallback<VpnState>
```
Subscribes to VPN connection event changes. 

The returned callback will respond with either of these two:

- **`onSuccess`**: The `VpnState`.
- **`onError`**: throws a `Throwable` object

### `listenToCentralizedConnectionState(...)`

```kotlin
fun listenToCentralizedConnectionState(
        /** The listener implementation to receive connection states changes **/
        @NonNull vpnStateConnectionCallback: VpnStateConnectionCallback
    )
```
This method will allow us to subscribe to the VPN state changes with a centralized listener class.
For example, you can update Widgets, Quick Settings Tiles, Notifications, and Other Services.


Don't forget to call  `Callback.unsubscribe()`  in the activity's `onDestroy` or
`onPause` lifecycle methods.

### Examples

1. Listening to events inside one of our activities.

```kotlin
class MainActivity : AppCompatActivity() {
    var callbackState: ICallback<VpnState>? = null

    // Setup yor activity controls and interactions

    override fun onResume() {
        super.onResume()
        callbackState = registerVpnStateListener()
    }

    override fun onPause() {
        super.onPause()
        callbackState!!.unsubscribe()
    }

    private fun registerVpnStateListener(): ICallback<VpnState>? =
         MyApplication.vpnSdk?.listenToConnectState()
                ?.subscribe({ vpnState ->
                    when (vpnState.connectionState) {
                    
                        VpnState.DISCONNECTED_ERROR -> {
                            // Handle any state update in here
                        }

                        VpnState.DISCONNECTED -> {
                            // Handle any state update in here
                        }

                        VpnState.CONNECTED -> {
                            // Handle any state update in here
                        }

                        VpnState.CONNECTING -> {
                            // Handle any state update in here
                        }
                        
                    }

                } { throwable ->

                    throwable.printStackTrace()
                    // Handle
                })
    
}
```


2. Listening a single centralized event to update an internal widget and a quick settings tile.

```kotlin
    class MyApplication : Application() {

        companion object {

                var vpnSdk: IVpnSdk? = null
                    private set

                fun initVpnSdk(context: Application) {

                        vpnSdk = VpnSdk.init(......);

                        // Set Connect State Callback to full support Widget and
                        // Quick Tile Notifications notifications
                        vpnSdk?.setConnectStateCallback(
                            object : VpnStateConnectionCallback() {

                                override fun onStateChange(state: Int) {

                                    // Widget Notification
                                    val intent = Intent(context, MyWidgetProvider::class.java)
                                            intent.action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                                            context.sendBroadcast(intent)

                                    // Quick Settings Tile Notification
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        TileService.requestListeningState(
                                                context,
                                                ComponentName(context, MyQuickSettingsTile::class.java)
                                        )
                                    }
                                }

                        })

                    }
                }
            }
        }

        // ..... My Application class implementation
    }
```

## Disclaimer

This is not an official Google product. 

[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-state/index.html
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-data-usage/index.html