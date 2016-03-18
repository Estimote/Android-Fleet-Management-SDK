# Monitoring after system restart #

In order to have monitoring working after device is rebooted you must:
* Create empty broadcast receiver (Android will invoke Application.onCreate before BroadcastReceiver.onReceive):
```java
public class SystemBootReceiver extends BroadcastReceiver {
  @Override public void onReceive(Context context, Intent intent) {
  }
}
```
* In AndroidManifest.xml register that receiver to boot event (and optionally power events):
```xml
    <receiver android:name=".SystemBootReceiver">
      <intent-filter>
        <action android:name="android.intent.action.BOOT_COMPLETED" />
        <action android:name="android.intent.action.ACTION_POWER_CONNECTED" />
        <action android:name="android.intent.action.ACTION_POWER_DISCONNECTED" />
      </intent-filter>
    </receiver>
```
* Add a permission to start at boot.
```xml
 <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
```
* Create you own application class (if you don't have any) and start monitoring there (in onCreate method):
```java
public class MyApp extends Application {

  private Region monitoringRegion = new Region("region", UUID.fromString("my-UUID"), 1, 1);
  private BeaconManager beaconManager;
  private NotificationManager notificationManager;

  @Override public void onCreate() {
    super.onCreate();
    beaconManager = new BeaconManager(this.getApplicationContext());
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override public void onServiceReady() {
        startMonitoring();
      }
    });
    notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
  }

  private void startMonitoring() {
    beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
      @Override public void onEnteredRegion(Region region, List<Beacon> list) {
          // invoke your action here
      }

      @Override public void onExitedRegion(Region region) {
          // invoke your action here
      }
    });
    beaconManager.startMonitoring(monitoringRegion);
  }
}
```
* Don't forget to add you Application class to AndroidManifest.xml in <application> tag.
```xml
 <application
      android:allowBackup="true"
      android:label="@string/app_name"
      android:icon="@mipmap/ic_launcher"
      android:theme="@style/AppTheme"
      android:name=".MyApp"
      >
```
* In one of your activities check permission for Bluetooth and Location. Access must be granted by the user before system is rebooted. If not monitoring will not start.
```java
    SystemRequirementsChecker.checkWithDefaultDialogs(this);
```
