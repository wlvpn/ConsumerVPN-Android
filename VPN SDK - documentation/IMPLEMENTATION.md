# Implementation

Before starting this section, it is recommended that you read VPN-SDK's [Setup][1]

Throughout all of our libraries, we use RxJava 1 for all of our handling
of threading, data manipulation, and event buses. 

To simplify this process for the SDK implementation and to ensure that 
the user does not have to maintain different Rx versions, we created a lightweight 
wrapper class called `Callback`.

Any async calls from the SDK will return a `Callback` object that can be passed
two **optional** callbacks; one for handling the result, and one for handling errors. To execute
the call, you must `subscribe()`, which is simply a call to the observable's subscribe
method behind the scenes.

Here is an example:

### Java Example

```java
public class MainActivity extends AppCompatActivity {
    @Override
    public void onResume() {
        super.onResume();
        
        MyApplication.getVpnSdk()
            .fetchAllServers()
            .subscribe(vpnServers -> {
                //do something with the servers
            }, throwable -> {
                //There was an error fetching the servers
            });
    }
}
```

### Kotlin Example

```kotlin
class MainActivity: AppCompatActivity() {
    
     fun loadServersData() {
        
        MyApplication.vpnSdk!!.fetchAllServers()
               .subscribe({ vpnServers: List<VpnServer> ->
                   //do something with the servers
               }, { throwable ->
                   //There was an error fetching the servers
               })
    }
    
}
```

If you prefer, you can also use the builder methods to build out the callback
response. Just as a note, using the `subscribe()` method parameters
with the builder methods will overwrite the builder inputs.

### Java Example

```java
public class MainActivity extends AppCompatActivity {

    public void loadServersData() {
        
        MyApplication.getVpnSdk()
            .fetchAllServers()
            .onNext(vpnServers -> {
                //do something with the servers
            })
            .onError(throwable -> {
                //There was an error fetching the servers
            });
    }
}
```

### Kotlin Example

```kotlin
class MainActivity: AppCompatActivity() {
   
    fun loadServersData() {
        
        MyApplication.vpnSdk!!.fetchAllServers()
            .onNext { vpnServers: List<VpnServer> ->
                //do something with the servers
            }
            .onError { e: Throwable ->            
                //There was an error fetching the servers
            }
            .subscribe()
    }
    
}
```

Due to the threading requirements of some of the processes in the SDK,
we did not expose RxJava's `subscribeOn()` method, which would overwrite
our threading and possibly introduce unexpected bugs. We do, however,
expose RXJava's `observeOn()` which can be used to determine which
Thread Pool executor the results are handled on. This can be used like so:

### Java Example

```java
public class MainActivity extends AppCompatActivity {
   
    public void loadServersData() {
        
        ThreadPoolExecutor exampleThreadPool = 
            new ThreadPoolExecutor(
                1, 1, 0L, 
                TimeUnit.MILLISECONDS, 
                LinkedBlockingQueue(),                    
                new ThreadFactory(r -> {
                    new Thread(r, "myThread");
                }));
        
        MyApplication.getVpnSdk()
            .fetchAllServers()
            .onNext(vpnServers -> {
                //do something with the servers
            })
            .onError(throwable -> {
                //There was an error fetching the servers
            });
    }
}
```

### Kotlin Example

```kotlin
class MainActivity: AppCompatActivity() {
   
     fun loadServersData() {
        
        val exampleThreadPool = ThreadPoolExecutor(
            1, 1, 0L, 
            TimeUnit.MILLISECONDS, 
            LinkedBlockingQueue(),                    
            ThreadFactory { 
                r -> Thread(r, "myThread") 
            })
        
        MyApplication.vpnSdk!!.fetchAllServers()
            .observeOn(exampleThreadPool)
            .onNext { /* do something */ }
            .onError { /* handle error */ }
            .subscribe()
    }
    
}
```


In most situations, the SDK callbacks will fire and complete normally. However,
in some instances where you may be listening to state changes or waiting
on a long running api call, you may need to unsubscribe from the callback to prevent
a possible memory leak. The callback object contains an instance to the
RxJava subscription object and will handle teardown and creation internally.
To handle unsubscribing, simply call the unsubscribe method:

### Java Example

```java
public class MainActivity extends AppCompatActivity {
    
    private Callback callback;
    
    public void listenToConnectState() {
        callback = MyApplication.getVpnSdk().listenToConnectState()
            .onNext(vpnState -> {
                // So something
            })
            .onError(throwable -> {
                // Handle error
            })
            .subscribe();
    }
    
    public void onDestroy() {
        if (callback != null) {
            callback.unsubscribe();
            callback = null;
        }
                
        super.onDestroy();
    }
}
```

### Kotlin Example

```kotlin
class MainActivity: AppCompatActivity() {
    
    private var callback: Callback? = null
    
    fun listenToConnectState() {
        callback = MyApplication.vpnSdk!!
            .listenToConnectState()
            .onNext { /* do something */ }
            .onError { /* handle error */ }
            .subscribe()
    }
    
    override fun onDestroy() {
        callback?.unsubscribe()    
        callback = null
        super.onDestroy()
    }
    
}
```

## 1. Api Error Codes

The `Throwable` object can expose error codes coming from the API using the method
`getResponseCode()`. These values are:

- **Password Required**: _1000_
- **Invalid Api Key**: _1001_
- **Could Not Provision Oauth**: _1009_
- **Invalid Credentials**: _1100_

## 2. Login

Login is a convenient, secure, and fast way for users to log authenticate to your application.

See VPN-SDK'S [login implementation document][2]

## 3. Pops

Fetching of pops is the way your app has to obtain specific information about 
different available location address. Later on this information could be useful
to restrict connection to specific server locations as countries and/or cities.

See VPN-SDK'S [Pops implementation document][3]

## 4. Servers

Fetching servers is the way your app has to obtain specific information about 
all different servers location.

This API also contains some operations related to servers

See VPN-SDK'S [Servers implementation document][4]

## 5. VPN IP Geolocation

Ip Geo will help you determine the users geo location

See VPN-SDK'S [IP Geolocation document][5]

## 6. VPN Connection

Connection API is the base for our vpn application. 
In general, it helps to create and secure network tunneling in your vpn application.

See VPN-SDK'S [Connection implementation document][6]

## 7. VPN Event Listening

Helper to receive connection events in your application.

See VPN-SDK'S [Connection listener implementation document][7]

## Disclaimer

This is not an official Google product. 

[1]: SETUP.md
[2]: LOGIN.md
[3]: POPS.md
[4]: SERVERS.md
[5]: IPGEO.md
[6]: CONNECTION.md
[7]: LISTENER.md