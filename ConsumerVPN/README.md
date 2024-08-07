# ConsumerVPN

ConsumerVPN is a White Label VPN Application intended to make it easy to create
new VPN applications for Android platforms.

## Table of Contents

1. [Before Starting](#before-starting)
2. [Getting Started](#getting-started)
    
    i. [Prerequisites](#prerequisites)

    ii. [OS Requirements](#os-requirements)

    iii. [Package Management Tool](#package-management-tool)

    iv. [VPN SDK Keys Setup](#vpn-sdk-keys-setup)

    v. [Customizable URLs](#customizable-urls)
3. [Building The App](#building-the-app)

    i. [Running The Sample App](#running-the-sample-app)
4. [Theming Your Solution](#theming-your-solution)
    
    i. [App Colors](#app-colors)
    
    ii. [App Icons](#app-icons)

    iii. [External Theming Resources](#external-theming-resources)
5. [Application Architecture](#application-architecture)

## Before starting
To compile this project account information has to be provided by an
account manager, if you are interested in 
using this project please reach out to partners@wlvpn.com 

The Information needed to start:
- PackageCloud token
- Account Name
- Auth Suffix
- API Key  
- IPGEO URL 
- Test Username and password
 
## Getting Started

### Prerequisites

1. Android Studio 3.1
2. Java 17

### OS Requirements

1. Minimum Android SDK 21 (Android 5).
2. Currently supports Android SDK 34 (Android 14 Upside-down cake).

### Package Management Tool
[PackageCloud][4]  is our package management tool of choice for distributing and updating all packages related to the VPN SDK.

A key is needed and should be provided by your account manager.

Set the package cloud key obtained in the root project `build.gradle` (consumervpn2-android/build.gradle) file.

```groovy
    buildscript {
        ext.kotlin_version = '1.9.20'
        ext.packagecloud_token = "YOUR_PACKAGE_CLOUD_TOKEN"
    
        repositories {
            google()
            jcenter()
        }
    
        dependencies {
            classpath 'com.android.tools.build:gradle:8.3.2'
            classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        }
    }
    
    allprojects {
        repositories {
            google()
            jcenter()
            maven {
                url "https://packagecloud.io/priv/${packagecloud_token}/cloak/android-vpn-sdk/maven2"
            }
        }
    }
```

### VPN SDK Keys Setup

All VPN SDK keys are available at `strings_vpn_sdk_config.xml`, those should be provided by your account manager:
1. account_name -  Name of the account.
2. auth_suffix - Suffix of the account, normally used for VPN authentication (if no auth_suffix was provided, use account_name).
3. api_key - Key that grants access to the specific account.  
4. ip_geo_url - URL for the IPGeo endpoint associated with the account.
    
### Customizable URLs
The client must provide 
1. Support URL - Support related URL.
2. Forgot password URL - The application does not have an implementation for a user to recover his password, the user has to go to a web page to do so.

    ```xml
        <string name="support_url" translatable="false">https://www.wlvpn.com/</string>
        <string name="forgot_password_url" translatable="false">https://www.wlvpn.com/</string>
    ```
 
## Building The App
This project uses the Gradle build system, we provide different Build Types and Flavors to facilitate testing.

**Build Types**

1. Debug - The application is fully debuggable with code optimizations turned off for faster build times.
2. Staging - Same as Debug, with a different endpoint for testing new unreleased backend features.
3. Release - Code optimization and obfuscation are turned on.

**Product Flavors**

1. Mobile - Compatible with Mobile devices.
2. TV - Compatible only with TV devices.

### Running The Sample App

Once you have cloned the code, select the default Run Configuration `app` and run it on your target device.

## Theming Your Solution

By default, ConsumerVPN follows Google's [Material design][3]

### App Colors

- You can find the main application colors under `values/base_colors.xml`
- All other custom colors are under `values/colors.xml`

All of the colors have already been placed by category. Follow name descriptions and you can theme your app by replacing current values. 

### App Icons

Replace current app icons. We recommend creating your app icons using
Android Studio to accomplish this right click on the `res` folder and
choose `New -> Image Asset` option. Follow the instructions for the 
`Launcher Icons (Legacy and Legacy)` Icon Type. This should override
all icons or create new icons for the selected flavor.

### External Theming Resources

This VPN white-label solution follows `Google Material Design` principles.
It is recommended to consider these principles while branding your application:
 
1. [Material Design Style Guide][1]
2. [Material Design Color System][2]


## Application Architecture

This app's architecture follows the [Clean Architecture][5] guidelines; 
and is a custom implementation.

[1]: https://m3.material.io/foundations/content-design/style-guide/
[2]: https://m3.material.io/styles/color/
[3]: https://m3.material.io/
[4]: https://packagecloud.io/
[5]: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
