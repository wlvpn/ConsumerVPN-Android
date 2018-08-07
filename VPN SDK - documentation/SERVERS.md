# Servers

Fetching servers is how your app can obtain specific information about our VPN servers.

## `VpnServer`

Class: [com.gentlebreeze.vpn.sdk.model.VpnServer][1]

Object class that contains information about a certain server inside a server

- **`capacity`**: `int` value containing with server capacity
- **`ipAddress`**: `String` value containing server IP Address
- **`isInMaintenance`**: `boolean` value that reveals if the selected server is in maintenance
- **`name`**: `String` value containing the server name identifier
- **`popName`**: `String` value containing the pop name that houses this server
- **`scheduledMaintenance`**: `long` value that contains the date for any scheduled maintenance
- **`describeContents()`** `int` value that describes the server content 

## `SortServerOption`

Class: [com.gentlebreeze.vpn.sdk.sort.SortServerOption][2]

Enum class that holds options for sorting a pops list

- **`COUNTRY`**: Use this sort enum value to order the list by country 
- **`CITY`**: Use this sort enum value to order the list by city
- **`NAME`**: Use this sort enum value to order the list by pop name

## `SortOrder` (Enum)

Class: [com.gentlebreeze.vpn.sdk.sort.SortOrder][3]

Enum class that holds options for ordering alphabetically a servers list

- **`ASC`**: Use this sort enum value to show ascending alphabetically order
- **`DESC`**: Use this sort enum value to descending alphabetically order

## `SortServer`

Class: [com.gentlebreeze.vpn.sdk.sort.SortServer][4]

Sort server is a helper model to define a sorting behaviour for fetching methods

### Constructor

To Initialize use `SortServerOption` and `SortOrder` values

## Server List Fetching Related Methods

1. `ICallback<List<VpnServer>> fetchAllServers()`
    - This method will obtain all available severs
2. `ICallback<List<VpnServer>> fetchAllServers(sortServer)`
    - This method will obtain all available servers sorted and ordered
        - **`sortServer`**: `SortServer` object model to define how the list will be sorted and ordered
3. `ICallback<List<VpnServer>> fetchAllServersByPop(pop)`
    - This method will obtain all available servers from a certain pop
        - **`pop`**: `VpnPop` model object to filter it's servers
4. `ICallback<List<VpnServer>> fetchAllServersByPop(pop, sortServer)`
    - This method will obtain all available servers from a certain pop sorted and ordered 
        - **`pop`**: `VpnPop` model object to filter it's servers
        - **`sortServer`**: `SortServer` object model to define how the list will be sorted and ordered

All callbacks returning will return:
- **`OnNext`**: A List of `VpnServer`
- **`OnError`**: throws a `Throwable` object

All callbacks must be `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle method

## Single Server Fetching Related Methods

1. `ICallback<VpnServer> fetchServerByName(name)`
    - This method will get a server by it's unique name
        - **`name`**: `String` value containing the name of the server you are trying to fetch
        
        
All callbacks must be `unsubscribe` during the activity `onDestroy` or 
`onPause` lifecycle method

### Examples

### Java
        
```java
public class ServerListActivity extends AppCompatActivity {
    
    private ICallback<List<VpnServer>> vpnServerCallback;
    
    // Setup yor activity controls and interactions
    
    @Override
    public void onDestroy() {
        if (vpnServerCallback != null) {
            vpnServerCallback.unsubscribe();
        }
        
        super.onDestroy();
    }
    
    public ICallback<List<VpnServer>> getVpnPopCallback() {
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

#### Kotlin

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

[1]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-server/index.html
[2]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-server-option/index.html
[3]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-order/index.html
[4]: ../docs/javadoc/sdk/com.gentlebreeze.vpn.sdk.sort/-sort-server/index.html