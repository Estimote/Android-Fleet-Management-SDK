# Estimote SDK for Android #

The Estimote SDK for Android is a library that allows interaction with [Estimote beacons & stickers](http://estimote.com/#jump-to-products). The SDK system works on Android 4.3 or above and requires a device with Bluetooth Low Energy (Estimote SDK's minimum Android SDK version is 9).

It allows for:
- Beacon ranging (scans beacons and optionally filters them by their properties)
- Beacon monitoring (monitors regions for devices that have entered/exited a region)
- Nearables (a.k.a. stickers), [Eddystone](https://developers.google.com/beacons), Estimote Telemetry, Estimote Location and Mirror discovery
- Easily meeting [all the requirements for beacon detection](http://estimote.github.io/Android-SDK/JavaDocs/com/estimote/sdk/SystemRequirementsChecker.html) (runtime permissions, acquiring all rights),
- Estimote beacons management - changing proximity UUID, major & minor values, broadcasting power, advertising interval and more!
- Collecting analytics data.

## Installation

Estimote SDK for Android is distributed via JCenter repository. To be able to grab necessary artifacts, ensure you have JCenter repository configured. Usually, it's done by the following lines in top-level build.gradle of your project:

```gradle
allprojects {
    repositories {
        jcenter()
        (all other repositories you are using goes here)
    }
}
```

Once you have JCenter configured, add this line to your `build.gradle` file:

```gradle
dependencies {
  compile 'com.estimote:sdk:1.3.0'
}
```
*Important: If you are migrating from the old sdk (0.16.0), [here are some handy tips](Docs/switching_to_1.0.0.md) to help you adopt to breaking changes.*

Still using `Eclipse`? [Here is how](Docs/manual_installation.md) to import our sdk to your project.

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

**Foreground** - Scan is being scheduled using Estimote's internal scheduling mechanism.
Results are delivered precisely at given periods and readings are much more responsive than in the background.
This does not allow for scanning when the app is killed or in the background - there is no possibility to invoke periodical scans outside of an app.
Use monitoring instead if you need this kind of behavior. You can adjust your foreground scanning interval like this:
```java
beaconManager.setForegroundScanPeriods(scanTime, waitTime)
```

**Background** - Scan is being scheduled with Android's alarm mechanism which allows to scan when the app is in the background/killed.
One potential drawback of this mechanism is that results may be delivered with delay - system alarms are inexact.
On the other hand, this allows to reduce battery drain and notify your app when it is in background.
You can adjust your background scanning interval like this:
```java
beaconManager.setBackgroundScanPeriods(scanTime, waitTime)
```

**Single packet** - Packet received from scanned device. It is the most basic piece of data that can be acquired via discovery.
Each time the device advertises a new packet (depends on the device’s advertising interval and TX power) the listener should be notified about this fact.
There are some cases, when the data is too large to fit into one BLE packet, so it is splitted into two or more frames and advertised separately.
The discovery time is longer, because we need to wait for two (or more) packets in order to merge them.
Telemetry readings are a good example of this.

**Region** - Defines a zone for “geo-fencing”-like behavior. This is an abstraction level atop of single packets.
You can define a region using the iBeacon protocol, or any other set of rules and predicates - like a set of Configurable devices, or a bunch of Mirror devices. The region is used mostly to catch enter/exit events without focusing on constantly updating distance - except Ranging.

**Discovery** - constant scan in foreground for packets of a given type.

**Ranging** - constant scan in foreground for devices in a given Region.

**Monitoring** - background scanning for enter/exit events in a given Region.

## Tutorials

*If you are in hurry, you can check our [ultra quick guide with ready-to-copy snippets](Docs/quick_snippets.md)*

Otherwise, we encourage you to dig deeper into our available tutorials:

The Android tutorial is available under our [Estimote Developer Docs](http://developer.estimote.com/android/tutorial/part-1-setting-up/). The tutorial is divided into three parts:
 - [Part 1: Setting Up](http://developer.estimote.com/android/tutorial/part-1-setting-up/)
 - [Part 2: Background monitoring](http://developer.estimote.com/android/tutorial/part-2-background-monitoring/)
 - [Part 3: Ranging beacons](http://developer.estimote.com/android/tutorial/part-3-ranging-beacons/)

In addition, we suggest you check out our guides for using **Location Beacons** and **Proximity Beacons**:
 - [Ranging and monitoring](/Docs/DOC_monitoring_scanning.md)
 - [Beacon connection](/Docs/DOC_deviceConnection.md)
 - [Multiple advertisers in Location Beacons](/Docs/DOC_multiadvertisers.md)
 - [Using telemetry packets](/Docs/DOC_telemetry.md)
 - [Monitoring after system restart](/Docs/DOC_monitoring_after_restart.md)

If you need to know absolutely everything, take a look at our [JAVADOC](http://estimote.github.io/Android-SDK/JavaDocs/).

## Android 6.0 and runtime permissions

Depending on the Android platform, you need to grant different permissions to the completed app. It is recommended to implement the future proof Android M runtime permissions model.

 The following conditions must be met in order to reliably detect beacons:
  - Bluetooth permissions are granted (android.permission.BLUETOOTH and android.permission.BLUETOOTH_ADMIN). This is done automatically if you use the Estimote SDK.
  - `BeaconService` is declared in `AndroidManifest.xml`. This is done automatically if you use the Estimote SDK.
  - If running on Android M or later, Location Services must be turned on.
  - If running on Android M or later and your app is targeting SDK < 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION`) must be granted for <b>background</b> beacon detection.
  - If running on Android M or later and your app is targeting SDK >= 23 (M), any location permission (`ACCESS_COARSE_LOCATION` or `ACCESS_FINE_LOCATION` must be granted.

Sounds difficult? No worries. From time to time the SDK will put a warning in the device logs, specifying what's missing.
You can use the `SystemRequirementsChecker#check` method to determine which requirements are not met for beacon detection.
Use the `SystemRequirementsChecker#checkWithDefaultDialogs` method in your activity for a convenient way to ask for all permissions and rights.
It's all handled by the SDK, and should be of great help if you want to get up and running quickly.

## Android 7.0 and BLE scan restrictions

Since Nougat, every application is allowed to start/stop BLE scan a maximum of 5 times per 30s. Due to this restriction, if you set your *monitoring* period to less than 6s (for example `1000ms scan + 0ms wait`) our SDK will automatically set your scan time to `6000ms`. You should still be getting onEnter/onExit results properly. We strongly recommend you verify your monitoring periods according to Android N.

This restriction does not affect *ranging* times - when running in foreground, your ranging will deliver constant results according to your declared period. The only change is that scan is running constantly throughout the whole ranging process, which will drain.

You can read more about the differences between *ranging* and *monitoring* in the tutorials linked to earlier.

## Your feedback and questions
At Estimote we're massive believers in feedback! Here are some common ways to share your thoughts with us:
  - Posting issue/question/enhancement on our [issues page](https://github.com/Estimote/Android-SDK/issues).
  - Asking our community managers on our [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk).

## Changelog
To see what has changed in recent versions of Estimote SDK for Android, see the [CHANGELOG](CHANGELOG.md).
