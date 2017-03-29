## Nearables quick start

```java
  private BeaconManager beaconManager = new BeaconManager(context);

  // Should be invoked in #onCreate.
  beaconManager.setNearableListener(new BeaconManager.NearableListener() {
    @Override
    public void onNearablesDiscovered(List<Nearable> nearables) {
      Log.d(TAG, "Discovered nearables: " + nearables);
    }
  });

  // Should be invoked in #onStart.
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override
    public void onServiceReady() {
      beaconManager.startNearableDiscovery();
    }
  });

  // Should be invoked in #onStop.
  beaconManager.stopNearableDiscovery();

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

## Secure UUID quick start

Ranging and region monitoring works transparently with [Secure UUID](https://community.estimote.com/hc/en-us/articles/201371053-What-security-features-does-Estimote-offer-How-does-Secure-UUID-work-) enabled beacons. All you need is:

1. Enable _Secure UUID_ via [Estimote app](https://play.google.com/store/apps/details?id=com.estimote.apps.main&hl=en) from Google Play or via SDK

```java
DeviceConnection connection = connectionProvider.getConnection(device);
boolean enable = true;
connection.settings.beacon.secure().set(enable, new SettingCallback<Boolean>() {
 @Override
 public void onSuccess(Boolean value) {
   // Handle success here
 }

 @Override
 public void onFailure(DeviceConnectionException exception) {
   // Handle failure here
 }
});
```

2. Make sure you have initialised SDK with your App ID & App Token.
  ```java
  //  App ID & App Token can be taken from App section of Estimote Cloud.
  EstimoteSDK.initialize(applicationContext, appId, appToken);
  ```

3. Use `SecureBeaconRegion` instead of `BeaconRegion` when starting ranging or monitoring.

  ```java
  // Initialise BeaconManager as before.
  // Find all *your* Secure UUID beacons in the vicinity.
  beaconManager.startRanging(new SecureBeaconRegion(“regionId”, null, null, null));

  // Remember that you can also range for other regions as well.
  beaconManager.startRanging(new BeaconRegion(“otherRegion”, null, null, null);
  ```

## Eddystone quick start

[Eddystone](https://developers.google.com/beacons) is an open protocol BLE protocol from Google. Estimote Beacons can broadcast the Eddystone packet.

With Estimote SDK you can:
 - find nearby Eddystone beacons (`beaconManager.startEddystoneScanning()`)
 - configure Eddystone ralated properties:
   - URL property of `Eddystone-URL` (see `BeaconConnection#eddystoneUrl`)
   - namespace & instance properties of `Eddystone-UID` (see `BeaconConnection#eddystoneNamepsace`, `BeaconConnection#eddystoneInstance`)
 - configure broadcasting scheme of beacon to `Estimote Default`, `Eddystone-UID` or `Eddystone-URL` (see `BeaconConnection#broadcastingScheme`)

[SDK Examples](https://github.com/Estimote/Android-SDK/tree/master/Examples) contains Eddystone related samples.

Note that you can play with Estimote Beacons broadcasting the Eddystone packet and change their configuration via [Estimote app on Google Play](https://play.google.com/store/apps/details?id=com.estimote.apps.main).

In order to start playing with Eddystone you need to update firmware of your existing Estimote beacons to at least `3.1.1`. Easiest way is through [Estimote app on Google Play](https://play.google.com/store/apps/details?id=com.estimote.apps.main).
Then you can change broadcasting scheme on your beacon to Eddystone-URL or Eddystone-UID.

Following code snippet shows you how you can start discovering nearby Estimote beacons broadcasting Eddystone packet.

```java
  private BeaconManager beaconManager = new BeaconManager(context);

  // Should be invoked in #onCreate.
  beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
    @Override
    public void onEddystonesFound(List<Eddystone> eddystones) {
      Log.d(TAG, "Nearby Eddystone beacons: " + eddystones);
    }
  });

  // Should be invoked in #onStart.
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override
    public void onServiceReady() {
      beaconManager.startEddystoneScanning();
    }
  });

  // Should be invoked in #onStop.
  beaconManager.stopEddystoneScanning();

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

## Connecting to your devices quick start

At first, you will need to scan for configurable devices around you:

```Java
BeaconManager beaconManager = new BeaconManager(this);
  // set foreground scan periods. This one will scan for 2s and wait 2s
  beaconManager.setForegroundScanPeriod(2000, 2000);
  // connects beacon manager to underlying service
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override
    public void onServiceReady() {
      // add listener for ConfigurableDevice objects
      beaconManager.setConfigurableDevicesListener(new BeaconManager.ConfigurableDevicesListener() {
        @Override
        public void onConfigurableDevicesFound(List<ConfigurableDevice> configurableDevices) {
          // handle the configurable device here. You can use it to acquire connection from DeviceConnectionProvider
        }
      });
     beaconManager.startConfigurableDevicesDiscovery();
```

Once you have your `ConfigurableDevice` object, you want to acquire `DeviceConnection` for it. To do that, you need to be connected to `DeviceConnectionProvider`.
It creates simple service that lets you handle multiple connections at once. This provider is bound to your context, so you only need to connect once during your context lifetime. Here is how to do that in your activity `onCreate` method:

```Java
 @Override
 protected void onCreate(Bundle savedInstanceState) {
   DeviceConnectionProvider connectionProvider = new DeviceConnectionProvider(this);
   connectionProvider.connectToService(new DeviceConnectionProvider.ConnectionProviderCallback() {
     @Override
     public void onConnectedToService() {
       // Handle your actions here. You are now connected to connection service.
       // For example: you can create DeviceConnection object here from connectionProvider.
    });
 }
 ```
Remember to call `connectionProvider.destroy()` method in your activity `onDestroy()`:

```Java
 @Override
 protected void onDestroy() {
  connectionProvider.destroy();
  super.onDestroy();
 }
```

When your Activity is connected to `ConnectionProvider`, and you got your `ConfigurableDevice` object, you can now try to establish device connection. Doing that is really easy from now on:
```Java
// Pass your ConfigurableDevice to connection provider method
DeviceConnection connection = connectionProvider.getConnection(device);
connection.connect(new DeviceConnectionCallback() {
  @Override
  public void onConnected() {
    // Do something with your connection.
    // You can for example read device settings, or make an firmware update.
    Log.d("DeviceConnection", "onConnected");
  }

  @Override
  public void onDisconnected() {
    // Every time your device gets disconnected, you can handle that here.
    // For example: in this state you can try reconnecting to your device.
    Log.d("DeviceConnection", "onDisconnected");
  }

  @Override
  public void onConnectionFailed(DeviceConnectionException exception) {
    // Handle every connection error here.
    Log.d("DeviceConnection", "onConnectionFailed");
  }
});
```
Now you can use `DeviceConnection` object to communicate with a configurable device. Remember that every time your connection fails, your `DeviceConnectionCallback` needs to handle that.

Don't worry about connection state while switching application context - after first creation, your connection is always kept in the underlying service. Launching new activity and creating new `DeviceConnection` object for the same `ConfigurableDevice` only adds new observers to current connection.
If you only want to detach your activity callbacks from connection, just use `connection.destroy()` method in your activity `onDestroy()` method:
```Java
@Override
protected void onDestroy() {
  super.onDestroy();
  connection.destroy();
}
```

To completely close the underlying connection just call:
```Java
connection.close()
```
From now on, if any application context holds active `DeviceConnectionCallback`, it will have it's `onDisconnected()` called. Of course, it will only happen when you haven't called `connection.destroy()` on that context. Be sure to handle that!


## Bulk updater quick start

Bulk updater is a stand alone object that takes scans from your ``BeaconManager`` and updates devices that belongs to the user. In basic form it will simply connect to the device and synchronise it with Estimote cloud. This operation can be customised with firmware updates or include custom settings to write. The bulk updater runs constantly and checks if scanned devices have any new changes to apply.

### Building bulk updater

```Java
BulkUpdater bulkUpdater = new BulkUpdaterBuilder(this)
.withCloudFetchInterval(5, TimeUnit.SECONDS)
.withFirmwareUpdate()
.withRetryCount(3)
.withTimeout(0)
.build()
```
`withCloudFetchInterval(long)` - sets how often bulk updater should sync data from the cloud. The shorter this interval is, the quicker new pending settings from the cloud are applied to subsequent devices. The default value is 5 seconds.

`withFirmwareUpdate()` - allows bulk updater to update firmware of selected devices. This feature is disabled by default.

`withRetryCount(int)` - specifies how many retries BU should take to update each device. After N unsuccessful attempts, the device status will be reported as `Status.FAILED`. The default value is 3.

` withTimeout(long)` - Sets the time after which bulk updater should end its job. It will simply stop updating and fetching data. If the value is 0, the process will run constantly (forever and ever, as long as your battery will last).

### Listening to bulk update events
`BulkUpdater` progress is reported via listener interface. You can react on each device status change with the proper UI change.  You can pass a listener while starting the `BulkUpdater` - it is recommended to start it in you Activity's `onResume()` method.

```Java
  @Override
  protected void onResume() {
    super.onResume();
    bulkUpdater.start(new BulkUpdater.BulkUpdaterCallback() {
      @Override
      public void onDeviceStatusChange(ConfigurableDevice device, BulkUpdater.Status newStatus, String message) {
        // do something here
        logTextView.append(device.deviceId + ": " + newStatus);
      }

      @Override
      public void onFinished(int updatedCount, int failedCount) {
        // do something here
        logTextView.append("Finished. Updated: " + updatedCount + " Failed: " + failedCount );
      }

      @Override
      public void onError(DeviceConnectionException e) {
        // do somethign here
        logTextView.setText("Error: " + e.getMessage());
      }
    });
  }
```
You can also stop bulk updater whenever you want. Just use the `stop()` method:

```Java
bulkUpdater.stop();
```
DON'T FORGET: Because bulk updater uses an underlying service for handling connection to devices, it is necessary to call `destroy()` on your activity's `onDestroy` method. This will prevent any memory leaks.

```Java
  @Override
  protected void onDestroy() {
    super.onPause();
    bulkUpdater.destroy();
  }
```

### Running scan and passing results to BulkUpdater
You need to have a `BeaconManager` which will scan for `ConfigurableDevice` objects nearby. A list of such objects should be passed after each scan cycle to `BulkUpdater`, where all the magic happens:

```Java
beaconManager.setConfigurableDevicesListener(new BeaconManager.ConfigurableDevicesListener() {
          @Override
          public void onConfigurableDevicesFound(List<ConfigurableDevice> configurableDevices) {
            bulkUpdater.onDevicesFound(configurableDevices);
          }
        });
        beaconManager.startConfigurableDevicesDiscovery();
```

And that's all! You can consider temporarily stopping `BeaconManager` scanning while device update is in progress - just check whether any device has changed its state to `Status.UPDATING` and invoke `beaconManager.stopConfigurableDeviceDiscovery()`.
You can also play with scan periods - sometimes scanning every 1 s is not that efficient and is just a waste of energy. You can play with this settings using`beaconManager.setForegroundScanPeriod(long, long)`.

