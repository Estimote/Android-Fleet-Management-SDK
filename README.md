# Estimote SDK for Android #

## Overview ##

Estimote SDK for Android is a library to allow interaction with Estimote beacons. The SDK system requirements are Android 4.3 or above and Bluetooth Low Energy.

It mimics [Estimote SDK for iOS](https://github.com/Estimote/iOS-SDK). All naming conventions come from the iBeacon library for iOS and the Estimote iOS library.

It allows for:
- beacon ranging (scan beacons and optionally filters them by their values)
- beacon monitoring (monitors regions for those devices that have entered/exited a region)
- beacon characteristic reading and writing (proximity UUID, major & minor values, broadcasting power, advertising interval), see [BeaconConnection] (http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/connection/BeaconConnection.html) class and [demos](https://github.com/Estimote/Android-SDK/tree/master/Demos) in the SDK

Docs: 
 - [Current JavaDoc documentation](http://estimote.github.io/Android-SDK/JavaDocs/)
 - [Estimote Community Portal](http://community.estimote.com/hc/en-us)

**What is ranging?**

Ranging allows apps to know the relative distance between a device and beacons. This can be very valuable. Consider for example of an indoor location app of department store. The app can determine which department (such footwear, clothing, accessories etc) is closest by. Information about this proximity can be employed by the app to show fitting guides or offer discounts.

As Bluetooth Low Energy ranging depends on detecting radio signals, results will vary depending on the placement of Estimote beacons and whether a user's mobile device is in-hand, in a bag or a pocket. Clear line of sight between a mobile device and a beacon will yield better results so it is recommended that Estimote beacons not be hidden between shelves.

To enjoy consistent ranging it is good practice to use the app in the foreground while the user is holding the device in-hand (which means the app is on and running).

Apps can use `startRanging` method of `BeaconManager` class to determine relative proximity of beacons in the region and can be updated when this distance changes. Ranging updates come every second to listeners registered with `setRangingListener` method of `BeaconManager` class. Ranging updates contain a list of currently found beacons. If a beacon goes out of range it will not be presented on this list.

Ranging is designed to be used in apps in foreground.

**What is monitoring?**

Region monitoring is a term used to describe a Bluetooth device's usage and  detect when a user is in the vicinity of beacons. You can use this functionality to show alerts or provide contextual aware information as a user enters or exits  a beacon's region. Beacon's regions are defined by beacon's values:

- proximity UUID: 128-bit unique identifier,
- major: 16-bit unsigned integer to differentiate between beacons within the same proximity UUID,
- minor: 16-bit unsigned integer to differentiate between beacons with the same proximity UUID and major value.

Note that all of those values are optional. That means that single region can contain multiple beacons which creates interesting use cases. Consider for example a department store that is identified by a particular proximity UUID and major value. Different sections of the store are differentiated further by a different minor value. An app can monitor region defined by their proximity UUID and major value to provide location-relevant information by distinguishing minor values.

Apps can use `startMonitoring` method of `BeaconManager` class to start monitoring regions. Monitoring updates come to listeners registered with `setMonitoringListener` method of `BeaconsManager` class.

Monitoring is designed to perform periodic scans in the background. By default it scans for 5 seconds and sleeps 25 seconds. That means that it can take by default up to 30 seconds to detect entering or exiting a region. Default behaviour can be changed via `BeaconManager#setBackgroundScanPeriod`.

## Installation ##

1. Copy [estimote-sdk-preview.jar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk-preview.jar) to your `libs` directory.
2. Add following permissions and service declaration to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

```xml
<service android:name="com.estimote.sdk.service.BeaconService"
         android:exported="false"/>
```
(optional) You can enable debug logging of the Estimote SDK by calling `com.estimote.sdk.utils.L.enableDebugLogging(true)`.

## Usage and demos ##

Demos are located in [Demos](https://github.com/Estimote/Android-SDK/tree/master/Demos) directory. You can easily build it with [Gradle](http://www.gradle.org/) by typing `gradlew installDebug` (or `gradlew.bat installDebug` on Windows) in terminal when your device is connected to computer.

Demos include samples for ranging beacons, monitoring beacons, calculating distance between beacon and the device and also changing minor value of the beacon.

Quick start with ranging:

```java
  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
  private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

  private BeaconManager beaconManager = new BeaconManager(context);

  // Should be invoked in #onCreate.
  beaconManager.setRangingListener(new BeaconManager.RangingListener() {
    @Override public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
      Log.d(TAG, "Ranged beacons: " + beacons);
    }
  });

  // Should be invoked in #onStart.
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override public void onServiceReady() {
      try {
        beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
      } catch (RemoteException e) {
        Log.e(TAG, "Cannot start ranging", e);
      }
    }
  });

  // Should be invoked in #onStop.
  try {
    beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
  } catch (RemoteException e) {
    Log.e(TAG, "Cannot stop but it does not matter now", e);
  }

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

## FAQ ##


1. Where are JavaDocs for Estimote Android library?

  They are published on [GitHub pages](http://estimote.github.io/Android-SDK/JavaDocs/).

2. Android Bluetooth stack is crashing (“Bluetooth Share has stopped” alert)

  You may observe in logs either of those two:
   * process com.android.bluetooth crashed with SIGSEGV and backtrace leading to Bluetooth native driver
   * exception similar to this one:
A/EstimoteSDK(2413): com.estimote.sdk.service.BeaconService.stopScanning:285 BluetoothAdapter throws unexpected exception
A/EstimoteSDK(2413): java.lang.NullPointerException
A/EstimoteSDK(2413): at android.bluetooth.BluetoothAdapter$GattCallbackWrapper.stopLeScan(BluetoothAdapter.java:1596)
A/EstimoteSDK(2413): at android.bluetooth.BluetoothAdapter.stopLeScan(BluetoothAdapter.java:1540)
A/EstimoteSDK(2413): at com.estimote.sdk.service.BeaconService.stopScanning(BeaconService.java:283)
A/EstimoteSDK(2413): at com.estimote.sdk.service.BeaconService.access$700(BeaconService.java:60)
A/EstimoteSDK(2413): at com.estimote.sdk.service.BeaconService$1$1.run(BeaconService.java:545)
A/EstimoteSDK(2413): at android.os.Handler.handleCallback(Handler.java:733)
A/EstimoteSDK(2413): at android.os.Handler.dispatchMessage(Handler.java:95)
A/EstimoteSDK(2413): at android.os.Looper.loop(Looper.java:136)
A/EstimoteSDK(2413): at android.os.HandlerThread.run(HandlerThread.java:61)

  Resolution: turn on Airplane Mode on the device for a few seconds. If it does not help, please do try factory reset.

  This happens only when hundreds of Bluetooth Low Energy devices are around (hackathons, dev shops). Bluetooth library has low-level bug which activates only of there many many BLE devices around.

  This will not be seen by end users since they do not operate in the environment where there are many many Bluetooth devices.

  [Issue is already reported to Android](https://code.google.com/p/android/issues/detail?id=67272) and hopefully it will be fixed within next release.

  For more detailed info please see those [two StackOverflow](http://stackoverflow.com/questions/22048721/bluetooth-share-has-stopped-working-when-performing-lescan) [threads](http://stackoverflow.com/questions/22476951/bluetooth-share-has-stopped-alert-when-detecting-ibeacons-on-android).
  
  There is a workaround around this bug. Please read the [article](http://developer.radiusnetworks.com/2014/04/02/a-solution-for-android-bluetooth-crashes.html) and check out [Bluetooth crash resolver project on GitHub](https://github.com/RadiusNetworks/bluetooth-crash-resolver).

3. I did not find answer here. Where I can seek for help?

  You have three options:
   * file an issue on GitHub for [Estimote SDK for Android](https://github.com/Estimote/Android-SDK/issues) if it is highly technical
   * check our [Community Portal](https://community.estimote.com/hc/en-us) to get answers for most common questions related to our Hardware and Software, you can post questions there
   * ask a question on [StackOverflow.com](http://stackoverflow.com) with iBeacon, Estimote, Android tags


## Changelog ##

* 0.4.2 (June 24, 2014):
 - Fixes https://github.com/Estimote/Android-SDK/issues/55: it is safe to use library from remote process

* 0.4.1 (March 18, 2014)
 * CAN BREAK BUILD: MonitoringListener returns list of beacons the triggered enter region event (https://github.com/Estimote/Android-SDK/issues/18)
 * Better messaging when BeaconManager cannot start service to scan beacons (https://github.com/Estimote/Android-SDK/issues/25)
 * Fixed bug in SDK when other beacons are around (https://github.com/Estimote/Android-SDK/issues/27)
* 0.4 (February 17, 2014)
 * Introducing ability to change beacon's UUID, major, minor, broadcasting power, advertising interval (see BeaconConnection class).
 * Dropping Guava dependency.
* 0.3.1 (February 11, 2014)
 * Fixes bug when simulated beacons were not seen even when using Estimote's proximity UUID.
* 0.3 (February 11, 2014)
 * Background monitoring is more robust and using AlarmService to invoke scanning.
 * Default values for background monitoring were changed. Scanning is performed for 5 seconds and then service sleeps for 25 seconds. Those values can be changed with BeaconManager#setBackgroundScanPeriod.
 * Beacons reported in RangingListener#onBeaconsDiscovered are sorted by accuracy (estimated distance between device and beacon).
 * Bug fixes.
* 0.2 (January 7, 2014)
 * *IMPORTANT*: package changes BeaconService is now in `com.estimote.sdk.service service`. You need to update your `AndroidManifest.xml` service definition to `com.estimote.sdk.service.BeaconService`.
 * Support for monitoring regions in BeaconManager.
 * Region class: it is mandatory to provide region id in its constructor. This matches CLRegion/ESTBeaconRegion from iOS.
 * Beacon, Region classes now follow Java bean conventions (that is getXXX for accessing properties).
 * Debug logging is disabled by default. You can enable it via `com.estimote.sdk.utils.L#enableDebugLogging(boolean)`.

* 0.1 (December 9, 2013)
 * Initial version.

