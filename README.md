# Estimote SDK for Android #

## Overview ##

Estimote SDK for Android is a library to interact with iBeacons. SDK requires Android 4.3 or above and works on devices with Bluetooth Low Energy.

It mimics [Estimote SDK for iOS](https://github.com/Estimote/iOS-SDK). All name conventions come from iBeacon library from iOS and from Estimote iOS library.

It allows to:
- range beacons (scan beacons and optionally filter them by their values),
- monitor beacons (not implemented yet, on roadmap),
- read beacon's characteristics (not implemented yet, on roadmap).

[Current JavaDoc documentation](http://estimote.github.io/Android-SDK/JavaDocs/)

**What is ranging?**

Ranging allows apps to know the relative distance between device and beacons. This can be very valuable. Consider example of an indoor location app of department store. App can know in which department (such as shoes, suits, accessories) user is close-by. Information about this proximity can be use by the app to show fitting guides or offer today's discounts.

Because bluetooth low energy ranging depends on detecting radio signals, results will vary depending on placements of Estimote beacon and user's device:
- device being in user's hands, in a bag, or user's jean's pocket will produce different results,
- if there is clear line of sight between device and beacon gives different results than having beacon hidden between shelves.

Good way to have consistent ranging results is to use it when app is in foreground which means that user holds device in hands.

Apps can use `startRanging` method of `BeaconsManager` class to determine relative proximity of beacons in the region and can be updated when this distance changes. Ranging updates comes every second to listeners registerd with `setRangingListener` method of `BeaconsManger` class. Update contains list of currently found beacons. If beacon goes out of range, it will not be anymore on this list.


## Installation ##

1. Copy [estimote-sdk-preview.jar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk-preview.jar) along with [guava-15.0.jar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/guava-15.0.jar) to your `libs` directory.
2. Add following permissions and service declaration to your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.BLUETOOTH"/>
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN"/>
```

```xml
<service android:name="com.estimote.sdk.BeaconService"
         android:exported="false"/>
```

## Usage ##

Example distance app is located in [DistanceDemo](https://github.com/Estimote/Android-SDK/tree/master/DistanceDemo) directory.

```java
  private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
  private static final Region ALL_ESTIMOTE_BEACONS = new Region(ESTIMOTE_PROXIMITY_UUID, null, null)

  // Should be invoked in #onCreate.
  BeaconManager beaconManager = new BeaconManager(context);
  beaconManager.setRangingListener(new BeaconManager.RangingListener() {
    @Override public void onBeaconsDiscovered(Region region, final List beacons) {
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
    beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
  } catch (RemoteException e) {
    Log.e(TAG, "Cannot stop but it does not matter now", e);
  }

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

## Changelog ##

* 0.1 (December 9, 2013)
 * Initial version.

