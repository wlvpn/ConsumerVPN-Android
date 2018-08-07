# VPN-SDK for Android

VPN-SDK is an implementation library containing all of the libraries needed
to create a VPN application on Android. This library will handle all of the heavy
lifting needed to interact with our API to login, fetch servers, fetch protocols,
generate models, backup data in a generated database, connecting and configuring
the VPN service, and getting connection states.

The VPN-SDK is a Kotlin first library. Although it can be used with Java
with no problem, we recommend using Kotlin for the best experience.

## General Requirements

- Android API level 15+ (4.0.3)
- Android Build Tools 25.0+
- Gradle plugin version 3x

## Quick Start

To start out you will need an API token to access the VPN-SDK package repository.
You can retrieve this from one of the admins of the maven repository.

In your project root level build.gradle file add this line to your `allprojects` block:

```groovy
allprojects {
    repositories {
        maven {
            url "https://packagecloud.io/priv/${packagecloud_vpn_token}/cloak/android-vpn-sdk/maven2"
        }
    }
}
```

In your project's `build.gradle` file add this to your dependencies block. The
dependency must have the transitive flag to get the libraries passed down from
the maven repo.

```groovy
dependencies {
    implementation ("com.gentlebreeze.vpn.sdk:sdk:${sdkVersion}") {
        transitive true
    }
}
```

After adding the dependency you can initialize the VPN-SDK in your application.
The best place to initialize the library is as a static reference in your Application class
but this can also be initialized in an activity if necessary. Initialize takes two
parameters, application context and the SDK Configuration. Initialization
looks something like this:

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

## Detailed Documention

For all detailed documentation, see [Javadoc][3]

## Disclaimer

This is not an official Google product. 

[1]: SETUP.md
[2]: IMPLEMENTATION.md
[3]: ../docs/javadoc/sdk/index.html