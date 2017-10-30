Changelog
=====================
## 0.1.0-alpha.4 (Oct 28, 2017)

- Introduced improvements to API. Some breaking changes appeared:
  1. `ProximityObserver.RuleBuilder` is now `ProximityObserver.ZoneBuilder`
  2. `ProximityObserver` has now methods: `addProximityZone` instead of `addProximityRule`
  3. `ProximityObserver.ZoneBuilder` has fluent builder now. 
  4. `onExitAction` now returns `ProximityAttachment` that was last visible. 
  5. `ProximityObserver.Handler` is now an interface. 
  6. `proximityRuleBuilder.withDesiredMeanTriggerDistance()` is now split into three methods:
    - `inNearRange`
    - `inFarRange`
    - `inCustomRange(double)` with parameter in meters. 
  7. Errors are now logged to Android logcat by default. 
- Fixed the compilation error `unknown element: <uses-permission>`. There was a problem with wrong manifest merging.
- Fixed problem when using `forAttachmentKey` was not triggering any actions - now it works properly.

## 0.1.0-alpha.3 (Oct 20, 2017)
- Added key-value tag support to `ProximityObserver`

