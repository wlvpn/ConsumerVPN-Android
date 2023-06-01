# Split Tunneling

Split tunneling. Allows the client to configure apps, domains, or IP addresses to be excluded from 
the VPN tunnel.

Base for this feature is 2 parameters that are part of the VpnConfiguration class 
(Refer to [CONNECTION.md][1] for more details on that class) 

## `Split Tunneling by apps`

Split tunneling by apps need a param named **`splitTunnelApps`**:`List<String>` containing the package 
names of all the apps that we want to skip the VPN Tunnel and this list should be provided as an 
argument in the [VpnConnectionConfiguration][2] class

```kotlin
  val splitTunnelWhitelist = listOf(
    "com.example.app1",
    "com.example.app2",
    "com.example.app3",
  )
  
  VpnConnectionConfiguration
    .Builder(credentials.username, credentials.password)
    .splitTunnelApps(splitTunnelWhitelist)
    .build()
```

### `Considerations`
- All inputs received will be considered valid and no validation would be done on the SDK side.
- Split Tunneling for apps is supported by versions of `Android 5.1+`


## `Split Tunneling by domains`

Split tunneling by domains need a param named **`domains`**:`List<String>` list of all the **domains**
or **IP's (IPv4 or IPv6)** skip the VPN Tunnel and this list should be provided as an argument in 
the [VpnConnectionConfiguration][2] class

```kotlin
  val domainsWhitelist = listOf(
    "www.example.url.com",
    "10.0.0.1",
    "2345:0425:2CA1:0000:0000:0567:5673:23b5",
  )
  
  VpnConnectionConfiguration
    .Builder(credentials.username, credentials.password)
    .domains(domainsWhitelist)
    .build()
```

### `Considerations`
- All inputs received will be considered valid and no validation would be done on the SDK side.
- Split Tunneling for domains is supported by versions of `Android 13+`


[1]: CONNECTION.md
[2]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-connection-configuration/index.html