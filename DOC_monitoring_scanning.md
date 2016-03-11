# Ranging, Monitoring and Scanning

<!-- TOC depthFrom:2 depthTo:3 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Overview](#overview)
- [Monitoring and ranging](#monitoring-and-ranging)
	- [1. Define beacon regions](#1-define-beacon-regions)
	- [2. Create a Beacon Manager object](#2-create-a-beacon-manager-object)
	- [3. Connect to the beacon scanning service](#3-connect-to-the-beacon-scanning-service)
	- [4. Set monitoring and ranging listeners](#4-set-monitoring-and-ranging-listeners)
	- [5. Set scan periods](#5-set-scan-periods)
	- [6. Start monitoring/ranging](#6-start-monitoringranging)
- [Scanning](#scanning)
	- [Eddystone](#eddystone)
	- [Nearable](#nearable)
	- [Estimote Telemetry](#estimote-telemetry)
	- [Estimote Location](#estimote-location)

<!-- /TOC -->

## Overview

Estimote beacons and stickers are able to broadcast multiple packets: [iBeacon](http://developer.estimote.com/ibeacon/), [Eddystone](http://developer.estimote.com/eddystone/), [Nearable](http://developer.estimote.com/nearables/), Estimote Telemetry, and Estimote Location. Android SDK supports interacting with them in the following ways:

- **monitoring** and **ranging** for [iBeacon](http://developer.estimote.com/ibeacon/) packets advertised by beacons.
- **scanning** for [Eddystone](http://developer.estimote.com/eddystone/), Estimote Telemetry and Estimote Location packets advertised by beacons, and [Nearable](http://developer.estimote.com/nearables/) packets advertised by stickers.

## Monitoring and ranging

[Monitoring](http://developer.estimote.com/ibeacon/tutorial/part-2-background-monitoring/) can be thought of as a geofence: a virtual barrier, here defined by the range of beacon or a group of beacons. Going in and out of this range triggers events that application can react to.

[Ranging](http://developer.estimote.com/ibeacon/tutorial/part-3-ranging-beacons/) enables receiving more comprehensive beacon data: identifiers and proximity estimates of the beacons in range.

To use monitoring and ranging, follow these steps:

  1. Define beacon regions.
  2. Create a Beacon Manager object.
  3. Connect the Beacon Manager to the beacon scanning service maintained by the Estimote SDK.
  4. Set monitoring and ranging listeners.
  5. Set scan periods.
  6. Start monitoring/ranging for the regions defined earlier.

### 1. Define beacon regions

Beacon regions can be used to specify beacons or groups of beacons that should trigger a monitoring event or be included in the list of ranged devices. You can defined a region using beacon identifiers:

- **UUID** - most commonly represented as a String, e.g.: `"B9407F30-F5F8-466E-AFF9-25556B57FE6D"`
- **Major** - an unsigned short integer, (1–65535)
- **Minor** - an unsigned short integer, (1–65535)

You can find UUIDs, majors, and minors of your beacons in [Estimote Cloud](https://cloud.estimote.com).

Define a Region object like this:

```java
Region mintBeaconRegion = new Region("Mint beacon",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), 24515, 28667);
```

You don't have to pass all three values—e.g., your region can be based on UUID only (skip other values by passing null):

```java
Region allBeaconsRegion = new Region("Beacons with default Estimote UUID",
        UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
```

The available combinations are: UUID + major + minor, UUID + major, UUID alone, or none of it.

### 2. Create a Beacon Manager object

As you might need to reuse the Beacon Manager in many activities or fragments, it's a good practice to store an instance of it in your Application's subclass:

```java
public class MyApplication extends Application {

    private BeaconManager beaconManager;

    @Override
    public void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(getApplicationContext());
    }
}
```

Make sure that you add your `Application` subclass to `AndroidManifest.xml`:

```xml
<application
    android:allowBackup="true"
    android:icon="@mipmap/ic_launcher"
    android:label="@string/app_name"
    android:supportsRtl="true"
    android:theme="@style/AppTheme"
    android:name=".MyApplication"> <!-- <=== here -->
```

### 3. Connect to the beacon scanning service

Once you create the Beacon Manager object, you need to connect it to the **beacon service** that is responsible for performing BLE scanning operations. You can simply do it by invoking the `connect` method. Pass a `ServiceReadyCallback` to receive information when mentioned service is ready to use.

```java
@Override
public void onCreate() {
    super.onCreate();
    beaconManager = new BeaconManager(getApplicationContext());
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
        @Override
        public void onServiceReady() {
            // Ready to start scanning!
        }
    });
}
```

Make sure that the `beaconManager`'s connection scope is strictly related to the lifecycle of its encompassing application (or activity, or fragment).

### 4. Set monitoring and ranging listeners

Beacon Manager offers a variety of listeners that facilitate detecting beacons.

#### Monitoring listener

Monitoring listener enables you to subscribe to monitoring events, such as entering and exiting the range of beacons in a Region.

```java
beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
    @Override
    public void onEnteredRegion(Region region, List<Beacon> list) {
        // ...
    }

    @Override
    public void onExitedRegion(Region region) {
        // ...
    }
});
```

#### Ranging listener

Ranging listener enables you to subscribe to a continuous (by default: every second) stream of ranging events, which provides a detailed list of beacons detected in range, their full identifiers, and relative proximity to them. If a beacon goes out of range, it will disappear from the list.

```java
beaconManager.setRangingListener(new BeaconManager.RangingListener() {
    @Override
    public void onBeaconsDiscovered(Region region, List<Beacon> beacons) {
        if (beacons.size() != 0) {
            Beacon beacon = beacons.get(0);
            // ...
        }
    }
});
```

### 5. Set scan periods

To conserve the host device's battery, Estimote SDK doesn't continuously scan for beacons. Instead, it cycles between periods of scanning and waiting. You can adjust the duration of these two periods with two methods listed below. Bear in mind that these values are only a hint—the internal Bluetooth stack of the device might shorten or extend the scan period.

- **monitoring**: `setBackgroundScanPeriod(long scanPeriodMillis, long waitTimeMillis)`
- **ranging**: `setForegroundScanPeriod(long scanPeriodMillis, long waitTimeMillis)`

For example, to increase responsiveness of monitoring events, you may set the `scanPeriod` to 5 seconds and the `waitTime` to 10 seconds.

```java
beaconManager.setBackgroundScanPeriod(5000, 10000);
```

Just keep in mind that shortening the cycle can increase the battery drain.

If not sure what to do, leave these two methods out. The default values were picked carefully to provide a good balance between responsiveness and battery usage.

### 6. Start monitoring/ranging

Once you set monitoring and ranging listeners you can start monitoring and ranging using the regions you defined earlier.

```java
@Override
public void onCreate() {
    super.onCreate();
    beaconManager = new BeaconManager(getApplicationContext());
    beaconManager.setMonitoringListener(/* ... */);
    beaconManager.setRangingListener(/* ... */);
    // beaconManager.setBackgroundScanPeriod(5000, 10000);
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
        @Override
        public void onServiceReady() {
            // Ready to start scanning!
            beaconManager.startMonitoring(allBeaconsRegion);
            // beaconManager.startRanging(defaultUUIDRegion);
        }
    });
}
```

#### Hint: ranging only when in range of beacons

Since ranging is much more energy-intensive than monitoring, it's a good practice to only do it when you know (thanks to monitoring) that you're in range of some beacons:

```java
private void setMonitoringListener() {
    beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
        @Override
        public void onEnteredRegion(Region region, List<Beacon> list) {
            beaconManager.startRanging(region);
        }

        @Override
        public void onExitedRegion(Region region) {
            beaconManager.stopRanging(region);
        }
    });
}
```

## Scanning

Android SDK supports scanning for devices that broadcast [Eddystone](http://developer.estimote.com/eddystone/), Estimote Telemetry, Estimote Location, and [Nearable](http://developer.estimote.com/nearables/) packets.

Since scanning is similar in nature to ranging, it uses the `setForegroundScanPeriod` settings.

### Eddystone

[Eddystone](http://developer.estimote.com/eddystone/) is an open Bluetooth 4.0 protocol from Google that is designed to support multiple data packet types such as **Eddystone-UID** or **Eddystone-URL**. Estimote Beacons are fully compatible with Eddystone.

To start scanning for [Eddystone](http://developer.estimote.com/eddystone/) packets:

-   Set an `EddystoneListener`:

    ```java
    beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
        @Override
        public void onEddystonesFound(List<Eddystone> eddystones) {
            // ...
        }
    });
    ```

-   Start scanning for Eddystone devices. Make sure that you store the `scanId` value—you will need it later to stop scanning:

    ```java
    public class MyActivity extends Activity {

        private BeaconManager beaconManager;
        private String scanId;

        @Override
        protected void onCreate() {
            super.onCreate();
            beaconManager = new BeaconManager(this);
            beaconManager.setEddystoneListener(/* ... */);
        }

        @Override
        protected void onStart() {
            super.onStart();
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    scanId = beaconManager.startEddystoneScanning();
                }
            });
        }

        @Override
        protected void onStop() {
            super.onStop();
            beaconManager.stopEddystoneScanning(scanId);
        }
    }
    ```

### Nearable

[Nearable](http://developer.estimote.com/nearables/) is the main advertising packet of Estimote Stickers. Apart from the sticker's 16-byte identifier, it includes sensor data (motion, temperature) and sticker's health data (battery voltage, firmware version).

To start scanning for [Nearables](http://developer.estimote.com/nearables/):

-   Set a `NearableListener`:

    ```java
    beaconManager.setNearableListener(new BeaconManager.NearableListener() {
        @Override
        public void onNearablesDiscovered(List<Nearable> nearables) {
            // ...
        }
    });
    ```

-   Start scanning for Nearable devices. Make sure that you store the `scanId` value—you will need it later to stop scanning:

    ```java
    public class MyActivity extends Activity {

        private BeaconManager beaconManager;
        private String scanId;

        @Override
        protected void onCreate() {
            super.onCreate();
            beaconManager = new BeaconManager(this);
            beaconManager.setNearableListener(/* ... */);
        }

        @Override
        protected void onStart() {
            super.onStart();
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    scanId = beaconManager.startNearableDiscovery();
                }
            });
        }

        @Override
        protected void onStop() {
            super.onStop();
            beaconManager.stopNearableDiscovery(scanId);
        }
    }
    ```

### Estimote Telemetry

To start scanning for Estimote Telemetry packets:

-   Set a `TelemetryListener`:

    ```java
    beaconManager.setTelemetryListener(new BeaconManager.TelemetryListener() {
        @Override
        public void onTelemetriesFound(List<EstimoteTelemetry> telemetries) {
            // ...
        }
    });
    ```

-   Start scanning for Estimote Telemetry packets. Make sure that you store the `scanId` value—you will need it later to stop scanning:

```java
public class MyActivity extends Activity {

    private BeaconManager beaconManager;
    private String scanId;

    @Override
    protected void onCreate() {
        super.onCreate();
        beaconManager = new BeaconManager(this);
        beaconManager.setTelemetryListener(/* ... */);
    }

    @Override
    protected void onStart() {
        super.onStart();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                scanId = beaconManager.startTelemetryDiscovery();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        beaconManager.stopTelemetryDiscovery(scanId);
    }
}
```

### Estimote Location

To start scanning for Estimote Location packets:

-   Set a `LocationListener`:

    ```java
    beaconManager.setLocationListener(new BeaconManager.LocationListener() {
        @Override
        public void onLocationsFound(List<EstimoteLocation> locations) {
            // ...
        }
    });
    ```

-   Start scanning for Estimote Location packets. Make sure that you store the `scanId` value—you will need it later to stop scanning:

    ```java
    public class MyActivity extends Activity {

        private BeaconManager beaconManager;
        private String scanId;

        @Override
        protected void onCreate() {
            super.onCreate();
            beaconManager = new BeaconManager(this);
            beaconManager.setLocationListener(/* ... */);
        }

        @Override
        protected void onStart() {
            super.onStart();
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                @Override
                public void onServiceReady() {
                    scanId = beaconManager.startLocationDiscovery();
                }
            });
        }

        @Override
        protected void onStop() {
            super.onStop();
            beaconManager.stopLocationDiscovery(scanId);
        }
    }
    ```
