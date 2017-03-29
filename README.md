# Estimote SDK for Android #

The Estimote SDK for Android is a library that allows interaction with [Estimote beacons & stickers](http://estimote.com/#jump-to-products). The SDK system works on Android 4.3 or above and requires device with Bluetooth Low Energy (SDK's min Android SDK version is 9).

It allows for:
- Beacon ranging (scans beacons and optionally filters them by their properties)
- Beacon monitoring (monitors regions for those devices that have entered/exited a region)
- Nearables (aka stickers) discovery (see [quickstart](#quick-start-for-nearables-discovery))
- [Eddystone](https://developers.google.com/beacons) discovery (see [quickstart](#quick-start-for-eddystone))
- Easy way to meet [all requirements for beacon detection](http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html) (runtime permissions, acquiring all rights),
- Estimote beacons management - changing proximity UUID, major & minor values, broadcasting power, advertising interval and many more!
- Collecting analytics data

## Installation

Add this line to your `build.gradle` file:

```gradle
dependencies {
  compile 'com.estimote:sdk:1.0.0@aar'
}
```
*Important: If you are migrating from the old sdk (0.16.0), [here are some handy tips](LINK HERE) to help you adopt to breaking changes.*

Still using `Eclipse`? [Here is how](LINK HERE) to import our sdk to your project.

## Initializing Estimote SDK

Initialize Estimote SDK in your Application class onCreate() method:

```java
//  To get your AppId and AppToken you need to create new application in Estimote Cloud.
EstimoteSDK.initialize(applicationContext, appId, appToken);
// Optional, debug logging.
EstimoteSDK.enableDebugLogging(true);
```

 ## Basic concepts

|    | Scan type | Scan result | Return type |
| ------------- | ------------- | ------------- | ------------- |
| Discovery | foreground | single packets |  ConfigurableDevice, Eddystone, EstimoteTelemetry, EstimoteLocation, Nearable |
| Ranging | foreground | region | BeaconRegion, MirrorRegion |
| Monitoring | background | region | BeaconRegion, MirrorRegion   |

**Foreground** - Scan is being scheduled with use of internal Estimote scheduling mechanism.
Results are delivered precisely at given periods and readings are much more responsive than in background.
This does not allow for scanning when app is killed or in background - there is no possibility to invoke periodical scans outside of an app.
Use monitoring instead if you need this kind of behaviour. You can adjust your foreground scanning interval like this:
```java
beaconManager.setForegroundScanPeriods(scanTime, waitTime)
```

**Background** - Scan is being scheduled with Android alarm mechanism which allows to scan when app is in background/killed.
Potential drawback of this mechanism is that results may be delivered with delay - system alarms are inexact.
Although this allows to reduce battery drain and notify your app when it is in background.
You can adjust your background scanning interval like this:
```java
beaconManager.setBackgroundScanPeriods(scanTime, waitTime)
```

**Single packet** - Received packet from scanned device. It is the most basic data that can be acquired via discovery.
Each time the device advertises new packet (depends on device’s advertising interval and TX power) the listener should be notified about this fact.
There are some cases, when data is too large to fit into one BLE packet, so it is splitted into two or more frames and advertised separately.
The discovery time is longer, because we need to wait for two (or more) packets in order to merge them.
Telemetry readings are a good example for this case.

**Region** - Defines zone for “geo-fencing” like behaviour. This is an abstraction level a top of single packets.
You can define region using iBeacons, or any other set of rules and predicates - like a set of Configurable devices,
or bunch of Mirror devices. The region is used mostly to catch enter/exit events without focusing on constantly updating distance - except Ranging.

**Discovery** - constant scan in foreground for packets of given type.

**Ranging** - constant scan in foreground for devices in given Region.

**Monitoring** - background scanning for enter/exit events in given Region.

## Tutorials

*If you are in hurry, you can check our [ultra quick guide with ready-to-copy snippets](LINK HERE)*

Otherwise, we encourage you to dig deeper into our available tutorials:

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

If you need to know absolutely everything, take a look at our [JAVADOC](http://estimote.github.io/Android-SDK/JavaDocs/).

## Android 6.0 and runtime permissions

Depending on Android platform you need different permissions to be granted. It is recommended to implement future proof Android M runtime permissions model.

 In order to reliably detect beacons following conditions must be met:
  - Bluetooth permissions are granted (android.permission.BLUETOOTH and android.permission.BLUETOOTH_ADMIN). This is done automatically if you use Estimote SDK.
  - `BeaconService` is declared in `AndroidManifest.xml`. This is done automatically if you use Estimote SDK.
  - If running on Android M or later, Location Services must be turned on.
  -  If running on Android M or later and your app is targeting SDK < 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION`) must be granted for <b>background</b> beacon detection.
  - If running on Android M or later and your app is targeting SDK >= 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` must be granted.

Sounds difficult? No worries. From time to time SDK will put warning in device logs what is missing.
You can use the `SystemRequirementsChecker#check` method to determine which requirements are not met for beacon detection.
Use `SystemRequirementsChecker#checkWithDefaultDialogs` method in your activity for a convenient way to ask for all permissions and rights.
It's all handled by the SDK, and should be of great help if you want to get up and running quickly.

## Android 7.0 and BLE scan restrictions

Since Nougat, every application is allowed to start/stop BLE scan maximum 5 times per 30s. Due to this restriction, if you set your *monitoring* period to be lower than 6s (for example `1000ms scan + 0ms wait`) our sdk will automatically set your scan time to be `6000ms`. You should still be getting onEnter/onExit results properly. We strongly recommend you to verify your monitoring periods according to Android N.

This restriction does not affect *ranging* times - when running in foreground, your ranging will deliver constant results according to your declared period. The only change is that scan is running constantly throughout the whole ranging process, which will drain.

You can read more about the difference between *ranging* and *monitoring* in tutorials below.

## Your feedback and questions
At estimote we're massive believers in feedback! Here are common ways to share your thoughts with us:
  - Posting issue/question/enhancement at our [issues page](https://github.com/Estimote/Android-SDK/issues).
  - Asking our community managers at our [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk).

## Changelog
To see what has changed in recent versions of Estimote SDK for Android, see the [CHANGELOG](CHANGELOG.md).

