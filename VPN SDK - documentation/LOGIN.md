# 2. Login

Login is a convenient, secure, and fast way for users to log authenticate to your application.

## `VpnLoginResponse`

Class: [com.gentlebreeze.vpn.sdk.model.VpnLoginResponse][1]

Contains values related to successful VPN-SDK login calls

- **`accessExpireEpoch`**: `long` timestamp for when your access token expires
- **`accessToken`**: `String` value containing session access token
- **`accountStatus`**: `int` value containing account status
- **`accountType`**: `int` value containing account type
- **`email`**: `String` value containing user email
- **`refreshToken`**: `String` value containing the session refresh token
- **`subEndEpoch`**: `long` timestamp for when the user's subscription ends

## Login Methods

### `loginWithUsername(...)`

```kotlin
    fun loginWithUsername(
        /** parameter holding credentials user value **/
        username: String,
        /** parameter holding credentials password value **/
        password: String
    ): Callback<VpnLoginResponse>
```
Attempts to login the user into the VPN service.

The returned callback will respond with either of these two:
- **`onSuccess`** responds with a `VpnLoginResponse` object.
- **`onError`** throws a `Throwable` object of the following types:
    - **`NetworkUnavailableException`**: For Network related errors.
    - **`LoginErrorThrowable`**: For Login related issues.
    

### `refreshToken(...)`
```kotlin
    fun refreshToken(
        /** The user's username **/
        username: String,
        /** The user's password **/
        password: String
    ): Callback<VpnLoginResponse>

```

You can use this method to update the session token when the current one is invalid or has expired.

The returned callback will respond with either of these two:
- **`onSuccess`** responds with a `VpnLoginResponse` object.
- **`onError`** throws a `Throwable` object of the following types:
    - **`NetworkUnavailableException`**: For Network related errors
    - **`LoginErrorThrowable`**: For Login related issues.


### `isUserLoggedIn()`
```kotlin
fun isUserLoggedIn(): Boolean
```
Returns true if the user has a valid session by validating the access token.

### `logout()`
```kotlin
fun logout(): Callback<Unit>
```
Logs out the user of the VPN service; clears auth and account info.

### Example

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Setup your Activity and button listeners

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> {
                MyApplication.vpnSdk?.loginWithUsername(email, password)
                        ?.subscribe({ (accessToken, refreshToken, accountType, accountStatus,
                            accessExpireEpoch, subEndEpoch, email) ->
                            // Save any preferences or check any LoginResponse object properties in here
                            // Send the user to another activity in here
                        }) { throwable ->
                            throwable.printStackTrace()
                            // Handle and manipulate errors in here
                        }
            }

            R.id.logout_button -> {
                MyApplication.vpnSdk?.logout()
                    ?.subscribe({ unit ->
                    // Handle any action after a successful log out
                    }) { throwable ->
                        // Handle and manipulate errors in here
                        throwable.printStackTrace()
                    }
            }
            
            R.id.is_user_login_button -> {
                if (MyApplication.vpnSdk?.isUserLoggedIn()) {
                    // The user was not logged in 
                } else {
                    // The user previously log into platform
                }
            }
        }
    }
}
```

[1]: javadoc/sdk/com.gentlebreeze.vpn.sdk.model/-vpn-login-response/index.html