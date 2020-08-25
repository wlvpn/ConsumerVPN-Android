# Setup

The VPN-SDK is a Kotlin first library. Although it can be used with Java
with no problem, we recommend using Kotlin for the best experience.

Before you begin using VPN-SDK, you must setup your project following the next instructions

## Requirements

- Java 8 (Optional)
- Local Gradle (Optional)
- API Token Access
- Package Cloud Access TPken
- Android API level 15+ (4.0.3)
- Android Build Tools 28.0+
- Project Gradle plugin version 3x

### Gradle

Install gradle on your local machine to support global variables in gradle files

For detailed instructions to install gradle on your local machine, 
see Gradle's [Installation Guide][1]. 

### Java 8

To support Java 8 Lambda in your project make sure your gradle file contains Java 8 support

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
}
```

`ACCOUNT_NAME`, `AUTH_SUFFIX`, and `API_KEY` will be given to you and are required
for any API call made in our system. `SdkConfig` also contains a few
other API configuration options that are available for modifying API
endpoints but, in most cases, modifying these are are not necessary. Here is a complete list
of configuration options:

* **accountName (Required)** - String value containing your account name
* **apiKey (Required)** -  String value containing your API key
* **authSuffix (Required)** - String value containing your auth suffix
* **apiTokenRefreshEndpoint (Optional)** - String value endpoint for the API token refresh
* **client (Optional)** - String value designating the client (e.g. Android)
* **apiHost (Optional)** - String value designating the API hostname
* **ipGeoUrl (Optional)** - String value designating the IP Geo API hostname
* **loginApi (Optional)** -  String value endpoint for the API login
* **protocolListApi (Optional)** - String value endpoint for the API protocol list
* **serverListApi - (Optional)** String value endpoint for the API server list
* **logTag - (Optional)** String value to place an identifier on each log entry
* **locale - (Optional)** Locale value that allows searches related to countries in different languages

Note: You can add these values directly to your `MyApplication.java` file

Also in your module-level `build.gradle` file, add this to your `dependencies` block. The
dependency must have the transitive flag to get the libraries passed down from
the maven repo.

```groovy
def vpnSdk = "1.3.16684@aar" // Or latest
dependencies {
    implementation("com.gentlebreeze.vpn.sdk:sdk:$vpnSdk") {
        transitive = true
    }
}
```

### 2. Android Manifest Permissions

The VPN-SDK adds the following required permissions automatically into your final
application manifest:

* **android.permission.ACCESS_NETWORK_STATE**: Allows the SDK to get access 
to information about networks.
* **android.permission.INTERNET**: Allows the SDK to execute API calls 
over the Internet.
* **android.permission.ACCESS_WIFI_STATE**: Allows the SDK to get access 
to information about Wi-Fi networks.
* **android.permission.FOREGROUND_SERVICE**: Allows the SDK to run the 
VPN service in the foreground

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

The final result merged application `manifest` could be seen at
`app/build/intermediates/manifests/full/(debug|release)/AndroidManifest.xml`

There is no need to remove any permission if you now have those set it up into your current 
application `manifest`. The final result will not produce any repeated values.

### 3. Library Initialization

After adding the dependency, you can initialize the VPN-SDK in your application.
The best place to initialize the library is as a static reference in your Application class
but this can also be initialized in an activity if necessary.

In your application file add the following code in the `onCreate` method

For Java:

```java
public class MyApplication extends Application {
    
    private static IVpnSdk vpnSdk;
    
    public static IVpnSdk getVpnSdk() {
        return vpnSdk; 
    }
        
    @Override
    public void onCreate() {
        super.onCreate();
        
        vpnSdk = VpnSdk.init(this, new SdkConfig(
                    BuildConfig.ACCOUNT_NAME,
                    BuildConfig.API_KEY,
                    BuildConfig.AUTH_SUFFIX,
                    BuildConfig.CLIENT,
                    BuildConfig.BASE_HOSTNAME,
                    BuildConfig.IP_GEO,
                    BuildConfig.LOGIN_API,
                    BuildConfig.REFRESH_API,
                    BuildConfig.PROTOCOL_LIST_API,
                    BuildConfig.SERVER_LIST_API
        ));
        
        // OR use the builder
        
        vpnSdk = VpnSdk.init(this, new SdkConfig.Builder(
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
                .logTag(MY_SDK_LOG_TAG)
                .locale(new Locale("en", "US"))
                .build());
    }
}
```

For Kotlin:

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
        
        vpnSdk = VpnSdk.init(this, SdkConfig.Builder(
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
                .logTag(MY_SDK_LOG_TAG)
                .locale(Locale("en", "US"))
                .build())
    }
    
    companion object {
        var vpnSdk: IVpnSdk? = null
    }
}
```


If a dynamic api key is needed you can modify the api key after the
library was initialized.

For Java:

```java
    getVpnSdk().setApiKey("My new api key")
        .subscribe();
```

For Kotlin:

```kotlin
vpnSdk.setApiKey("My new api key")
    .subscribe()
```

If a dynamic Authentication suffix is needed you can modify it
after the library was initialized.

For Java:

```java
getVpnSdk().setAuthSuffix("My new auth suffix")
    .subscribe();
```

For Kotlin:

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

````groovy
packagingOptions {
        exclude "**/libopenvpn.so"
        exclude "**/libovpnexec.so"
        }
````

* IKEv2

````groovy
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

````   

* WireGuard

````groovy
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
```` 

[1]: https://gradle.org/install/
[2]: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
