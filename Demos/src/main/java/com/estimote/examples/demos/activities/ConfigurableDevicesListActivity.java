package com.estimote.examples.demos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.ConfigurableDevicesListAdapter;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;
import java.util.Collections;
import java.util.List;

/**
 * Displays list of found beacons sorted by RSSI.
 * Starts new activity with selected beacon if activity was provided.
 *
 * @author lukasz.pobereznik@estimote.com (Lukasz Pobereznik)
 */
public class ConfigurableDevicesListActivity extends BaseActivity {

  private static final String TAG = ConfigurableDevicesListActivity.class.getSimpleName();

  public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
  public static final String EXTRAS_DEVICE = "extrasDevice";

  private ConfigurableDevicesScanner deviceScanner;
  private ConfigurableDevicesListAdapter adapter;

  @Override protected int getLayoutResId() {
    return R.layout.main;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Configure device list.
    adapter = new ConfigurableDevicesListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);
    list.setOnItemClickListener(createOnItemClickListener());

    deviceScanner = new ConfigurableDevicesScanner(this);
    deviceScanner.setOwnDevicesFiltering(false);
//    deviceScanner.setDeviceTypes(DeviceType.LOCATION_BEACON);
  }

  @Override protected void onDestroy() {
    deviceScanner.stopScanning();
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();

    if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
      startScanning();
    }
  }

  @Override protected void onPause() {
    deviceScanner.stopScanning();
    super.onPause();
  }

  private void startScanning() {
    toolbar.setSubtitle("Scanning...");
    adapter.replaceWith(Collections.<ConfigurableDevicesScanner.ScanResultItem>emptyList());
    deviceScanner.scanForDevices(new ConfigurableDevicesScanner.ScannerCallback() {

      @Override public void onDevicesFound(final List<ConfigurableDevicesScanner.ScanResultItem> devices) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            // Note that beacons reported here are already sorted by estimated
            // distance between device and beacon.
            toolbar.setSubtitle("Found beacons: " + devices.size());
            adapter.replaceWith(devices);
          }
        });
      }
    });
  }

  private AdapterView.OnItemClickListener createOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
          try {
            Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
            Intent intent = new Intent(ConfigurableDevicesListActivity.this, clazz);
            intent.putExtra(EXTRAS_DEVICE, adapter.getItem(position).device);
            startActivity(intent);
          } catch (ClassNotFoundException e) {
            Log.e(TAG, "Finding class by name failed", e);
          }
        }
      }
    };
  }
}
