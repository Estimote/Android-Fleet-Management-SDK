# Connecting to beacons in Android SDK

Connecting to a device lets you change its settings (Minor, Major, advertising interval etc.).
To edit seetings you need to be an owner of the beacon in Estimote Cloud. 
Every attempt to connect with device that is not linked to your Estimote Account will fail.

Here is a brief list of actions that should be taken in order to acquire direct device connection:
 1. Create **ConfigurableDevicesScanner** object.
 2. Provide it with filters.
 3. Declare callback and start scanning.
 4. Once your callback gets a list of **ScanResultItem**, you can acquire your **ConfigurableDevice** object from each item.
 5. Create **ConnectionProvider** object - this connects your activity with an asynchronous service that will take care of      creating **DeviceConnection** for you. The reason for using **ConnectionProvider** is explained later on.
 6. Declare callback for provider and wait for service connection.
 7. Once you are connected to provider, you can connect to your device - declare callback and wait for device connection.
 8. Use your device connection to configure settings.

## Discovering configurable devices
You can easily scan your environment for your own devices, using **ConfigurableDeviceScanner** object. 
Please notice that it does not create a connection by itself, but provides **ConfigurableDevice** data object 
that can be used to obtain **DeviceConnection** later on. Here is how you can create basic configurable device discovery:

```Java
ConfigurableDevicesScanner deviceScanner = new ConfigurableDevicesScanner(context);
// Scan for devices own by currently logged user.
deviceScanner.setOwnDevicesFiltering(true);
// Scan only for Location Beacons. You can set here different types of devices, such as Proximity Beacons or Nearables.
deviceScanner.setDeviceTypes(DeviceType.LOCATION_BEACON);
// Pass callback object and start scanning. If scanner finds something, it will notify your callback.
deviceScanner.scanForDevices(new ConfigurableDevicesScanner.ScannerCallback() {
    @Override 
    public void onDevicesFound(List<ConfigurableDevicesScanner.ScanResultItem> devices) {
       for(ScanResultItem item : devices) {
          // Do something with your object.
          // ScanResultItem contains basic info about device discovery - such as RSSI, TX power, or discovery time.
          // It also contains ConfigurableDevice object. You can easily acquire it via item.configurableDevice
         }
  }
});
  
```
This scanner should not be used for long term scanning in the background. It uses low latency scanning which drains the battery. It is intended for displaying available devices in the UI. 
It should be started in your Activity's `OnResume()`  and stopped in `onPause()`. 

## Connecting to the ConnectionProvider service
Once you have your **ConfigurableDevice** object, you want to acquire **DeviceConnection** for it. To do that, you need to be connected to **DeviceConnectionProvider**. It creates simple service that lets you handle multiple connections at once. This provider is bound to your context, so you only need to connect once during your context lifetime. Here is how to do that in your activity `onCreate` method:

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
 
## Getting direct connection to your configurable device
When your Activity is connected to **ConnectionProvider**, and you got your **ConfigurableDevice** object, you can now try to establish device connection. Doing that is really easy from now on:
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
Now you can use **DeviceConnection** object to communicate with a configurable device. Remember that every time your connection fails, your **DeviceConnectionCallback** needs to handle that. 

### Dealing with multiple activities 
Don't worry about connection state while switching application context - after first creation, your connection is always kept in the underlying service. Launching new activity and creating new **DeviceConnection** object for the same **ConfigurableDevice** only adds new observers to current connection. 
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
From now on, if any application context holds active **DeviceConnectionCallback**, it will have it's `onDisconnected()` called. Of course, it will only happen when you haven't called `connection.destroy()` on that context. Be sure to handle that!


###### Known issues

Unfortunately due to the differences between Bluetooth implementations on many Android devices, you may facem problems with achieving stable connection to devices. It often occurs as randomly thrown `BluetoothGatt error 133`. We are trying our best to find a workaround, but the issue is linked to low-level libraries we cannot modify.

##Basic operations on connected device
Just after your device is connected, you can perform actions to read or write data to it. Please bear in mind that all these actions are performed **asynchronously**. For each operation you will need to define a callback object that will handle all possible results for you.

### Reading device setting
You can access all reading methods via `connection.settings` object. It holds references for many objects containing device data. Feel free to choose what you need, and then call `get(SettingCallback<T> callback)` on it. Be sure to implement callback methods! Take a look at that example:

```Java 
connection.settings.deviceInfo.firmware().get(new SettingCallback<Version>() {
  @Override 
  public void onSuccess(final Version value) {
    // Handle read data here. 
    // For example: display them in UI. This callback will be called in the same thread as connection was created (not opened).
    // You can use your activity method runOnUIThread(Runnable runnable) to handle that.
    Log.d("DeviceRead","Read firmware version:  " + value.toString());
  }

  @Override public void onFailure(DeviceConnectionException exception) {
    // Handle exceptions here.
    Log.d("DeviceRead","Reading firmware version failed.");
  }
});
```
### Writing device setting
Writing data to device is similar - in this case you need to call `set(SettingCallback<T> callback)` instead of get.
Keep in mind that some device settings are read only!
Writing device eddystone interval example:

```Java 
int advertisingInterval = 1000;
connection.settings.eddystone.tlm.advertisingInterval().set(advertisingInterval, new SettingCallback<Integer>() {
  @Override
  public void onSuccess(Integer value) {
   // Data saved to device
   Log.d("DeviceWrite","Written new Eddystone interval: " + value.toString());
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
   // Handle exceptions here.
   Log.d("DeviceWrite","Write new Eddystone interval failed.");
  }
});
```
If entered value is invalid, the **DeviceConnectionException** object will contain information about possible values.
Once you got the idea how to read/write device setting, let's learn how to make advanced operations.

## Advanced operations on connected device
It might come in handy to update firmware or multiple settings at once - we've got you covered!
### Bulk setting write
In order to make bulk settings write, you have to create **SettingsEditor** object. Take a look at example:

```Java 
// Take your connected DeviceConnection object and get it's editor 
SettingsEditor edit = connection.edit()
edit.set(connection.settings.beacon.proximityUUID(), UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"));
edit.set(connection.settings.beacon.major(), 1000);
edit.set(connection.settings.beacon.minor(), 100);
edit.commit(new SettingCallback() {
  @Override
  public void onSuccess(Object value) {
   // Handle success here. It will be called only when all settings have been written.
   Log.d("DeviceBulkWrite","Bulk write successful");
  }
  
  @Override
  public void onFailure(DeviceConnectionException exception) {
   // Handle exceptions
   Log.d("DeviceBulkWrite","Bulk write failed");
  });
}
```

### Updating firmware
To check if a device i up to date - use `checkForFirmwareUpdate()` method on your **Connection** object.
```Java
connection.checkForFirmwareUpdate(new DeviceConnection.CheckFirmwareCallback() {
  @Override
  public void onDeviceUpToDate(DeviceFirmware firmware) {
   // If device is up to date, handle that case here. Firmware object contains info about current version.
   Log.d("DeviceFirmwareUpdate","Device firmware is up to date.");
  }
  
  @Override
  public void onDeviceNeedsUpdate(DeviceFirmware firmware) {
   // Handle device update here. Firmware object contains info about latest version.
   Log.d("DeviceFirmwareUpdate","Device needs firmware update.");
  }
  
  @Override
  public void onError(DeviceConnectionException exception) {
   // Handle errors here
   Log.d("DeviceFirmwareUpdate","Error checking device firmware: " + exception.getMessage());
  }
});
```

When your device needs an update, use `updateFirmware(FirmwareUpdateCallback callback)` on your **Connection** object. Remember to implement your callback methods!
```Java
connection.updateDevice(new DeviceConnection.FirmwareUpdateCallback() {
  @Override
  public void onSuccess() {
   // Handle success
   Log.d("DeviceFirmwareUpdate","Device firmware updated.");
  }

  @Override
  public void onProgress(float progress, String message) {
   // Handle progress - range is 0.0 - 1.0
   Log.d("DeviceFirmwareUpdate","Device firmware update progress: " + progress + " Message: " + message);
  }

  @Override
  public void onFailure(DeviceConnectionException e) {
   // Handle failure. Don't worry about device state - upon failure, it resets back to its old version.
   Log.d("DeviceFirmwareUpdate","Device firmware update failure: " + e.getMessage());
  }
});
```

Please keep in mind that firmware update is an **asynchronous long-term** process. Be sure to inform your user about it, and take care of UI reports - you can use `onProgress(float progress, String message)` to get current update progress. 

### Accessing Nearable settings

All nearables have their own advertisers assigned to setting tree. You can access each setting via calling `connection.settings.estimote.nearable`. You can change advertised packet by switching broadcasting scheme setting:
```Java
 connection.settings.estimote.nearable.broadcastingScheme().set(NearableMode.IBEACON, new SettingCallback<NearableMode>() {
      @Override
      public void onSuccess(NearableMode value) {
        // Handle success
        Log.d("BroadcastingScheme","Changed nearable broadcastung scheme. ");
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        // Handle errors here
        Log.d("Broadcasting Scheme","Error setting broadcasting scheme: " + exception.getMessage());
      }
    });
```
In addition, from Sticker firmware version 1.3 there is a possiblity to setup Eddystone URL using our SDK. You can simply achieve that by accessing proper setting: 
```Java
connection.settings.estimote.nearable.eddystoneUrl().set("http://estimote.com", new SettingCallback<String>() {
      @Override
      public void onSuccess(String value) {
        // Handle success
        Log.d("Eddystone URL","Changed nearable eddystone url. ");
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        // Handle errors here
        Log.d("Eddystone URL Scheme","Error changing eddystone url: " + exception.getMessage());
      }
    });
```




 
