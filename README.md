# Estimote Fleet Management SDK for Android

  * [Introduction](#introduction)
    + ["Can I use this SDK to detect beacons?"](#can-i-use-this-sdk-to-detect-beacons)
    + ["Do I need to build an app to configure my beacons?"](#do-i-need-to-build-an-app-to-configure-my-beacons)
  * [Requirements](#requirements)
  * [Installation](#installation)
    + [Connecting Fleet Management SDK to your Estimote Cloud account](#connecting-fleet-management-sdk-to-your-estimote-cloud-account)
  * [Bulk Updater](#bulk-updater)
  * [Configuring individual beacons](#configuring-individual-beacons)
  * [API documentation (AKA JavaDocs)](#api-documentation-aka-javadocs)
  * [Feedback, questions, issues](#feedback-questions-issues)
  * [Changelog](#changelog)

## Introduction

Estimote Fleet Management SDK allows you to **configure and update your Estimote Beacons via your own Android app**, for example:

 - enable Estimote Monitoring and Estimote Telemetry
 - enable iBeacon, and change its UUID, major, minor, transmit power
 - update beacon's firmware
 - etc.

You can [configure your beacons one by one](#configuring-individual-beacons), or use Estimote Cloud to queue "pending settings" on your beacons and apply these settings with the [Bulk Updater](#bulk-updater).

Integrating this SDK into your app means the users of your app can automatically propagate any configuration changes to your beacons. Say, you deployed the beacons, but the initial usage suggests you need to bump the transmit power up a notch, or maybe make the Telemetry advertise more frequently. With Bulk Updater integrated into the app, any user of the app in range of a "pending settings" beacon will apply the new settings.

(Settings that live entirely in Estimote Cloud, like the beacon's name, tags, and attachments, are always updated instantly, without the need to propagate settings to beacons.)

### "Can I use this SDK to detect beacons?"

Short version: no, use the [Proximity SDK](https://github.com/Estimote/Android-Proximity-SDK) instead.

Longer version: this SDK was previously known as "Estimote SDK", and it included APIs for detecting your beacons, which you could use to show notifications, etc. **These APIs are now deprecated and are no longer supported.** They have been replaced with the [Estimote Proximity SDK for Android](https://github.com/Estimote/Android-Proximity-SDK), powered by [Estimote Monitoring](https://community.estimote.com/hc/en-us/articles/360003252832-What-is-Estimote-Monitoring-).

You can, and are encouraged to, use the Fleet Management SDK alongside the Proximity SDK: Proximity SDK for driving the proximity-based events in your app, and Fleet Management SDK for remotely managing your beacons.

### "Do I need to build an app to configure my beacons?"

No, you can use our [Estimote Android app](https://play.google.com/store/apps/details?id=com.estimote.apps.main&hl=en) to change the most common settings, and to apply "pending settings" to individual beacons. Connecting to the beacon automatically applies the latest settings from Estimote Cloud.

If you have more Estimote devices, [Estimote Deployment app](https://itunes.apple.com/us/app/estimote-deployment/id1109375679?mt=8) can apply "pending settings" in bulk. At this time, it's available on iOS only.

## Requirements

Android 4.3 or later, and an Android device with Bluetooth Low Energy.

The minimum Android API level this SDK will run on is 9 (= Android 2.3), but the Bluetooth-detection features (which is most of them) won't work. You can handle this gracefully in your app by not using the fleet management features if you detect Android < 4.3, or no Bluetooth Low Energy available.

Bluetooth Low Energy scanning on Android also requires the app to obtain the location permissions from the user, and "location" must also be enabled system-wide. Chances are, if your app is using beacons (e.g., via the Estimote Proximity SDK), [you already have this permission](https://developer.estimote.com/proximity/android-tutorial/#request-location-permissions).

## Installation

This SDK is distributed via JCenter repository. Most of the time, you'll already have JCenter repo configured in your Android project, with these lines in the Project build.gradle:

```gradle
allprojects {
    repositories {
        jcenter()
        // ...
    }
}
```

If not, add `jcenter()` inside `repositories`, as shown above. Then, in the Module build.gradle, add:

```gradle
dependencies {
    implementation 'com.estimote:mgmtsdk:1.4.4'
    // if using an older version of Gradle, try "compile" instead of "implementation"
}
```

### Connecting Fleet Management SDK to your Estimote Cloud account

This SDK needs to be able to access your Estimote Cloud account in order to configure your beacons.

To do that, register your mobile app in Estimote Cloud in the "Mobile Apps" section. You'll get an App ID and App Token, which you can use to initialize the SDK:

```java
// do this before you use any of the fleet management features
EstimoteSDK.initialize(applicationContext, "my-app-bf6", "7bcabedcb4f...");
```

Your Estimote Cloud account needs to have a fleet management subscription. As of now, there's a free, indefinite trial available if you have less than 20 devices.

## Bulk Updater

This is how to set up the Bulk Updater: (we recommend doing this in an Application subclass)

```java
private BulkUpdater bulkUpdater;

@Override
protected void onCreate() {
    super.onCreate();

    Context context = getApplicationContext();
    this.bulkUpdater = new BulkUpdaterBuilder(context)
        .withFirmwareUpdate()
        .withCloudFetchInterval(1, TimeUnit.HOURS)
        .withTimeout(0)
        .withRetryCount(3)
        .build()
}
```

And this is how to use it:

```java
this.bulkUpdater.start(new BulkUpdater.BulkUpdaterCallback() {
    @Override
    public void onDeviceStatusChange(ConfigurableDevice device,
            BulkUpdater.Status newStatus, String message) {
        Log.d("BulkUpdater", device.deviceId + ": " + newStatus);
    }

    @Override
    public void onFinished(int updatedCount, int failedCount) {
        Log.d("BulkUpdater", "Finished. Updated: " +
                updatedCount + ", Failed: " + failedCount);
    }

    @Override
    public void onError(DeviceConnectionException e) {
        Log.d("BulkUpdater", "Error: " + e.getMessage());
    }
});
```

To stop, and clean up after the BulkUpdater, do:

```java
bulkUpdater.stop();
bulkUpdater.destroy();
```

## Configuring individual beacons

If you want to individually configure a beacon, you'll need to connect to it first: [Connecting to beacons](https://github.com/Estimote/Android-Fleet-Management-SDK/blob/master/Docs/DOC_deviceConnection.md).

Once connected, you can read and write settings, as well as update the beacon's firmware: [Basic operations on connected device](https://github.com/Estimote/Android-Fleet-Management-SDK/blob/master/Docs/DOC_deviceConnection.md#basic-operations-on-connected-device) and [Advanced operations on connected device](https://github.com/Estimote/Android-Fleet-Management-SDK/blob/master/Docs/DOC_deviceConnection.md#advanced-operations-on-connected-device).

## API documentation (AKA JavaDocs)

… is available here:

<http://estimote.github.io/Android-Fleet-Management-SDK/JavaDocs/>

## Feedback, questions, issues

Post your questions and feedback to [Estimote Forums](https://forums.estimote.com), we'd love to hear from you!

Report issues on the [issues page](https://github.com/Estimote/Android-Fleet-Management-SDK/issues).

## Changelog

See [CHANGELOG.md](CHANGELOG.md).
