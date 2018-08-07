# VPN Event Listening

Helper to receive connection events in your application.

## `VpnState`

Class: [com.gentlebreeze.vpn.sdk.model.VpnState] [1]

This object holds information relevant to the current state of the VPN connection.

Properties:

- **`connectionDescription`**: `String` value that contains detailed description of the current connection state
- **`connectionState`**: `int` value that contains current connection state
- **`dataUsageRecord`** (Deprecated): `VpnDataUsage` value object with current connection usage
_(Data usage is now accessible by using the `listenToConnectionData()` callback)_

`Integer` general value constants for connection state information:

- **`CONNECTED`**: _0_
- **`CONNECTING`**: _1_
- **`DISCONNECTED`**: _2_

## `VpnDataUsage`

Class: [com.gentlebreeze.vpn.sdk.model.VpnDataUsage][2]

Model object class that contains information about data usage measured in bytes

- **`downBytes`**: `long` value that represents current connection download bytes
- **`downBytesDiff`**: `long` value that represents current connection download bytes difference
- **`upBytes`**: `long` value that represents current connection uploaded bytes
- **`upBytesDiff`**: `long` value that Represents current connection uploaded bytes difference

## Listening Method

1. `ICallback<VpnState> listenToConnectState()`
    - Is going to listen to VPN connection events. 
    - In callback responds with
        - **`onSuccess`**: `VpnState`.
        - **`onError`**: throws a `Throwable` object

This callback must be `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle method. And is recommended to be initialize in our 
activity `onResume`

### Examples

Listening to events inside one of our activities.

#### Java Example
        
```java
public class MainActivity extends AppCompatActivity {
    
    private ICallback<VpnState> callbackState;
    
    // Setup yor activity controls and interactions
    
    @Override
    public void onResume() {
        super.onResume();
        callbackState = registerVpnStateListener();
    }

    @Override
    public void onPause() {
        super.onPause();
        callbackState.unsubscribe();
    }
    
    private ICallback<VpnState> registerVpnStateListener() {
            return MyApplication.getVpnSdk().listenToConnectState()
                    .subscribe(vpnState -> {
                        int connectionState = vpnState.getConnectionState();
    
                        switch (connectionState) {
                            case VpnState.DISCONNECTED:
                                // Handle any state update in here
                                break;
                            case VpnState.CONNECTED:
                                // Handle any state update in here
                                break;
                            case VpnState.CONNECTING:
                                // Handle any state update in here
                                break;
                        }
                        return Unit.INSTANCE;
                    }, throwable -> {
                        
                         throwable.printStackTrace();
                         // Handle any error in here
                         
                        return null;
                    });
    }
}
```

#### Kotlin Example

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
    
    private fun registerVpnStateListener(): ICallback<VpnState> {
        return MyApplication.vpnSdk!!.listenToConnectState()
                .subscribe({ vpnState ->
                    val connectionState = vpnState.connectionState
                   
                    when (connectionState) {
                    
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
                    
                    Unit
                    
                } { throwable ->
                
                    throwable.printStackTrace()
                    // Handle any error in here
                    
                    null
                })
    }
}
```

## Disclaimer

This is not an official Google product. 

[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-state/index.html
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-data-usage/index.html