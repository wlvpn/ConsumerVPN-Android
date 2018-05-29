# ConsumerVPN

ConsumerVPN is a White Label VPN Application intended to make easy to create
new vpn applications for Android platforms

## Requirements

1. Android Studio 3.1 or superior
2. Android SDK 8.1
3. Android NDK 16 or superior
4. Android Support library 27
6. Build Tools 27
5. Kotlin Plugin 1.2.30


Note: You can always try to upgrade the internal libraries. 
For most cases they should work just fine

## Start

1. Fork or Clone the solution at your desire end
2. Open the solution in Android Studio
3. Set the package cloud key obtained in the root project `build.gradle` file

    ```groovy
    allprojects {
        version = property("android.versionName")
        group = property('pom.groupId')
        repositories {
            mavenLocal()
            jcenter()
            maven { url 'https://maven.google.com' }
            maven {
                url "https://packagecloud.io/priv/MY_PACKAGE_CLOUD_API_KEY/cloak/android-vpn-sdk/maven2"
            }
        }
    }
    ```

4. In the `consumerVPN` app module `build.gradle` file set the `vpn-sdk` api key
5. Next to the key set your company website locations, account name, and 
authentication suffix.

    ```groovy
    buildConfigField 'String', 'API_KEY', '"MY_API_KEY"'
    buildConfigField 'String', 'ACCOUNT_NAME', '"MY_ACCONT_NAME"'
    buildConfigField 'String', 'AUTH_SUFFIX', '"MY_AUTH_SUFFIX"'
    buildConfigField 'String', 'SUPPORT_URL', '"https://www.mysite.com/support"'
    buildConfigField 'String', 'SIGN_UP', '"https://www.mysite.com/signup"'
    buildConfigField 'String', 'FORGOT_PASS', '"https://www.mysite.com/forgotpassword"'
    ```
    
6. If the vpn `API's` are hosted in a different domain than the default ones 
set those values too inside the same file

    ```groovy
    buildConfigField 'String', 'BASE_HOSTNAME', '"mycompany.com"'
    buildConfigField 'String', 'AUTH_SUFFIX', '""'
    buildConfigField 'String', 'PROTOCOL_LIST_API', '"/protocols"'
    buildConfigField 'String', 'LOGIN_API', '"/login"'
    buildConfigField 'String', 'REFRESH_API', '"/refresh"'
    buildConfigField 'String', 'SERVER_LIST_API', '"/servers"'
    buildConfigField 'String', 'IP_GEO', '"https://ipgeo.mycompany.com/v3?apikey=MY_API_KEY"'
    buildConfigField 'String', 'CONFIGURATION_URL', '""'
    buildConfigField 'String', 'CERTIFICATE_URL', '""'
    ```

7. Crate a keystore, `.jks` file, using your prefer method. Our recommendation is to let
Android studio Studio help you for this task. To do this you must do the following:
    1. Open `Build` menu
    2. Click on `Generate Signed APK...`
    3. Choose any module and click next.
    4. Next to Key Store Path input click on `Create New...`
    5. Choose internal folder `.keys` location to store the file
    6. Fill other values and make sure you store the selected `Password's` and `Alias`
    7. Click ok and close the section. Check if the file was stored and replace
    current values.
    8. Delete any previous keystore file `.jks`
    9. In the `consumerVPN` app module `build.gradle` file replace for the new values
    
        ```groovy
        signingConfigs {
            myAppNameConfiguration {
                storeFile rootProject.file("config/keystore/MyAppKeystore.jks")
                keyAlias "MY_APP_KEYSTORE_ALIAS"
                keyPassword "MY_APP_KEYSTORE_PASSWORD"
                storePassword "MY_APP_KEYSTORE_STORE_PASSWORD"
            }
        }
        ```
8. Update Flavor values to match with your applications on the `consumerVPN` module
`build.gradle` file. This includes the `applicationId`, this will override the 
package name of your application), and the current flavor name.
    ```groovy
    productFlavors {
            consumervpn {
                applicationId 'com.myappdomain.vpn.android'
                signingConfig signingConfigs.myAppNameConfiguration
                dimension "brand"
                //... Other configurations takes place in here
             }
       // Any other flavors could be set in here
    } 
    ```
9. Sync your project and build your solution. The solution should be ready at this point
10. You are ready to publish your solution in your private Git Repo manager

## Building
This project uses the Gradle build system with integration to several quality check tools.

**Build Types**

* Debug
    * Development API endpoint
    * Debug on
* Release
    * Production API endpoint
    * Debug off
    * Key signing
    * Proguard
    * Minify
    
## Theme your Solution

Showing your brand colors was made to make it really simple for you.
If you want to keep the default configuration values you will need to add 
the correspondent flavor name folder inside the `consumerVPN` app module
and add a copy of the desire resources `res` files like

1. `values/colors.xml`
2. `values/strings.xml`
3. `values/styles.xml` and `values-v21/styles.xml`

All of the colors have been already placed by category. Follow name descriptions
and you will be able to theme your app by replacing current values. 

### App Icons

Replace current app icons. We recommend to create your app icon's using
Android Studio to accomplish this right click on the `res` folder and
choose `New -> Image Asset` option. Follow the instructions for the 
`Launcher Icons (Legacy and Legacy)` Icon Type. This should override
all icons or create new icons for the selected flavor.

### App Internal Icons

Make sure selected internal app icons meet your brand color requirements.
All Vector files uses `Android Vector` format. 
Change the colors using `HEX` (Hexadecimal) format by altering all 
xml property `fillColor` from `res/drawable` folder files:

1. `ic_filter.xml`
2. `ic_ip.xml`
3. `ic_location.xml`
4. `ic_lock.xml`
5. `ic_search.xml`
6. `ic_user.xml`
 
Note: The current app logo is saved as a vector `ic_logo` you will need to replace this 
file in order to use your brand logo instead. This file can be saved in other formats as 
`png` or `webp`


### External Helpful Sources

This VPN white label solution follows `Google Material Design` principles.
Is recommended to consider this principles while branding your application:
 
1. [Material Design Guidelines][1]
2. [Color Tool][2]

    
## Disclaimer

This is not an official Google product.

[1]: https://material.io/guidelines/
[2]: https://material.io/color/
