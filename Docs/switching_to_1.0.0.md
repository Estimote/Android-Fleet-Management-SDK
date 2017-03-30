# Switching to SDK 1.0.0

To help you migrate your old project to our new API, we prepared a short how-to-switch guide!
We did a lot of work to improve the quality of our SDK. We introduced some changes to our documentation (and will add more over time). We appreciate your feedback, so feel free to post any issue/improvement ideas on our Github page.

Be sure you add new line to your build.gradle file:

```gradle
dependencies {
  compile 'com.estimote:sdk:1.0.0:release@aar'
}
```

## New packages and fixing imports
Some classes are now in different packages. You compiler will not see the old classes and will highlight them.
A simple way to fix that is to delete the lines from the old (previous) import. After that, Android Studio will show pop-ups with newly found packages.
Use this trick to fix the wrong imports.
Why did we do that?
Because we believe in constant improvement and our old packages were in need of some spring cleaning.

## BeaconManager api changes
The old `Region` class is now replaced with `BeaconRegion` / `MirrorRegion`.
This allows us to add new region implementations in the future. You can use `BeaconRegion` just as you used `Region` - in fact it's the same class with just the name changed. As for `MirrorRegion` — you can create a region from the list of Mirror devices id's. You can also make it `null` - it will scan for every Mirror packet around you.

All `startDiscovery()` methods (Location, Eddystone, ConfigurableDevice, Telemetry) are now of the `void` type. You no longer need to use `scanId` for stopping discovery via `stopDiscovery()`.
For example, starting Eddystone discovery should look like this:

```Java
beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
  @Override public void onEddystonesFound(List<Eddystone> eddystones) {
    // Handle your Eddystones here!
  }
});
beaconManager.startEddystoneDiscovery();
```

Don't forget to stop your scanning when not in use:

```Java
beaconmanager.stopEddystoneDiscovery()
```

## Scanning for ConfigurableDevice objects
Scanning for `ConfigurableDevice` objects is now handled in BeaconManager instead of the old `ConfigurableDevicesScanner`. These objects are crucial for establishing connection to each device.
The basic flow for connecting to device is as follows:

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

# New BulkUpdater

New bulk updater is a stand alone object that takes scans from your ``BeaconManager`` and updates devices that belongs to the user. In basic form it will simply connect to the device and synchronise it with Estimote cloud. This operation can be customised with firmware updates or include custom settings to write. The bulk updater runs constantly and checks if scanned devices have any new changes to apply.

## Building bulk updater

```Java
BulkUpdater bulkUpdater = new BulkUpdaterBuilder(this)
.withCloudFetchInterval(5, TimeUnit.SECONDS)
.withFirmwareUpdate()
.withRetryCount(3)
.withTimeout(0)
.build()
```
``withCloudFetchInterval(long)`` - sets how often bulk updater should sync data from the cloud. The shorter this interval is, the quicker new pending settings from the cloud are applied to subsequent devices. The default value is 5 seconds.
``withFirmwareUpdate()`` - allows bulk updater to update firmware of selected devices. This feature is disabled by default.
``withRetryCount(int)`` - specifies how many retries BU should take to update each device. After N unsuccessful attempts, the device status will be reported as `Status.FAILED`. The default value is 3.
`` withTimeout(long)`` - Sets the time after which bulk updater should end its job. It will simply stop updating and fetching data. If the value is 0, the process will run constantly (forever and ever, as long as your battery will last).

## Listening to bulk update events
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
You can also stop bulk updater whenever you want. Just use the ``stop()`` method:

```Java
bulkUpdater.stop();
```
DON'T FORGET: Because bulk updater uses an underlying service for handling connection to devices, it is necessary to call ``destroy()`` on your activity's `onDestroy` method. This will prevent any memory leaks.

```Java
  @Override protected void onDestroy() {
    super.onPause();
    bulkUpdater.destroy();
  }
```

## Running scan and passing results to BulkUpdater
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

