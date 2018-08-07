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

1. `Callback<VpnLoginResponse> loginWithUsername(username, password)`
    - This method will execute a login action using parameters:
        - **`username`**: `String` parameter holding credentials user value
        - **`password`**: `String` parameter holding credentials password value
    - This method returns a `Callback`:
        - **`onSuccess`** responds with a `VpnLoginResponse` object.
        - **`onError`** throws a `Throwable` object of the following types:
            - **`NetworkUnavailableException`**: For Network related errors
            - **`LoginErrorThrowable`**: For Login related issues while log in.
2. `Boolean isUserLoggedIn()`
    - This method will tell you if the current has a current session on
3. `Callback<Unit> logout()`
    - This method will execute a log out operation

### Examples

#### Java Example

```java
public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    // Setup your Activity and Button listeners

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.login_button:
                MyApplication.getVpnSdk().loginWithUsername(email, password)
                        .subscribe(vpnLoginResponse -> {
                            // Save any preferences or check any LoginResponse object properties in here
                            // Send the user to another activity in here
                            return Unit.INSTANCE;
                        }, throwable -> {
                            throwable.printStackTrace();
                            // Handle and manipulate errors in here
                            return Unit.INSTANCE;
                        });
                break;

            case R.id.logout_button:
                MyApplication.getVpnSdk().logout().subscribe(unit -> {
                    // Handle any action after a successful log out
                    return Unit.INSTANCE;
                }, throwable -> {
                    // Handle and manipulate errors in here
                    throwable.printStackTrace();
                });
                break;
                
            case R.id.is_user_login_button:
                if (MyApplication.getVpnSdk().isUserLoggedIn()) {
                    // The user was not logged in 
                } else {
                    // The user previously log into platform
                }
                break;
        }
    }
}
```

#### Kotlin Example

```kotlin
class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Setup your Activity and button listeners

    override fun onClick(view: View) {
        when (view.id) {
            R.id.login_button -> {
                MyApplication.vpnSdk!!.loginWithUsername(email, password)
                        .subscribe({ (accessToken, refreshToken, accountType, accountStatus, accessExpireEpoch, subEndEpoch, email) ->
                            // Save any preferences or check any LoginResponse object properties in here
                            // Send the user to another activity in here
                            Unit
                        }) { throwable ->
                            throwable.printStackTrace()
                            // Handle and manipulate errors in here
                            Unit
                        }
            }

            R.id.logout_button -> {
                MyApplication.vpnSdk!!.logout().subscribe({ unit ->
                    // Handle any action after a successful log out
                        Unit
                    }) { throwable ->
                        // Handle and manipulate errors in here
                        throwable.printStackTrace()
                    }
            }
            
            R.id.is_user_login_button -> {
                if (MyApplication.vpnSdk!!.isUserLoggedIn()) {
                    // The user was not logged in 
                } else {
                    // The user previously log into platform
                }
            }
        }
    }
}
```

## Disclaimer

This is not an official Google product. 

[1]: Javadocs/myClass.html