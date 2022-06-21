# VPN-SDK for Android

VPN-SDK is a library with all of the necessary components to manage, create and monitor a
VPN connection on Android. 

This library will handle all of the heavy
lifting needed to interact with our API to login, fetch servers, fetch protocols,
generate models, backup data in a generated database, connecting and configuring
the VPN service, and getting connection states.

The VPN-SDK is a Kotlin first library. Although it can be used with Java
with no problem, we recommend using Kotlin for the best experience.

## General Requirements

- Android API level 15+ (4.0.3)
- Android Build Tools 25.0+
- Gradle plugin version 4.0.0

## Quick Start

To start out you will need an API token to access the VPN-SDK package repository.
You can retrieve this from one of the admins of the maven repository.

In your project root level `build.gradle` file add this line to your `allprojects` block:

```groovy
allprojects {
    repositories {
        maven {
            url "https://packagecloud.io/priv/${packagecloud_vpn_token}/cloak/android-vpn-sdk/maven2"
        }
    }
}
```

In your project's `build.gradle` file add this to your dependencies block.

```groovy
dependencies {
    implementation "com.gentlebreeze.vpn.sdk:sdk:${sdkVersion}"
}
```

After adding the dependency, you can initialize the VPN-SDK in your application.

The recommended practice is to use any Dependency Injection (a library or custom implementation)
to inject an instance of the `IVpnSdk` API.

Although it is recommended to use DI, you can easily add a static reference in your Application class
or directly initialize the library in an activity if necessary.

Initialize the library using the `VpnSdk.init(...)` method. It only requires two arguments:

A context and a SdkConfig. The latter can be created through a constructor or an easy to use Builder
(`SdkConfig.Builder`).

Here is a sample of the initialization call:


```kotlin
val vpnSdk = VpnSdk.init(this, SdkConfig(
        accountName,
        apiKey,
        authSufix,
        client,
        apiHost,
        gepApiDomain,
        loginApi,
        refreshApi,
        protocolListApi,
        serverListApi
))
```

For detailed instructions including a Java example and full requirements, 
see VPN-SDK's [Download and setup document][1].

## Implementation

Throughout all of our libraries we use RXJava for all of our handling
of threading, data manipulation and event buses. To simplify this process
for the SDK implementation and to ensure that the user does not have to maintain
different RX versions we created a lightweight wrapper class called `Callback`

For detailed explanation and examples, see VPN-SDK's [Implementation][2]

## Detailed Documentation

For all detailed documentation, see [Javadoc][3]

## Important considerations

### App bundle support

App bundle it's not currently supported by the VPN SDK and there are no plans to support it
in the near future.

App bundle optimizes the application size creating separate files that are dynamically
delivered to the user, this modifies the way the application stores the native libraries used
by all the VPN Protocols. 

Since the compilation process modifies how the native libraries are stored, it creates scenarios
 where the application can't find the native libraries breaking the native linking.

If app bundle is a business requirement, a workaround is to enable a gradle compilation flag
in the  gradle.properties file:

```groovy
android.bundle.enableUncompressedNativeLibs=false
```
 
Note that this compilation flag it's warned as **unsopported**.




## Open source licenses

For details on the Open Source Licenses used by the VPN SDK, see [Licenses][4] 

[1]: SETUP.md
[2]: IMPLEMENTATION.md
[3]: ../docs/javadoc/sdk/index.html
[4]: LICENSES.md
[5]: https://developer.android.com/platform/technology/app-bundle