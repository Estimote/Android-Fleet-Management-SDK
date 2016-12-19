# Estimote SDK for Android #

The Estimote SDK for Android is a library that allows interaction with [Estimote beacons & stickers](http://estimote.com/#jump-to-products). The SDK system works on Android 4.3 or above and requires device with Bluetooth Low Energy (SDK's min Android SDK version is 9).

It allows for:
- beacon ranging (scans beacons and optionally filters them by their properties)
- beacon monitoring (monitors regions for those devices that have entered/exited a region)
- nearables (aka stickers) discovery (see [quickstart](#quick-start-for-nearables-discovery))
- [Eddystone](https://developers.google.com/beacons) scanning (see [quickstart](#quick-start-for-eddystone))
- easy way to meet [all requirements for beacon detection](http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html) (runtime permissions, acquiring all rights),
- Estimote Proximity beacon & Location beacon characteristic reading and writing (proximity UUID, major & minor values, broadcasting power, advertising interval and many more)
- Collecting analytics data - from version 0.10.7 enabled by default.

Start with [Android tutorial for monitoring & ranging beacons](http://developer.estimote.com/android/tutorial/part-1-setting-up/).

Learn more:
 - [Comprehensive JavaDoc documentation](http://estimote.github.io/Android-SDK/JavaDocs/).
 - Play with [SDK Examples](https://github.com/Estimote/Android-SDK/tree/master/Demos) (includes scanning beacons, nearables, Eddystone beacons, connecting to Estimote beacons).
 - Download [Estimote app](https://play.google.com/store/apps/details?id=com.estimote.apps.main) from Play Store to see what SDK is capable of.
 - Check our [Estimote Forums](https://forums.estimote.com/c/android-sdk) where you can post your questions and get answers.
 - [Estimote Community Portal](http://community.estimote.com/hc/en-us)

## Installation

### Gradle via Maven Central

Estimote Android SDK is available on [Maven Central](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.estimote%22). Declare in your Gradle's `build.gradle` dependency to this library.

```gradle
dependencies {
  compile 'com.estimote:sdk:0.15.0@aar'
}
```

Initialize Estimote SDK in your Application class onCreate() method:

```java
//  App ID & App Token can be taken from App section of Estimote Cloud.
EstimoteSDK.initialize(applicationContext, appId, appToken);
// Optional, debug logging.
EstimoteSDK.enableDebugLogging(true);
```

### Manual installation

*Eclipse users:* Mark Murphy [on his blog explained](https://commonsware.com/blog/2014/07/03/consuming-aars-eclipse.html) how to use `aar` format in Eclipse.

1. Create `libs` directory inside your project and copy there [estimote-sdk.aar](https://github.com/Estimote/Android-SDK/blob/master/EstimoteSDK/estimote-sdk.aar).
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
    compile(name:'estimote-sdk', ext:'aar')
  }
```
4. Initialize Estimote SDK in your Application class onCreate() method - if you are using [Estimote Cloud](http://cloud.estimote.com):

  ```java
  //  App ID & App Token can be taken from App section of Estimote Cloud.
  EstimoteSDK.initialize(applicationContext, appId, appToken);
  // Optional, debug logging.
  EstimoteSDK.enableDebugLogging(true);
  ```

## Android 6.0 and runtime permissions

Depending on Android platform you need different permissions to be granted. It is recommended to implement future proof Android M runtime permissions model.
 
 In order to reliably detect beacons following conditions must be met:
  - Bluetooth permissions are granted (android.permission.BLUETOOTH and android.permission.BLUETOOTH_ADMIN). This is done automatically if you use Estimote SDK.
  - `BeaconService` is declared in `AndroidManifest.xml`. This is done automatically if you use Estimote SDK.
  - If running on Android M or later, Location Services must be turned on.
  -  If running on Android M or later and your app is targeting SDK < 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION`) must be granted for <b>background</b> beacon detection.
  - If running on Android M or later and your app is targeting SDK >= 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` must be granted.

Sounds difficult? No worries. From time to time SDK will put warning in device logs what is missing. You can use the `SystemRequirementsChecker#check` method to determine which requirements are not met for beacon detection.

 Use `SystemRequirementsChecker#checkWithDefaultDialogs` method in your activity for a convenient way to ask for all permissions and rights. It's all handled by the SDK, and should be of great help if you want to get up and running quickly.
 
## Android 7.0 and BLE scan restrictions
 
Since Nougat, every application is allowed to start/stop BLE scan maximum 5 times per 30s. Due to this restriction, if you set your *monitoring* period to be lower than 6s (for example `1000ms scan + 0ms wait`) our sdk will automatically set your scan time to be `6000ms`. You should still be getting onEnter/onExit results properly. We strongly recommend you to verify your monitoring periods according to Android N. 

This restriction does not affect *ranging* times - when running in foreground, your ranging will deliver constant results according to your declared period. The only change is that scan is running constantly throughout the whole ranging process, which will drain.

You can read more about the difference between *ranging* and *monitoring* in tutorials below.

## Tutorials

Android tutorial is available on [Estimote Developer Docs](http://developer.estimote.com/android/tutorial/part-1-setting-up/). Tutorial is divided into three parts:
 - [Part 1: Setting Up](http://developer.estimote.com/android/tutorial/part-1-setting-up/)
 - [Part 2: Background monitoring](http://developer.estimote.com/android/tutorial/part-2-background-monitoring/)
 - [Part 3: Ranging beacons](http://developer.estimote.com/android/tutorial/part-3-ranging-beacons/)

In addition, we suggest you to check our guides for using **Location Beacons** and **Proximity Beacons**:
 - [Scanning and monitoring](/DOC_monitoring_scanning.md)
 - [Beacon connection](/DOC_deviceConnection.md)
 - [Multiple advertisers in Location Beacons](/DOC_multiadvertisers.md)
 - [Using telemetry packets](/DOC_telemetry.md)
 - [Monitoring after system restart](/DOC_monitoring_after_restart.md)


### Quick start for nearables discovery

```java
  private BeaconManager beaconManager = new BeaconManager(context);
  private String scanId;

  // Should be invoked in #onCreate.
  beaconManager.setNearableListener(new BeaconManager.NearableListener() {
    @Override public void onNearablesDiscovered(List<Nearable> nearables) {
      Log.d(TAG, "Discovered nearables: " + nearables);
    }
  });

  // Should be invoked in #onStart.
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override public void onServiceReady() {
      scanId = beaconManager.startNearableDiscovery();
    }
  });

  // Should be invoked in #onStop.
  beaconManager.stopNearableDiscovery(scanId);

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

### Quick start for Secure UUID

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

3. Use `SecureRegion` instead of `Region` when starting ranging or monitoring.

  ```java
  // Initialise BeaconManager as before.
  // Find all *your* Secure UUID beacons in the vicinity.
  beaconManager.startRanging(new SecureRegion(“regionId”, null, null, null));

  // Remember that you can also range for other regions as well.
  beaconManager.startRanging(new Region(“otherRegion”, null, null, null);
  ```

### Quick start for Eddystone

[Eddystone](https://developers.google.com/beacons) is an open protocol BLE protocol from Google. Estimote Beacons can broadcast the Eddystone packet.

With Estimote SDK you can:
 - find nearby Eddystone beacons (`BeaconManager#startEddystoneScanning`)
 - configure Eddystone ralated properties:
   - URL property of `Eddystone-URL` (see `BeaconConnection#eddystoneUrl`)
   - namespace & instance properties of `Eddystone-UID` (see `BeaconConnection#eddystoneNamepsace`, `BeaconConnection#eddystoneInstance`)
 - configure broadcasting scheme of beacon to `Estimote Default`, `Eddystone-UID` or `Eddystone-URL` (see `BeaconConnection#broadcastingScheme`)

[SDK Examples](https://github.com/Estimote/Android-SDK/tree/master/Demos) contains Eddystone related samples.

Note that you can play with Estimote Beacons broadcasting the Eddystone packet and change their configuration via [Estimote app on Google Play](https://play.google.com/store/apps/details?id=com.estimote.apps.main).

In order to start playing with Eddystone you need to update firmware of your existing Estimote beacons to `3.1.1`. Easiest way is through [Estimote app on Google Play](https://play.google.com/store/apps/details?id=com.estimote.apps.main). Then you can change broadcasting scheme on your beacon to Eddystone-URL or Eddystone-UID.

Following code snippet shows you how you can start discovering nearby Estimote beacons broadcasting Eddystone packet.

```java
  private BeaconManager beaconManager = new BeaconManager(context);
  private String scanId;

  // Should be invoked in #onCreate.
  beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
    @Override public void onEddystonesFound(List<Eddystone> eddystones) {
      Log.d(TAG, "Nearby Eddystone beacons: " + eddystones);
    }
  });

  // Should be invoked in #onStart.
  beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
    @Override public void onServiceReady() {
      scanId = beaconManager.startEddystoneScanning();
    }
  });

  // Should be invoked in #onStop.
  beaconManager.stopEddystoneScanning(scanId);

  // When no longer needed. Should be invoked in #onDestroy.
  beaconManager.disconnect();
```

## FAQ

There is [Estimote SDK FAQ on wiki](https://github.com/Estimote/Android-SDK/wiki/FAQ).
There is also [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk) where you can post your questions.

## Changelog

To see what has changed in recent versions of Estimote SDK for Android, see the [CHANGELOG](CHANGELOG.md).

