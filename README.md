# Estimote SDK for Android #

## Introduction

This Android SDK can be used to update the settings of multiple Estimote beacons at once. You no longer need to connect to each beacon individually. Instead, you use Estimote Cloud to queue ‘pending settings’ on your beacons. Then, your Android app equipped with this SDK (see [Bulk Updater section](#bulk-updater)) can propagate those settings the moment it encounters the beacons. This also means that, once you deploy the beacons, users of your app can propagate the settings by simply being around the beacons. 

With this SDK, you can:
 - Enable or disable packets (e.g. Estimote Monitoring, iBeacon, Eddystone-UID)
 - Modify the settings of individual beacons (e.g. increase the broadcasting power, decrease the advertising interval, modify Eddystone-URL’s link)
 - Update Estimote beacon firmware

Keep in mind that tags & attachments from [Estimote Proximity SDK](https://github.com/Estimote/Android-Proximity-SDK) are updated instantly, without the need to propagate settings to beacons.

**All the proximity monitoring features of this SDK have been deprecated and are no longer supported.** Instead, we strongly recommend [Estimote Proximity SDK for Android](https://github.com/Estimote/Android-Proximity-SDK) powered by Estimote Monitoring. On top of Proximity SDK, use this Android SDK for beacon fleet management.

If you simply need to change the settings (or apply ‘pending settings’) of the nearby Estimote beacons, get [Estimote Android app](https://play.google.com/store/apps/details?id=com.estimote.apps.main&hl=en). For a single beacon or a few of them, this will be the fastest method.

If you have more Estimote devices, [Estimote Deployment app](https://itunes.apple.com/us/app/estimote-deployment/id1109375679?mt=8) will be a better choice. Use it to propagate settings to multiple beacons at once. At this point, it’s available on iOS only. 

To learn more about this Android SDK, review the [Java documentation](http://estimote.github.io/Android-SDK/JavaDocs/) and check the [Developer Portal tutorial](https://developer.estimote.com/android/tutorial/part-1-setting-up/). Visit also [Estimote Cloud API docs](https://cloud.estimote.com/docs/).


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
  compile 'com.estimote:sdk:1.4.5'
}
```

Still using `Eclipse`? [Here is how](Docs/manual_installation.md) to import our sdk to your project.

## Initializing Estimote SDK

Initialize Estimote SDK in your Application class onCreate() method:

```java
//  To get your AppId and AppToken you need to create new application in Estimote Cloud.
EstimoteSDK.initialize(applicationContext, appId, appToken);
// Optional, debug logging.
EstimoteSDK.enableDebugLogging(true);
```

## Requirements

The SDK system works on Android 4.3 or above and requires a device with Bluetooth Low Energy (Estimote SDK's minimum Android SDK version is 9).

## [Bulk Updater](#bulk-updater)

With this feature you can:
 - Enable or disable packets (e.g. iBeacon, Eddystone-UID)
 - Modify the settings of individual beacons (e.g. increase the broadcasting power, decrease the advertising interval, modify Eddystone-URL’s link)
 - Update Estimote beacon firmware

Check [this tutorial](https://github.com/Estimote/Android-SDK/blob/master/Docs/DOC_deviceConnection.md#advanced-operations-on-connected-device) for the details on how to bulk update your beacons with this SDK.

## Configuring individual beacons

### Connecting to beacons in Android SDK

In order to modify any settings of a beacon, you’ll need to connect to it first. If a beacon is set to Deployed & Protected [access mode](https://community.estimote.com/hc/en-us/articles/115000221671-What-is-Access-Mode-How-to-enable-it-), you have to be the beacon's owner in Estimote Cloud to modify any settings. Every attempt to connect with a device not logged in to your Estimote account will fail. Enable Development access mode to allow your co-workers to also edit beacon's settings.

Read the tutorial on how to connect to Estimote Beacons with this SDK [here](https://github.com/Estimote/Android-SDK/blob/master/Docs/DOC_deviceConnection.md#discovering-configurable-devices).

### Basic operations on connected devices

Once you establish a connection with a beacon, you can perform various actions that we already outlined in [Bulk Updater section](#bulk-updater). This SDK can both read (return the current values) and write settings (set new values). For the details on how to run both, follow [this tutorial](https://github.com/Estimote/Android-SDK/blob/master/Docs/DOC_deviceConnection.md#basic-operations-on-connected-device).

## Your feedback and questions
At Estimote, we're massive believers in feedback! Here are some common ways to share your thoughts with us:
  - Posting issue/question/enhancement on our [issues page](https://github.com/Estimote/Android-SDK/issues).
  - Asking our community managers on our [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk).

## Changelog
To see what has changed in recent versions of Estimote SDK for Android, see the [CHANGELOG](CHANGELOG.md).
