# ConsumerVPN

ConsumerVPN is a White Label VPN Application intended to make easy to create
new vpn applications for Android platforms

## Before start
To compile this project account information has to be provided by an
account manager, if you are interested on 
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

1. Android Studio 3.1 or superior
2. Android SDK 8.1
3. Android NDK 16 or superior
4. Android Support library 28
6. Build Tools 28
5. Kotlin Plugin 1.3.31 

Note: You can always try to upgrade the internal libraries. 
For most cases they should work just fine

### PackageCloud
[PackageCloud][4]  is our package management tool of choice  to distribute and update all packages related to the VPN SDK

A key is needed and should be provided by your account manager

Set the package cloud key obtained in the root project `build.gradle` (consumervpn2-android/build.gradle) file

```groovy
    buildscript {
        ext.kotlin_version = '1.3.31'
        ext.packagecloud_token = "YOUR_PACKAGE_CLOUD_TOKEN"
    
        repositories {
            google()
            jcenter()
        }
    
        dependencies {
            classpath 'com.android.tools.build:gradle:3.4.1'
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

### VPN SDK Keys set up

All VPN SDK keys are available at `strings_vpn_sdk_config.xml`, those should be provided by your account manager
    1. account_name -  Name of the account
    2. auth_suffix - Suffix of the account, normally used for VPN authentication (if no auth_suffix was provided, use account_name)
    3. api_key - Key that grants access to the specific account.  
    4. ip_geo_url - URL for the IPGeo endpoint associated with the account

### Custom APIs
These should only be modified if the vpn `API's` are hosted in a different domain than the default ones 
    ```xml
    <string name="protocol_list_api" translatable="false">protocols</string>
    <string name="login_api" translatable="false">login</string>
    <string name="token_refresh_api" translatable="false">login</string>
    <string name="server_list_api" translatable="false">servers</string>
    ```
    
### Custom URLs
The client must provide 
1. Support URL - Support related URL.
2. Sign up URL - The application does not have an implementation of user registration, the user has to go to a web page to register.
3. Forgot password URL - The application does not have an implementation for a user to recover his password, the user has to go to a web page to do so.

    ```xml
        <string name="support_url" translatable="false">https://www.wlvpn.com/</string>
        <string name="sign_up_url" translatable="false">https://www.wlvpn.com/</string>
        <string name="forgot_password_url" translatable="false">https://www.wlvpn.com/</string>
    ```
 
## Building
This project uses the Gradle build system, we provide differen Build types and flavors to facilitate testing

**Build Types**

1. Debug 
    The application is fully debuggable with code optimizations turned off for faster build times
2. Staging
    Same as Debug, with a different endpoint for testing new unreleased backend features
3. Release
    Code optimization and obfuscation are turned on

**Product Flavors**

1. Mobile Version compatible with Mobile devices
2. TV compatible only with TV devices

## Theme your Solution

By default ConsumerVPN follows Google's [Material design][3]

### Application Colors

- You can find the main application colors under `values/base_colors.xml`
- All other custom colors are under `values/colors.xml`

All of the colors have been already placed by category. Follow name descriptions and you will be able to theme your app by replacing current values. 

### App Icons

Replace current app icons. We recommend to create your app icon's using
Android Studio to accomplish this right click on the `res` folder and
choose `New -> Image Asset` option. Follow the instructions for the 
`Launcher Icons (Legacy and Legacy)` Icon Type. This should override
all icons or create new icons for the selected flavor.

### External Helpful Sources

This VPN white label solution follows `Google Material Design` principles.
Is recommended to consider this principles while branding your application:
 
1. [Material Design Guidelines][1]
2. [Color Tool][2]


## Application Architecture

We follow the principles of [Clean Architecture][]


[1]: https://material.io/guidelines/
[2]: https://material.io/color/
[3]: https://material.io/design/
[4]: https://packagecloud.io/
[5]: https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html
