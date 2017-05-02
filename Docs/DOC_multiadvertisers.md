# Multiple advertisers
Estimote Location Beacons are able to advertise multiple data packets simultaneously. Here is a full list of featured advertisers:
  1. Connectivity 
  2. Estimote Telemetry
  3. Estimote Location
  4. iBeacon
  5. Eddystone UID
  6. Eddystone URL
  7. Eddystone Telemetry
  
You can adjust each advertiser to have its own Tx power and advertising interval. You can set advertising interval in the range of `100 - 10000 ms` and Tx power as one of the allowed values: `-30, -20, -16, -12, -8, -4, 0, 4 dBm` 
### Connectivity
Estimote Connectivity packet lets you connect to a beacon. You can adjust its interval and power using `get()` and `set()` methods. This advertiser is always on and cannot be disabled.
```Java
int txPower = -8;
connection.settings.estimote.connectivity.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
```Java
int advertisingInterval = 1000;
connection.settings.estimote.connectivity.advertisingInterval().set(advertisingInterval, new SettingCallback<Integer>(){...});
```
### Estimote Telemetry
Estimote Telemetry advertiser provides information from beacon's sensors and GPIO. You can enable/disable it by using `set()` method:
```Java
boolean enable = true;
connection.settings.estimote.telemetry.enable().set(enable, new SettingCallback<Boolean>() {
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
You can also modify Tx power and advertising interval:
```Java
int txPower = -8;
connection.settings.estimote.telemetry.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
```Java
int advertisingInterval = 1000;
connection.settings.estimote.telemetry.advertisingInterval().set(txPower, new SettingCallback<Integer>() {...});
```

### Estimote Location
Beacon measured power advertiser. For getting distance to device.
```Java
connection.settings.estimote.location.enable()
```
Modifying transmit power:
```Java
int txPower = -8;
connection.settings.estimote.location.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
Modifying advertising interval:
```Java
int advertisingInterval = 1000;
connection.settings.estimote.location.advertisingInterval().set(txPower, new SettingCallback<Integer>() {...});
```

### iBeacon
Using and modifying iBeacon advertiser is also possible with our SDK. Here is how you can enable your iBeacon advertising:
```Java
boolean enable = true;
connection.settings.beacon.enable().set(enable, new SettingCallback<Boolean>() {
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
By default, Apple's iBeacon protocol has interval fixed at `100 ms`. We strongly recommend using this default value for optimal ranging/monitoring performance.
If you want to change it, you need to turn your beacon into  **non-strict mode**. Beacons in **non-strict mode** technically are not considered iBeacon-enabled by they are compatible with iBeacon apps:
```Java
boolean enable = true;
connection.settings.beacon.nonStrictMode().set(enable, new SettingCallback<Boolean>() {
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
Then you will be able to change advertising interval:
```Java
int advertisingInterval = 1000;
connection.settings.estimote.beacon.advertisingInterval().set(txPower, new SettingCallback<Integer>() {...});
```
Beside of that, you can always modify your transmit power:
```Java
int txPower = -8;
connection.settings.estimote.beacon.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
Changing beacon proximity UUID:
```Java
UUID newUUID = UUID.randomUUID();
connection.settings.beacon.proximityUUID().set(newUUID, new SettingCallback<UUID>() {
  @Override
  public void onSuccess(UUID value) {
    // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
```
Changing **minor** and **major**:
```Java
int minor = 1000;
connection.settings.beacon.minor().set(minor, new SettingCallback<Integer>() {
  @Override
  public void onSuccess(Integer value) {
    // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
```
```Java
int major = 2000;
connection.settings.beacon.major().set(major, new SettingCallback<Integer>() {
  @Override
  public void onSuccess(Integer value) {
   // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
```
Enabling **secure mode**:
```Java
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
You can also enable **motion UUID** - it advertises different UUID when the beacon is in motion:
```Java
boolean enable = true;
connection.settings.beacon.enableMotionUUID().set(enable, new SettingCallback<Boolean>() {
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

### Eddystone
Eddystone packets can be modified using `set()` and `get()` methods as in examples above. For more info about each Eddystone packet please read [our article](http://developer.estimote.com/eddystone/).

#### Eddystone UID
```Java
boolean enable = true;
connection.settings.eddystone.uid.enable().set(enable, new SettingCallback<Boolean>() {...});
```
Modifying advertising interval:
```Java
int advertisingInterval = 1000;
connection.settings.eddystone.uid.advertisingInterval().set(advertisingInterval, new SettingCallback<Integer>() {...});
```
Modifying transmit power:
```Java
int txPower = -8;
connection.settings.eddystone.uid.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
Modifying namespace:
```Java
String namespace = "EDD1EBEAC04E5DEFA017";
connection.settings.eddystone.uid.namespace().set(namespace, new SettingCallback<String>() {
  @Override
  public void onSuccess(String value) {
    // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
 ```
Modifying instance:
```Java
String instance = "0BDB87539B67";
connection.settings.eddystone.uid.instance().set(instance, new SettingCallback<String>() {
  @Override
  public void onSuccess(String value) {
    // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
```
#### Eddystone URL
```Java
boolean enable = true;
connection.settings.eddystone.url.enable().set(enable, new SettingCallback<Boolean>() {
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
Modifying transmit power:
```Java
int txPower = -8;
connection.settings.eddystone.url.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
```
Modifying advertising interval:
```Java
int advertisingInterval = 1000;
connection.settings.eddystone.url.advertisingInterval(advertisingInterval, new SettingCallback<Integer>() {...});
```
Modifying url value:
```Java
String url = "http://estimote.com";
connection.settings.eddystone.url.url().set(url, new SettingCallback<String>() {
  @Override
  public void onSuccess(String value) {
    // Handle success here
  }

  @Override
  public void onFailure(DeviceConnectionException exception) {
    // Handle failure here
  }
});
```
#### Eddystone Telemetry
```Java
boolean enable = true;
connection.settings.eddystone.tlm.enable().set(enable, new SettingCallback<Boolean>() {
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
Modifying transmit power:
```Java
int txPower = -8;
connection.settings.eddystone.tlm.transmitPower().set(txPower, new SettingCallback<Integer>() {...});
 ```
Modifying advertising interval:
```Java
int advertisingInterval = 1000;
connection.settings.eddystone.tlm.advertisingInterval(advertisingInterval, new SettingCallback<Integer>() {...});
```
