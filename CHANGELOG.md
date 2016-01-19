Changelog
=====================

## 0.9.7 (January 19, 2016)

- Packet parser errors are logged and should not crash whole application.
- Fixed threading issue that cause scanning to continue when service was stopped (on some devices).
- onExitRegion should be now working on devices with pre-Lollipop Androids. 
- Fixed (https://github.com/Estimote/Android-SDK/issues/135): Fails getting RSSI on Android >=5.0 (Samsung Galaxies)
- Fixed (https://github.com/Estimote/Android-SDK/issues/137): IllegalStateException when starting monitoring on SDK 0.9.6

## 0.9.6 (December 22, 2015)

- Added ScanStatusListner to BeaconManager. This allows to find you when scanning really started and when it stopped. User may switch the Bluetooth off and then turn it on when
  application is running. This listener allows to track this events and react properly to them (eg. gray out device list). See ListBeaconsActivity to see sample code. 
  ```java
    beaconManager.setScanStatusListener(new BeaconManager.ScanStatusListener() {
      @Override public void onScanStart() {
        // Unlock UI
		list.setEnabled(true);
        list.setAlpha(1.0f);
      }

      @Override public void onScanStop() {
        // Lock UI
		list.setEnabled(false);
        list.setAlpha(0.5f);
      }
    });
  ```
- Fixed (https://github.com/Estimote/Android-SDK/issues/134): java.lang.NullPointerException when using SDK on Emulator


## 0.9.5 (December 8, 2015)
  
  - This version uses new Lollipop BLE scanning API. Previous API is still supported on older phones. It uses features like batch scanning and hardware filtering (if particular device supports them). No need to change your code, those features are enabled automatically.
    
## 0.9.4 (October 30, 2015)

 - This release features support for Android Marshmallow runtime permissions and helps you meet all the requirements necessary for beacon detection (Bluetooth, Location, runtime permissions).
 - Full description of all requirements for beacon detection are described in JavaDoc for `SystemRequirementsChecker`.
 - Demos have been updated to target API 23 (M) and uses `SystemRequirementsChecker` to meet all requirements.
 - From time to time SDK will put warning in device logs what is missing.

New tools
 - `SystemRequirementsChecker`: use the `check` method to determine which requirements are not met for beacon detection
 - `SystemRequirementsChecker`: use the `checkWithDefaultDialogs` method in your activity for a convenient way to ask for all permissions and rights. It's all handled by the SDK, and should be of great help if you want to get up and running quickly.
 - `SystemRequirementsHelper`: a grab bag of static methods to determine status of Bluetooth, Location Services, granted required permissions

Breaking changes:
 - `BeaconManager`: not longer included the following methods `checkPermissionsAndService`, `isBluetoothEnabled`, `hasBluetooth`. You can find them in `SystemRequirementsHelper`.

## 0.9.3 (October 20, 2015)
Bug fix:
 - `EstimoteCloud#fetchBeaconDetails(UUID, major, minor, callback)` under certain circumstances could yield beacon not found.

## 0.9.2 (October 20, 2015)
New:
 - Beacon *signal filtering* for smoothing [RSSI](https://en.wikipedia.org/wiki/Received_signal_strength_indication) readings. What does it mean for you? The RSSI values returned by `RangingListener#onBeaconsDiscovered` are much more stable now, which should enable you to predict proximity to a beacon more accurately and without having to write complex filtering algorithms yourself. The dinner's on us\*.

  \* The dinner is not really on us, but the RSSI smoothing code is.
 - You can add and remove multiple callbacks in `BeaconConnection` (new methods `addConnectionCallback`, `removeConnectionCallback`, `clearCallbacks`).
 - Added `EstimoteCloud#fetchBeaconDetails(UUID, major, minor, callback)` method to fetch details of your beacon in Estimote Cloud using UUID, major, minor values.

Bug fixes:
 - Secure UUID introduced in `v0.9` got lots of good improvements. Fixes https://github.com/Estimote/Android-SDK/issues/127.

Breaking changes:
 - Removed `Beacon#getName` method. It was name of the Bluetooth device and was often confused with name given to a beacon in Estimote Cloud. Beacon's  name can be fetched using `EstimoteCloud#fetchBeaconDetails` method.

## 0.9.1 (September 29, 2015)
- Fixes problem when not initializing SDK caused problems with ranging & monitoring.

## 0.9 (September 29, 2015)
- This release brings [**Secure UUID**](https://community.estimote.com/hc/en-us/articles/201371053-What-security-features-does-Estimote-offer-How-does-Secure-UUID-work-) – a security mechanism to protect your beacons from spoofing (where someone tries to ‘impersonate’ your beacons, by broadcasting the same UUID, Major and Minor). Using _Secure UUID_, the UUID, Major and Minor values that your beacon broadcasts will change unpredictably over time. The only way to resolve the values we generate to a particular beacon is via authorized access to Estimote Cloud.

- You can enable _Secure UUID_ via [Estimote app](https://play.google.com/store/apps/details?id=com.estimote.apps.main&hl=en) from Google Play or via SDK
   ```java
  connection = new BeaconConnection(...);
  connection.edit().set(connection.secureUUID(), true).commit(...);
   ```

- Ranging and region monitoring works transparently with _Secure UUID_ enabled beacons. All you need is:

  - Make sure you have initialised SDK with your App ID & App Token.
    ```java
    //  App ID & App Token can be taken from App section of Estimote Cloud.
    EstimoteSDK.initialize(applicationContext, appId, appToken);
    ```

  - Use `SecureRegion` instead of `Region` when starting ranging or monitoring.

    ```java
    // Initialise BeaconManager as before.
    // Find all *your* Secure UUID beacons in the vicinity.
    beaconManager.startRanging(new SecureRegion(“regionId”, null, null, null));

    // Remember that you can also range for other regions as well.
    beaconManager.startRanging(new Region("otherRegion", null, null, null);
    ```

- **Breaking changes**:
 - `BeaconManager` methods for ranging & monitoring (`startRanging`, `stopRanging`, `startMonitoring`, `stopMonitoring`) no longer throws `RemoteException`. We heard that it caused lots of boilerplate code. We agree and removed it.

 - Formatting MAC address was always pain (`aabbccddeeff` or `AA:BB:CC:DD:EE:FF`?). That's why we removed all `String` representation of MAC address and introduced `MacAddress` class.

 - For the same reason we removed all usages of `String` representation [UUID](https://en.wikipedia.org/wiki/Universally_unique_identifier) and used Java's `java.util.UUID` class instead.

## 0.8.8 (September 16, 2015)
 - Finally support for built-in sensors: motion and temperature.
 - Motion sensor readout with notifications. Note that you need to make sure it is enabled (separate property do enable/disable motion) sensor.
  ```java
  connection = new BeaconConnection(...);

  // Make sure to enable motion sensor first.
  connection.edit().set(connection.motionDetectionEnabled(), true).commit(...callback...);

  connection.setMotionListener(new Property.Callback<MotionState>() {
    @Override public void onValueReceived(final MotionState value) {
      // Motion state in value argument.
    }

    @Override public void onFailure() {
      // Error handling.
    }
  });
  ```
 - Temperature sensor readout and calibration (two separate properies added to BeaconConnection).
 ```java
 // Temperature calibration (see also docs for BeaconConnection#temperatureCalibration()).
 connection.edit().set(connection.temperatureCalibration(), 21).commit(...);

 // If you want to measure temperature from beacon on demand.
 connection.temperature().getAsync(new Property.Callback<Float>() {
      @Override public void onValueReceived(final Float value) {
        // updateTemperatureValue(value);
      }

      @Override public void onFailure() {
        // Error handling.
      }
    });
```

Bug fixes:
- Negative temperature is properly interpreted in EddystoneTelemetry packet.
- BeaconManager crash when there are Eddystone beacons with bad packet.

**Breaking changes**
- `BeaconConnection.ConnectionCallback` has been changed to indicate that that your access to beacon had been authorised (`ConnectionCallback#onAuthorized(BeaconInfo)`).

## 0.8.7 (August 25, 2015)
 - Finally Estimote SDK is available on Maven Central (`com.estimote:sdk:0.8.7@aar`).

## 0.8.6 (August 21, 2015)
 - Authentication to beacon is more robust than ever (requires updating beacon to firmware 3.2.0).

## 0.8.5 (August 20, 2015)
 - Security improvements in the beacon authorization mechanism.
 - Beacon update is more stable.

## 0.8.2 (July 24, 2015)
 - This is mainly a bugfix release.
 - Fixed (https://github.com/Estimote/Android-SDK/issues/109): Documentation Bug.
 - Fixed (https://github.com/Estimote/Android-SDK/issues/117): Crash after update to 0.8 version.
 - SDK demos are now using Material Design (h/t @RingoMckraken).

## 0.8.1 (July 15, 2015)
 - Small fixes for Eddystone protocol.

## 0.8 (July 14, 2015)
 - Say hello to [Eddystone](https://developers.google.com/beacons) - an open protocol BLE protocol from Google.
   - Estimote Beacons can broadcast Eddystone protocol.
 - In order to start playing with Eddystone you need to update firmware of your existing Estimote beacons to `3.1.1`. Easiest way is through [Estimote app on Google Play](https://play.google.com/store/apps/details?id=com.estimote.apps.main). Than you can change broadcasting scheme on your beacon to Eddystone-URL or Eddystone-UID.
 - *New in SDK*:
   - find nearby Eddystone beacons (`BeaconManager#startEddystoneScanning`)
   - configure Eddystone related properties:
     - URL property of `Eddystone-URL` (see `BeaconConnection#eddystoneUrl`)
     - namespace & instance properties of `Eddystone-UID` (see `BeaconConnection#eddystoneNamepsace`, `BeaconConnection#eddystoneInstance`)
   - configure broadcasting scheme of beacon to `Estimote Default`, `Eddystone-UID` or `Eddystone-URL` (see `BeaconConnection#broadcastingScheme`)
 - [SDK Examples](https://github.com/Estimote/Android-SDK/tree/master/Demos) have been updated to showcase Eddystone support.

## 0.7 (June 18, 2015)
 - Initial support for nearables. You can discover nearby nearables via `BeaconManager.startNearableDiscovery()`. With nearbles you can read temperature, motion, orientation without need to connect to it. Directly from discovered `Nearable` class.
 - You can change basic & smart power mode in your beacon via `BeaconConnection`. [Read more about power modes.](https://community.estimote.com/hc/en-us/articles/202552866-How-to-optimize-battery-performance-of-Estimote-Beacons-)
 - `android.hardware.bluetooth_le` feature is no longer required
 - You can also change conditional broadcating in beacon (Flip To Sleep). It is great for development. [Read more about Flip To Sleep.](https://community.estimote.com/hc/en-us/articles/205413787-How-to-enable-conditional-broadcasting-and-Flip-to-sleep-mode-)
 - **Breaking changes** (1.0 is approaching, bear with us):
   - most of `BeaconConnection`s write* methods are gone, they are replaced with more appropriate `Property` class

```java
// Before
connection.writeMajor(newMajor, callback);
connection.writeMinor(newMinor, callback);

// After: reading
connection.minor().get()
connection.major().get()

// After: writing in batch
connection.edit()
  .set(connection.proximityUuid(), newUuid)
  .set(connection.major(), newMajor)
  .set(connection.minor(), newMinor)
  .commit(callback);
```

## 0.6.1 (June 2, 2015)
 - Fixed authentication issues (#111).

## 0.6 (May 4, 2015)
 - You can update update firmware in Estimote beacons from the SDK. There are several ways to do it:
    - Use `BeaconOta` class to perform firmware update on selected beacon.
    - Use `BeaconConnection#updateBeacon` which triggers update on the beacon. See updated demos to see how it works.
    - You can also use Estimote app from Play Store to do that.
 - Estimote SDK now includes also `android.permission.ACCESS_NETWORK_STATE` permission to determine internet connectivity.
 - **Breaking changes** (please bear with us, we are approaching stable 1.0 release):
    - `BeaconConnection`'s `ConnectionCallback#onAuthenticated` method does not return `BeaconCharacteristics` object any more. You can read them directly on `BeaconConnection` object.
    - For example read reading broadcasting power is just `connection.getBroadcastingPower()`.

## 0.5 (April 17, 2015)
 - From now Estimote SDK for Android is distributed as [AAR archive](http://tools.android.com/tech-docs/new-build-system/aar-format) rather than jar. That means that you do not need to change your `AndroidManifest.xml`. SDK's `AndroidManifest.xml` will be merged with your application's `AndroidManifest.xml`. See [installation guide](https://github.com/Estimote/Android-SDK#installation) how to add library to your project.
 - Welcome back! We have added support for [Estimote Cloud](http://cloud.estimote.com). You can access it via `EstimoteCloud` class. Remember first to provide your App ID & App Token from App section of [Estimote Cloud](http://cloud.estimote.com) via `EstimoteSDK#initialize` method.
 - From now all connections to beacons needs to be authorized. If a beacon is not registered to your account, you will not be able to connect to it.
 - Estimote SDK's `AndroidManifest.xml` uses `BLUETOOTH`, `BLUETOOTH_ADMIN` and `INTERNET` permissions.
 - Yes, there is single point of initialisation of the SDK.

 ```java
 //  App ID & App Token can be taken from App section of Estimote Cloud.
 EstimoteSDK.initialize(applicationContext, appId, appToken);
 // Optional, debug logging.
 EstimoteSDK.enableDebugLogging(true);
 ```
 - All exceptions within the SDK has been unified and exposed in `com.estimote.sdk.exception` package.

 - That means some **breaking changes**:
	 - `L` class is no longer available, in order to turn on debug logging use `EstimoteSDK` class.
	 - `BeaconConnection.ConnectionCallback` & `BeaconConnection.WriteCallback` methods have been changed to contain apropriate exception when happens.

## 0.4.3 (November 12, 2014)
 - Fixes https://github.com/Estimote/Android-SDK/issues/59: compatibilty with Android L

## 0.4.2 (June 24, 2014)
 - Fixes https://github.com/Estimote/Android-SDK/issues/55: it is safe to use library from remote process

## 0.4.1 (March 18, 2014)
 - *CAN BREAK BUILD*: MonitoringListener returns list of beacons the triggered enter region event (https://github.com/Estimote/Android-SDK/issues/18)
 - Better messaging when BeaconManager cannot start service to scan beacons (https://github.com/Estimote/Android-SDK/issues/25)
 - Fixed bug in SDK when other beacons are around (https://github.com/Estimote/Android-SDK/issues/27)

## 0.4 (February 17, 2014)
 - Introducing ability to change beacon's UUID, major, minor, broadcasting power, advertising interval (see BeaconConnection class).
 - Dropping Guava dependency.

## 0.3.1 (February 11, 2014)
 - Fixes bug when simulated beacons were not seen even when using Estimote's proximity UUID.

## 0.3 (February 11, 2014)
 - Background monitoring is more robust and using AlarmService to invoke scanning.
 - Default values for background monitoring were changed. Scanning is performed for 5 seconds and then service sleeps for 25 seconds. Those values can be changed with BeaconManager#setBackgroundScanPeriod.
 - Beacons reported in RangingListener#onBeaconsDiscovered are sorted by accuracy (estimated distance between device and beacon).
 - Bug fixes.

## 0.2 (January 7, 2014)
 - *IMPORTANT*: package changes BeaconService is now in `com.estimote.sdk.service service`. You need to update your `AndroidManifest.xml` service definition to `com.estimote.sdk.service.BeaconService`.
 - Support for monitoring regions in BeaconManager.
 - Region class: it is mandatory to provide region id in its constructor. This matches CLRegion/ESTBeaconRegion from iOS.
 - Beacon, Region classes now follow Java bean conventions (that is getXXX for accessing properties).
 - Debug logging is disabled by default. You can enable it via `com.estimote.sdk.utils.L#enableDebugLogging(boolean)`.

## 0.1 (December 9, 2013)
 - Initial version.
