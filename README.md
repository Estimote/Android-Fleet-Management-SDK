# Estimote SDK for Android #

## Overview ##

The Estimote SDK for Android is a library that allows interaction with Estimote beacons. The SDK system requirements are Android 4.3 or above and Bluetooth Low Energy.

It allows for:
- beacon ranging (scans beacons and optionally filters them by their properties)
- beacon monitoring (monitors regions for those devices that have entered/exited a region)
- beacon characteristic reading and writing (proximity UUID, major & minor values, broadcasting power, advertising interval), see [BeaconConnection] (http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/connection/BeaconConnection.html) class and [demos](https://github.com/Estimote/Android-SDK/tree/master/Demos) in the SDK

Learn more: 
 - [Comprehensive JavaDoc documentation](http://estimote.github.io/Android-SDK/JavaDocs/).
 - Play with [SDK Examples](https://github.com/Estimote/Android-SDK/tree/master/Demos).
 - Download [Estimote app](https://play.google.com/store/apps/details?id=com.estimote.apps.main) from Play Store to see what SDK is capable of.
 - Check our [Estimote Forums](https://forums.estimote.com/c/android-sdk) where you can post your questions and get answers.
 - [Estimote Community Portal](http://community.estimote.com/hc/en-us)

**What is ranging?**

Ranging allows apps to know the relative distance between a device and beacons. This can be very valuable – consider for example an indoor location app of a department store. The app can determine which department (such as footwear, clothing, accessories etc) you're closest by. Information about this proximity can be used within the app to show fitting guides or offer discounts.

As Bluetooth Low Energy ranging depends on detecting radio signals, results will vary depending on the placement of Estimote beacons and whether a user's mobile device is in-hand, in a bag or a pocket. Clear line of sight between a mobile device and a beacon will yield better results so it is recommended that Estimote beacons not be hidden between shelves.

To enjoy consistent ranging it is good practice to use the app in the foreground while the user is holding the device in-hand (which means the app is on and running).

Apps can use the `startRanging` method of the `BeaconManager` class to determine relative proximity of beacons in the region and can be updated when this distance changes. Ranging updates come every second to listeners registered with the `setRangingListener` method of the `BeaconManager` class. Ranging updates contain a list of currently found beacons. If a beacon goes out of range it will not be presented on this list.

Ranging is designed to be used for apps running in the foreground.

**What is monitoring?**

Region monitoring is a term used to describe a Bluetooth device's usage and detect when a user is in the vicinity of beacons. You can use this functionality to show alerts or provide contextually aware information as a user enters or exits a beacon region. Beacon regions are defined by the following beacon properties:

- proximity UUID: 128-bit unique identifier,
- major: 16-bit unsigned integer to differentiate between beacons within the same proximity UUID,
- minor: 16-bit unsigned integer to differentiate between beacons with the same proximity UUID and major value.

Note that all of these values are optional, meaning that a single region can encompass multiple beacons — which creates interesting use cases. Consider for example a department store that is identified by a particular proximity UUID and major value. Different sections of the store are differentiated further by a different minor value. An app can monitor region defined by their proximity UUID and major value to provide location-relevant information by distinguishing minor values.

Apps can use the `startMonitoring` method of the `BeaconManager` class to start monitoring regions. Monitoring updates come to listeners registered with the `setMonitoringListener` method of the `BeaconsManager` class.

Monitoring is designed to perform periodic scans in the background. By default it scans for 5 seconds and sleeps for 25 seconds. This means that it can take by default up to 30 seconds to detect entering or exiting a region. Default behaviour can be changed via `BeaconManager#setBackgroundScanPeriod`.

## Installation ##

*Note*: SDK version 0.5 switched from jar distribution to [aar archive](http://tools.android.com/tech-docs/new-build-system/aar-format). There is no longer need to change your `AndroidManifest.xml` as it is being done automatically.

*Eclipse users:* Mark Murphy [on his blog explained](https://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html) how to use `aar` format in Eclipse.

*Note about AAR Manifest Merger*: SDK's `AndroidManifest.xml` will be automatically merged into your app. Right now it declared min SDK level 18. You can override this declaration as [described here](http://tools.android.com/tech-docs/new-build-system/user-guide/manifest-merger#TOC-tools:overrideLibrary-marker).

1. Create `libs` directory inside your project and copy there [estimote-sdk-preview.aar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk-preview.aar).
2. In your `build.gradle` add `flatDir` entry to your repositories

  ```groovy
  repositories {
    mavenCentral()
      flatDir {
        dirs 'libs'
      }
  }
```
3. Add dependency to Estimote SDK. All needed permissions (`BLUETOOTH`, `BLUETOOTH_ADMIN` and `INTERNET`) and services will be merged from SDK's `AndroidManifest.xml` to your application's `AndroidManifest.xml`.

  ```groovy
  dependencies {
    compile(name:'estimote-sdk-preview', ext:'aar')
  }
```
4. Initialize Estimote SDK in your Application class if you are using [Estimote Cloud](http://cloud.estimote.com).

  ```java
  //  App ID & App Token can be taken from App section of Estimote Cloud.
  EstimoteSDK.initialize(applicationContext, appId, appToken);
  // Optional, debug logging.
  EstimoteSDK.enableDebugLogging(true);
  ```

## Usage and demos ##

Demos are located in [Demos](https://github.com/Estimote/Android-SDK/tree/master/Demos) directory. You can easily build it with [Gradle](http://www.gradle.org/) by typing `gradlew installDebug` (or `gradlew.bat installDebug` on Windows) in terminal when your device is connected to computer. If you use [Android Studio](http://developer.android.com/tools/studio/index.html) you can just simply open `build.gradle`.

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

There is [Estimote SDK FAQ on wiki](https://github.com/Estimote/Android-SDK/wiki/FAQ).
There is also [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk) where you can post your questions.

## Changelog ##

To see what has changed in recent versions of Estimote SDK for Android, see the [CHANGELOG](CHANGELOG.md).

