## Using telemetry advertiser

Estimote Location Beacons can broadcast Estimote Telemetry data packet, which gives you extra information from sensors and GPIO. This packet uses its own advertiser - you can easily enable/disable it and change its advertising interval and Tx power.

### Enabling telemetry
If you want your beacon to advertise telemetry packets, you need a **DeviceConnection** object with an opened connection. You can get it from **DeviceConnectionProvider** in your activity. More about creating stable connection to your own devices can be found [here](/Docs/DOC_deviceConnection.md).
```Java
DeviceConnection connection = connectionProvider.getConnection(device);
connection.connect(new DeviceConnectionCallback(){...});
```
Then you can change beacon settings to enable telemetry advertising.
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

### Adjusting telemetry advertising interval
```Java
int advertisingInterval = 1000;
connection.settings.estimote.telemetry.advertisingInterval().set(advertisingInterval, new SettingCallback<Integer>() {
      @Override
      public void onSuccess(Integer value) {
       // Handle setting success here
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
       // Handle failure here
      }
    });
```

### Adjusting telemetry transmit power
```Java
int txPower = 4;
connection.settings.estimote.telemetry.transmitPower().set(txPower, new SettingCallback<Integer>() {
      @Override
      public void onSuccess(Integer value) {
         // Handle setting success here
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        // Handle failure here
      }
    });

```
