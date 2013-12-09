# Estimote SDK for Android #

## Overview ##

Estimote SDK for Android is a library to interact with Estimote beacons.

It mimics [Estimote SDK for iOS](https://github.com/Estimote/iOS-SDK). All name conventions come from iBeacon library from iOS and from Estimote iOS library.

It allows to:
- range beacons (scan beacons and optionally filter them by their values),
- monitor beacons (not implemented yet, on roadmap),
- read beacon's characteristics (not implemented yet, on roadmap).

**What is ranging?**

Ranging allows apps to know the relative distance between device and beacons. This can be very valuable. Consider example of an indoor location app of department store. App can know in which department (such as shoes, suits, accessories) user is close-by. Information about this proximity can be use by the app to show fitting guides or offer today's discounts.

Because bluetooth low energy ranging depends on detecting radio signals, results will vary depending on placements of Estimote beacon and user's device:
- device being in user's hands, in a bag, or user's jean's pocket will produce different results,
- if there is clear line of sight between device and beacon gives different results than having beacon hidden between shelves.
- Good way to have consistent ranging results is to use it when app is in foreground which means that user holds device in hands.

Apps can use `startRanging` method of `BeaconsManager` class to determine relative proximity of beacons in the region and can be updated when this distance changes. Ranging updates comes every second to listeners registerd with `setRangingListener` method of `BeaconsManger` class. Update contains list of currently found beacons. If beacon goes out of range, it will not be anymore on this list.


## Installation ##


## Usage ##


## Changelog ##

* 0.1 (December 9, 2013)
 * Initial version.

