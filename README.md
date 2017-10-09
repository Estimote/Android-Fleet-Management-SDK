# Estimote Proximity SDK for Android 

Estimote Proximity SDK aims to provide a simple way for apps to react to physical context by reading signals from Estimote Beacons.

Why should you use it?

1. Reliability. It's built upon Estimote Monitoring, Estimote's algorithm for reliable enter/exit reporting.
2. No need to operate on abstract identifiers, or Proximity UUID, Major, Minor triplets. Estimote Proximity SDK lets you define zones by setting predicates for human-readable JSONs.
3. You can define multiple zones for a single beacon, i.e. at different ranges.
4. Cloud-backed grouping. When you change your mind and want to replace one beacon with another, all you need to do is reassign JSON attachments in Estimote Cloud. You don't even need to connect to the beacon!

## Requirements

- One or more [Estimote Proximity or Location Beacons](https://estimote.com/products/) with enabled `Estimote Location` packet advertising. 
- An Android device with Bluetooth Low Energy support. We suggest using Android Lollipop or newer. 

## Installation

Add this line to your `build.gradle` file:
```Gradle
compile 'com.estimote:proximity-sdk:0.1.0-alpha.2'
```
Note: this is a pre-release version of Estimote Proximity SDK for Android.

## Attachment-based identification

Details of each of your Estimote devices are available in Estimote Cloud. Each device has a unique identifier, but remembering it and using it for every one of your devices can be challenging. This is why Estimote Proximity SDK uses attachment-based identification.

Each device has an associated JSON. When the SDK detects a proximity change of a device, it checks the device's attachment JSON to see which registered rule should be applied.

During the pre-release stage of Estimote Proximity SDK, attachment JSONs are encoded in tags. The convention for a tag-encoded attachment is

```
{
    "attachment" : {
        // Attachment JSON goes here.
        // You can put here any JSON you wish to use in your apps.
    }
}
```

## 0. Setting up attachments in cloud 
1. Go to https://cloud.estimote.com/#/
2. Click on the beacon you want to configure
3. Click Edit settings button
4. Click Tags field
5. Click Create New Tag button
6. Paste in the JSON with attachment that's going to represent your beacon
7. Click Save changes
Tags are Cloud-only settings — no additional connecting to the beacons with the Estimote app is required.

![Cloud attachments](/images/adding_attachment_json_tag.png)

## 1. Build proximity observer
The `ProximityObserver` is the main object for doing proximity observations. Build it using `ProximityObserverFactory` - and don't forget to put your cloud credentials!

```Kotlin
val cloudCredentials = EstimoteCloudCredentials(YOUR_APP_ID_HERE , YOUR_APP_TOKEN_HERE)
val proximityObserver = ProximityObserverFactory().create(applicationContext, cloudCredentials)
```

## 2. Define proximity rules
Now the fun part - create your own proximity rules using `proximityObserver.ruleBuilder()`

```Kotlin
val rule1 = proximityObserver.ruleBuilder()
                .forAttachmentKey("venue")
                .withOnEnterAction{/* Do something here */}
                .withOnExitAction{/* Do something here */}
                .withOnChangeAction{/* Do something here */}
                .withDesiredMeanTriggerDistance(2.0)
                .create()
```
- **attachmentKey** - the key you want to trigger actions for. 
- **onEnterAction** - action that will be triggered when the user enters the zone defined by given key. 
- **onExitAction** - action that will be triggered when user exits the zone defined by given key. 
- **onChangeAction** - triggers when there is a change in proximity attachments of given key. If the zone consists of more than a one beacon, this will help tracking the ones that are nearby, while still remaining one `onEnter` and `onExit` event. 
- **desiredMeanTriggerDistance** - the distance at which actions will be invoked. Notice that due to the nature of Bluetooth Low Energy, it is "desired" and not "exact". We are constantly improving the precision.

## 3. Start proximity observation
When you are done defining your rules, you will need to start the observation process:

```Kotlin
 val observationHandler = proximityObserver
                .addProximityRules(rule1, rule2, rule3)
                .withBalancedPowerMode()
                .withOnErrorAction{/* Do something here */}
                .startWithForegroundScanner(notification)
```
- **addProximityRules** - adds your pre-defined rules to `ProximityObserver`
- **lowLatencyPowerMode** - The most reliable mode, but may drain battery a lot. 
- **balancedPowerMode** - Balance between scan reliability and battery drainage. 
- **lowPowerMode** - Battery efficient mode, but not that reliable.
- **onErrorAction** - action triggered when any error occurs - such as cloud connection problems, scanning, etc.
- **startWithForegroundScanner** - starts the observation proces with scanner wrapped in Foreground Service. This will display notification in notifications bar, but will ensure that the scanning won't be killed by the system. It may even work after user kills your app. 
**startWithSimpleScanner** - starts the observation with scanner without any service. The scan will be destroyed when your app dies. Use this if you want to run quick and simple scan, or you want to implement service wrapper by yourself.

After start, the `ProximityObserver` will return `ProximityObserver.Handler` that you can use to stop scanning later. For example:
```Kotlin
    override fun onDestroy() {
        super.onDestroy()
        observationHandler.stop()
    }
```

## Example app

To get a working prototype, check out the example app. It's a single screen app with three labels that change background color when:

- you are in close proximity to the first desk,
- in close proximity to the second desk,
- when you are in the venue in general.

The demo requires at least two Proximity or Location beacons configured for Estimote Monitoring. It's enabled by default in dev kits shipped after mid-September 2017; to enable it on your own check out the [instructions](https://community.estimote.com/hc/en-us/articles/226144728-How-to-enable-Estimote-Monitoring-).

The demo expects beacons having specific tags assigned:

- `{"attachment":{"blueberry_desk":true,"venue":"office"}}` for the first one,
- `{"attachment":{"mint_desk":true,"venue":"office"}}` for the second one.

These attachments can be used to define the zones presented below:
![Zones](/images/demo_attachments.png)


## Documentation
Javadoc documentation available soon...

## Your feedback and questions
At Estimote we're massive believers in feedback! Here are some common ways to share your thoughts with us:
  - Posting issue/question/enhancement on our [issues page](https://github.com/Estimote/Android-SDK/issues).
  - Asking our community managers on our [Estimote SDK for Android forum](https://forums.estimote.com/c/android-sdk).

## Changelog
To see what has changed in recent versions of our SDK, see the [CHANGELOG](CHANGELOG.md).

