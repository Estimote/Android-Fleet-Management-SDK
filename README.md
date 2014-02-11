# Estimote SDK for Android #

## Overview ##

Estimote SDK for Android is a library to allow interaction with iBeacons. The SDK system requirements are Android 4.3 or above and Bluetooth Low Energy.

It mimics [Estimote SDK for iOS](https://github.com/Estimote/iOS-SDK). All naming conventions come from the iBeacon library for iOS and the Estimote iOS library.

It allows for:
- beacon ranging (scan beacons and optionally filters them by their values)
- beacon monitoring (monitors regions for those devices that have entered/exited a region)
- beacon characteristic probing (to be implemented)

[Current JavaDoc documentation](http://estimote.github.io/Android-SDK/JavaDocs/)

**What is ranging?**

Ranging allows apps to know the relative distance between a device and beacons. This can be very valuable. Consider for example of an indoor location app of department store. The app can determine which department (such footwear, clothing, accessories etc) is closest by. Information about this proximity can be employed by the app to show fitting guides or offer discounts.

As Bluetooth Low Energy ranging depends on detecting radio signals, results will vary depending on the placement of Estimote beacons and whether a user's mobile device is in-hand, in a bag or a pocket. Clear line of sight between a mobile device and a beacon will yield better results so it is recommended that Estimote beacons not be hidden between shelves.

To enjoy consistent ranging it is good practice to use the app in the foreground while the user is holding the device in-hand (which means the app is on and running).

Apps can use `startRanging` method of `BeaconManager` class to determine relative proximity of beacons in the region and can be updated when this distance changes. Ranging updates come every second to listeners registered with `setRangingListener` method of `BeaconManager` class. Ranging updates contain a list of currently found beacons. If a beacon goes out of range it will not be presented on this list.

**What is monitoring?**

Region monitoring is a term used to describe a Bluetooth device's usage and  detect when a user is in the vicinity of beacons. You can use this functionality to show alerts or provide contextual aware information as a user enters or exits  a beacon's region. Beacon's regions are defined by beacon's values:

- proximity UUID: 128-bit unique identifier,
- major: 16-bit unsigned integer to differentiate between beacons within the same proximity UUID,
- minor: 16-bit unsigned integer to differentiate between beacons with the same proximity UUID and major value.

Note that all of those values are optional. That means that single region can contain multiple beacons which creates interesting use cases. Consider for example a department store that is identified by a particular proximity UUID and major value. Different sections of the store are differentiated further by a different minor value. An app can monitor region defined by their proximity UUID and major value to provide location-relevant information by distinguishing minor values.

Apps can use `startMonitoring` method of `BeaconManager` class to start monitoring regions. Monitoring updates come to listeners registered with `setMonitoringListener` method of `BeaconsManager` class.

## Installation ##

1. Copy [estimote-sdk-preview.jar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk-preview.jar) along with [guava-15.0.jar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/guava-15.0.jar) to your `libs` directory.
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

Demos are located in [Demos](https://github.com/Estimote/Android-SDK/tree/master/Demos) directory. You can easily build it with [Gradle](http://www.gradle.org/) by typing `gradlew installDebug` in terminal when your device is connected to computer.

Demos include samples for ranging beacons, monitoring beacons are calculating distance between beacon and the device.

Quick start with ranging:

```java
  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
  private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);

  // Should be invoked in #onCreate.
  BeaconManager beaconManager = new BeaconManager(context);
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

## Changelog ##

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

