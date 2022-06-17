# Setup

The VPN-SDK is a Kotlin first library. Although it's compatible with Java
with no problem, we recommend using Kotlin for the best experience.

Before you begin using VPN-SDK, you must set up your project following the next instructions.

## Requirements

- Java 8 (Optional)
- Local Gradle (Optional)
- API Token Access
- Package Cloud Access TPken
- Android API level 15+ (4.0.3)
- Android Build Tools 28.0+
- Project Gradle plugin version 4.0.0

### Gradle

Install gradle on your local machine to support global variables in gradle files.

For detailed instructions to install gradle on your local machine, 
see Gradle's [Installation Guide][1]. 

### Java 8

To support Java's 8 Lambda in your project make sure your gradle file contains Java 8 support:

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}
```

To download and install Java 8, see Java's [JDK 8 Downloads][2].

### Package Cloud access token 

To start out, you will need an Package Cloud key token to access the VPN-SDK package repository.
You can retrieve this from one of the admins of the maven repository. 

#### Add the global API token in your gradle.properties file

Once you retrieve this API token you can add this to your Gradle home `gradle.properties` file.
- For MacOS this can usually be found at `~/.gradle/gradle.properties`.
- For Windows this can be found in your installation folder which
is usually `C:\Gradle`

You will need to add this line in the file:

```properties
packagecloud_vpn_token=MY_API_TOKEN
```

Another option is to add the line in your project `local.properties` file or create
one with the value. This will only work in the current project.

## Getting Started

This guide will walk you through all needed steps to start using the VPN-SDK 
library in your app.

### 1. Update the gradle configuration

To use the VPN-SDK, you will need to add the following to your `build.gradle`
files to retrieve our SDK package from our maven repository.

In your project root level `build.gradle` file, add this line to your `allprojects` block:

```groovy
allprojects {
    repositories {
        maven {
            url "https://packagecloud.io/priv/${packagecloud_vpn_token}/cloak/android-vpn-sdk/maven2"
        }
    }
}
```

Edit your module-level `build.gradle` file and add your configuration values:

```groovy
defaultConfig {
    buildConfigField 'String', 'ACCOUNT_NAME', '"my_app_account"'
    buildConfigField 'String', 'AUTH_SUFFIX', '""'
    buildConfigField 'String', 'API_KEY', '"my_api_key"'
    buildConfigField 'String', 'CLIENT', "\"Android-${versionName}b${versionCode}\""
    buildConfigField 'String', 'BASE_HOSTNAME', '"https://api.colomovers.com/wlapi/%s"'
    buildConfigField 'String', 'PROTOCOL_LIST_API', '"protocols"'
    buildConfigField 'String', 'LOGIN_API', '"login"'
    buildConfigField 'String', 'REFRESH_API', '"refresh"'
    buildConfigField 'String', 'SERVER_LIST_API', '"servers"'
    buildConfigField 'String', 'IP_GEO', '"https://myipgeoserver.com/v2?apikey=my_api_geo_server_key"'
    buildConfigField 'String', 'SDK_LOG_TAG', '"VPN_SDK"'
    buildConfigField 'String', 'IKE_REMOTE_ID', '"strongswan_remote_id"'
    buildConfigField 'String', 'WIREGUARD_ENDPOINT', '"wg_endpoint"'
    buildConfigField 'String', 'CREATE_ACCOUNT_ENDPOINT', '"create_account_endpoint"'
}
```

`ACCOUNT_NAME`, `AUTH_SUFFIX`, and `API_KEY` will be given to you and are required
for any API call made in our system. `SdkConfig` also contains a few
other API configuration options that are available for modifying API
endpoints but, in most cases, modifying these are are not necessary. Here is a complete list
of configuration options:

* `accountName` **(Required)** -  Your account name.
* `apiKey` **(Required)** -   Your API key.
* `authSuffix` **(Required)** -  Your auth suffix.
* `apiTokenRefreshEndpoint` **(Optional)** -  Endpoint for the API token refresh.
* `client` **(Optional)** -  The client type (e.g. Android).
* `apiHost` **(Optional)** -  The API hostname.
* `ipGeoUrl` **(Optional)** -  The IP Geo API hostname.
* `loginApi` **(Optional)** -   Endpoint for the login API.
* `protocolListApi` **(Optional)** -  Endpoint for the protocol list API.
* `serverListApi` - **(Optional)** -  Endpoint for the server list API.
* `version` - **(Optional)** - A version identifier used in the user agent header of the requests.
* `sdkLogTag` - **(Optional)** - An identifier to use on each log entry.
* `ikeRemoteId` - **(Optional)** - The remote id used in the StrongSwan protocol.
* `wireguardEndpoint` - **(Optional)** - Endpoint used by WireGuard protocol.
* `createAccountEndpoint` - **(Optional)** - Endpoint used to create accounts.

>**Note**: You can add these values directly to your `MyApplication.java` file

Also, in your module-level `build.gradle` file, add this to your `dependencies` block.

```groovy
def vpnSdk = "1.5.13.0" // Or latest
dependencies {
    implementation "com.gentlebreeze.vpn.sdk:sdk:$vpnSdk"
}
```

### 2. Android Manifest Permissions

The VPN-SDK adds the following required permissions automatically into your final
application manifest:

* `android.permission.ACCESS_NETWORK_STATE`: Allows the SDK to get access 
to information about networks.
* `android.permission.INTERNET`: Allows the SDK to execute API calls 
over the Internet.
* `android.permission.ACCESS_WIFI_STATE`: Allows the SDK to get access 
to information about Wi-Fi networks.
* `android.permission.FOREGROUND_SERVICE`: Allows the SDK to run the 
VPN service in the foreground.

```xml
<manifest
    xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.myapp">

    <uses-permission
        android:name="android.permission.ACCESS_NETWORK_STATE" />

    <uses-permission
        android:name="android.permission.INTERNET" />

    <uses-permission
        android:name="android.permission.ACCESS_WIFI_STATE" />
        
    <uses-permission 
        android:name="android.permission.FOREGROUND_SERVICE" />
        
    ....

    <application ...>
        ......
    </application>
</manifest>
``` 

You can find the merged `manifest` of your application at:
> app/build/intermediates/manifests/full/(debug|release)/AndroidManifest.xml

There is no need to remove any permission if you now have those set up into your current 
application `manifest`. The final result will not produce any repeated values.

### 3. Library Initialization

After adding the dependency, you can initialize the VPN-SDK in your application.

The recommended practice is to use any Dependency Injection (a library or custom implementation)
to inject an instance of the `IVpnSdk` API.

Although it is recommended to use DI, you can easily add a static reference in your Application class
or directly initialize the library in an activity if necessary.

Let's initialize the library the Application class:


```kotlin
private const val MY_SDK_LOG_TAG = "VPN_SDK"

class MyApplication : Application() {
    
    override fun onCreate() {
    
        super.onCreate()
        
        vpnSdk = VpnSdk.init(this, SdkConfig(
                BuildConfig.ACCOUNT_NAME,
                BuildConfig.API_KEY,
                BuildConfig.AUTH_SUFFIX,
                BuildConfig.CLIENT,
                BuildConfig.BASE_HOSTNAME,
                BuildConfig.IP_GEO,
                BuildConfig.LOGIN_API,
                BuildConfig.REFRESH_API,
                BuildConfig.PROTOCOL_LIST_API,
                BuildConfig.SERVER_LIST_API,
                MY_SDK_LOG_TAG,
                Locale("en", "US")
        ))
        
        // Or use the builder
        
        vpnSdk = VpnSdk.init(
            this,
            Builder(BuildConfig.ACCOUNT_NAME, BuildConfig.API_KEY, BuildConfig.AUTH_SUFFIX)
                .client(BuildConfig.CLIENT)
                .version(BuildConfig.VERSION_NAME)
                .apiHost(BuildConfig.HOSTNAME)
                .apiHostMirrorConfiguration(
                    mirrorsConfiguration
                )
                .ipGeoUrl(BuildConfig.IP_GEO)
                .apiLoginEndpoint(BuildConfig.LOGIN_API)
                .apiTokenRefreshEndpoint(BuildConfig.REFRESH_API)
                .apiWireGuardConnectionEndpoint(BuildConfig.API_WIREGUARD_CONNECTION_HOSTNAME)
                .apiWireGuardMirrorsConfiguration(
                    wireGuardmirrorsConfiguration
                )
                .apiProtocolListEndpoint(BuildConfig.PROTOCOL_LIST_API)
                .apiServerListEndpoint(BuildConfig.SERVER_LIST_API)
                .accountCreationConfiguration(
                    AccountCreationConfiguration(
                        CreateAccountKeyUtil().getAccountCreationKey(),
                        BuildConfig.CREATE_ACCOUNT_API
                    )
                ).logTag(BuildConfig.SDK_LOG_TAG)
                .build()
        )
    }
    
    companion object {
        var vpnSdk: IVpnSdk? = null
    }
}
```


If a dynamic api key is needed you can modify the api key after the
library was initialized.


```kotlin
vpnSdk.setApiKey("My new api key")
    .subscribe()
```

If a dynamic Authentication suffix is needed you can modify it
after the library was initialized.


```kotlin
vpnSdk.setAuthSuffix("My new auth suffix")
    .subscribe()
```

### 4. Removing VPN protocol binaries
If a VPN protocol is not being supported, it is recommended to remove the protocol's binaries 
to make the final .APK file smaller.

To remove a VPN protocol binary is necessary to use DSL Gradle object `packagingOptions` 
in your application's Gradle file. 
 
* OpenVPN

```groovy
packagingOptions {
        exclude "**/libopenvpn.so"
        exclude "**/libovpnexec.so"
        }
```

* IKEv2

```groovy
packagingOptions {
         //Removes StrongSwan-IKE libraries
                exclude "**/libcharon.so"
                exclude "**/libimcv.so"
                exclude "**/libipsec.so"
                exclude "**/libstrongswan.so"
                exclude "**/libtnccs.so"
                exclude "**/libtncif.so"
                exclude "**/libtpmtss.so"
        }

```   

* WireGuard

```groovy
packagingOptions {
         //Removes StrongSwan-IKE libraries
                exclude "**/libcharon.so"
                exclude "**/libimcv.so"
                exclude "**/libipsec.so"
                exclude "**/libstrongswan.so"
                exclude "**/libtnccs.so"
                exclude "**/libtncif.so"
                exclude "**/libtpmtss.so"
        }
``` 

[1]: https://gradle.org/install/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
