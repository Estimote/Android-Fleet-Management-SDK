Changelog
=====================
## 1.1.0 (Aug 22, 2017)
- Enable Magnetometer: Synchronize Magnetometer calibration data from cloud.
- FIX: Use millivolts as battery voltage level unit
- FIX: EstimoteLocation - changed timestamp to be public

## 1.0.15 (Aug 16, 2017)
- Revert buggy implementation  for: Use average advertising time to calculate expiration time when ranging beacons

## 1.0.14 (Aug 14, 2017)
- Use average advertising time to calculate expiration time when ranging beacons
- Add timestamp field to all packets parsed from scanner
- FIX: Ensure Here and now are synchronized to beacons with firmware 4.7.0 or newer
- FIX: Use proper sdk version when communicating with cloud

## 1.0.13 (Aug 08, 2017)
- Bug fix: Use region exit expiration timeout defined by the user instead of default value
- Cleanup SDK manifest to make it easier to use SDK in non-java-gradle android projects 
- Cleanup SDK resources to make it easier to use SDK in non-java-gradle android projects

## 1.0.12 (Aug 01, 2017)
- Bug fix: Ensure SDK is able to connect to the beacon without mesh network configured
- Bug fix: Hardware filter for BeaconRegion for Nougat devices

## 1.0.11 (Jul 26, 2017)
- Bug fixes

## 1.0.10 (Jul 21, 2017)
- Add "Here and Now" feature support. Now, "Here and Now" related settings are synchronized from Cloud in to the beacon.
- Synchronize GPIO settings from Cloud.
- Make it possible to reflect GPIO_0 state on Beacon's embeded LED.

## 1.0.9 (Jul 19, 2017)
- Old proximity beacons (D3.4) iBeacon packets are now properly scanned using `BeaconManager`
- Major improvements to monitoring/ranging on Nougat. The scanning relies now on internal Android mechanism, so scanning periods may slightly differ from those you set with `setForegroundScanningPeriod` and `setBackgroundScanningPeriod`. 

## 1.0.8 (Jul 4, 2017)
 - Added support for routed mesh immmplementation
 - Minor bug-fixes

## 1.0.3 (May 2, 2017)
- BeaconRegion should be now properly ranged/monitored. Related to [#211](https://github.com/Estimote/Android-SDK/issues/211)
- Added Mirror Access Control flag to packet. 
- Fixed NPE being thrown by KitKatScanScheduler on API 18 devices.

## 1.0.2 (April 19, 2017)
- Fixed [#211](https://github.com/Estimote/Android-SDK/issues/211) when SecureBeacon region with null UUID thrown NPE. Also fixed problem with filtering secure regions on some devices.
- Fixed [#207](https://github.com/Estimote/Android-SDK/issues/207) where Nearables and old Proximity beacons were not scanned as a ConfigurableDevice.
- Fixed [#213](https://github.com/Estimote/Android-SDK/issues/213) where temperature values in Estimote Telemetry were wrongly parsed.
- Added improvements for Nougat+ devices. Since Nougat, every application is allowed to start/stop BLE scan a maximum of 5 times per 30s. New improvements prevents many scan start/stop events, which resulted in "App XXX is scanning too frequently" logs. All start/stop requests are buffered and the most recent is executed after the delay time. You can play with the setting by yourself using new method in `BeaconManager` class, but we recommend using the default one (1,5s)
```Java
beaconManager.setScanRequestDelay(delayInMillis);
``` 

## 1.0.1 (April 11, 2017)
- Fixed [#206](https://github.com/Estimote/Android-SDK/issues/206) when changing scan period after starting scan was not applied 
- Fixed [#205](https://github.com/Estimote/Android-SDK/issues/205) when scan results were not sorted by RSSI.
- Fixed [#187](https://github.com/Estimote/Android-SDK/issues/187) when Estimote telemetry packets were reported as duplicates after getting out of beacon's range.
- Fixed "ClassNotFoundException" being thrown on pre-Lollipop devices when using `ScanFilter` class.
- Improved low-level filtering for Estimote devices.

## 1.0.0 (March 29, 2017)
- Changed package names to clean messy things, see [migration guide](https://github.com/Estimote/Android-SDK/blob/master/Docs/switching_to_1.0.0.md)
- Added new bulk updater, see [tutorial](https://github.com/Estimote/Android-SDK/blob/master/Docs/quick_snippets.md#bulk-updater-quick-start)
- Added discovery, ranging and monitoring for Estimote Mirror packets
- Updated [javadocs](https://estimote.github.io/Android-SDK/JavaDocs/)
- Scanning for configurable devices is now a part of `BeaconManager`. See [quick start guide](https://github.com/Estimote/Android-SDK/blob/master/Docs/quick_snippets.md#connecting-to-your-devices-quick-start) for more info.
- Fixed Estimote Location packets not being reported 
- Fixed problem with duplicated telemetry packets
- Fixed access to `Vector` x,y,z fields
- Fixed the problem with mesh_key when synchronising beacon data
 
## 0.16.0 (January 27, 2017)
- Added support for mesh via MeshManager 
- Added firmware update through mesh
- Fixed Motion Only broadcasting problems
- Added minor fixes for stability and bugfixes.

## 0.15.0 (December 19, 2016)
- Added minor fixes to Secure UUID resolving.
- Fixed wrong parceling in EstimoteTelemetry packets

Merged from beta branch:
- Added fixes for NFC 
- Added MOTION_ADVERTISING_DELAY setting. 
- Added support for key-value storage on devices with firmware 4.9.0+
  * You can now push your key:value map via our SDK to device in the same way as typical setting:
  ```Java
   Map<String, String> map = new HashMap<>();
   map.put("myKey", "myValue");
   connection.settings.storage.writeStorage(map, new StorageManager.WriteCallback() {
      @Override
      public void onSuccess() {
        // HANDLE SUCCESS
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        // HANDLE FAILURE
      }
    });
    ```
   * Reading stored values can be done without user being authorised - this will allow client apps to read stored data. For this purpose, there is a new method for quick connection to beacon: `deviceConnectionProvider.getConnectionForStorageRead(ConfigurableDevice)` 
 * Bear in mind that the more data is stored on device the more time it takes to read it.
 
- Improved Estimote Analytics precision and data persistence.

## 0.13.0 (September 28, 2016)
- Added support for Shake-to-connect:
	* Enabling/Disabling this setting on device via sdk
	* ConfigurableDevice has now property isShaken
- Added support for Near-to-connect:
	* Enabling/Disabling this setting on device via sdk
	* ConfigurableDevice has now property isClose
- Added Bulk Updater functionality to ConfigurableDevicesScanner:
	* You can enable it via `configurableDevicesScanner.enableBulkFirmwareUpdate(BulkUpdater.BulkUpdaterCallback);`
	* When enabled, bulk updater will update firmware and settings to all devices around you. This will make sure your beacons are always up to date. 
	* Availabe for use in foreground, or in background if you run your ConfigurableDevicesScanner in a service/or non-main thread.
	* Returns result via one callback, so you are able to be constantly notified about process.
	
## 0.12.0 (September 14, 2016)
- Added support for monitoring and ranging on Android Nougat devices. 
	* Due to new Android restrictions, we can only start/stop scanning no more than 5 times per 30 seconds. 
	* It is strongly recommended, to set your foreground/background scanning periods according to the new specification. If you forgot to do so, SDK will automatically update periods to avoid scan block by Nougat.
	
- Flag DISABLE_BATCH_SCANNING is from set to 'true' from now on. Batch scan implementation varies on many phones, and it is usually causing badly delayed scan results. If you really want to use it, just set it to 'false' in your android manifest file. 
- Fixed (https://github.com/Estimote/Android-SDK/issues/168) - FATAL EXCEPTION: BeaconServiceThread 

## 0.11.1 (September 1, 2016)
- Added Firmware version to ConfigurableDevice object
- Fixed wrong NFC cloud data 
- Fixed Flip to Sleep problems on some devices
- Fixed GPIO wrong data saving
- Fixed rare bug when NPE was thrown when closing connection while connecting

## 0.11.0 (August 1, 2016)
- Added NFC support for changing NFC data transmited by beacon.
	* You can set your own URI or Android Application Package Name as an EstimoteNdefRecord
	* Your data can take up to 256 bytes.
	* Beacon Id and Mac address are added automatically 
- Fixed Analytics problems with sending data (https://github.com/Estimote/Android-SDK/issues/157)

## 0.10.8 (July 6, 2016)
- Fixed problems while updating firmware
- Fixed old firmware version reported by SDK while checking device firmware. 
- Fixed "server error 200: null" bug with connecting to nearables
- Removed unnecessary logs

## 0.10.7 (July 1, 2016)
- Added motion only and flip to sleep as a separate settings (instead of Conditional Broadcasting)
- Fixed bugs with SettingEditor
- Fixed Motion Only/Flip to sleep problems on older devices
- Fixed EID tx power setting
- Analytics from now on is enabled by default. 


## 0.10.6 (June 23, 2016)
- Added support for connectiong to Nearables
	* There are some issues on some phones due to different implementations of bluetooth stack. We're working on it and it should be improved really soon. We recommend using Nexus devices for stable connection.
- Added support for Nearables Eddystone URL
- Nearables reported by scanner now have their type and color resolved (only for logged user otherwise all is UNKNOWN)
- Removed some unnecessary scan logs.

## 0.10.5 (May 25, 2016)
- Ranging results are now reported at exact time
- Default beacon expiration time after which onExitRegion events are called is now set to 20s (was 10s). This should help with random short onExit/onEnter events. If you need to tweak this by yourself, beaconManager has new method for changing this to value from 1-60s range:
```java
 beaconManager.setRegionExitExpiration(TimeUnit.SECONDS.toMillis(20));
```
Bear in mind, that high beacon advertising interval is also causing random short onEnter/onExit events. You can try lowering it to 200-300ms to gain better user experience.
- Added support for Eddystone Configuration Service (ECS)
- Minor optimizations

## 0.10.4 (May 11, 2016)
- Fixed problems with Advertising Interval setting update on Proximity Beacons
- Fixed (https://github.com/Estimote/Android-SDK/issues/151): Context problems with Analytics where NPE was thrown.

## 0.10.3 (May 4, 2016)
- Added support for Analytics - can be enabled via EstimoteSDK class and offers monitoring and ranging statistics.
- Fixed (https://github.com/Estimote/Android-SDK/issues/149): crash : com.estimote.sdk.internal.utils.AsyncCache$1.onFailure (AsyncCache.java:106)
- Fixed rare bug where reading beacon characteristic caused crash
- Fixed errors with RecoveryHelper while connecting to beacons
- Optimized beacon connection
- Unresolved Eddystone EID packets are now reported by scanner

## 0.10.2 (April 14, 2016)
- Added support for Eddystone Ephemeral ID (EID)
- 'Check for firmware updates' now properly compare firmware version values
- ScanResultItem is now Parcelable

## 0.10.1 (March 17, 2016)
- Added value validation for each setting
- Added RSSI read from connected beacon (for Proximity & Location beacons)
- Fixed enabling Secure UUID
- Fixed (https://github.com/Estimote/Android-SDK/issues/144): stopRanging does not seem to stop actual ranging
- Fixed duplicated listener notification while ranging/monitoring
- Fixed rare crash on closing bluetooth gatt
- Fixed triggering scan cycle in Doze mode (Android 6.0+)
- Beacon firmware is now cached properly

## 0.10.0 (March 11, 2016)

- New SDK for configuring Location and Proximity beacons
  * supports more sensors (temperature, motion, light)
  * advertising multiple packet types 
- Improved monitoring using batch mode and hardware filtering.
- Support for observing Estimote Telemetry packets
- Support for observing Estimote Location packets
- More information can be found in following docs:
   * [Scanning and monitoring](/DOC_monitoring_scanning.md)
   * [Beacon connection](/DOC_deviceConnection.md)
   * [Multiple advertisers in Location Beacons](/DOC_multiadvertisers.md)
   * [Using telemetry packets](/DOC_telemetry.md)


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
