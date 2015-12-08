package com.estimote.examples.demos.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.EddystonesListAdapter;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.eddystone.Eddystone;
import java.util.Collections;
import java.util.List;

/**
 * Displays list of found eddystones sorted by RSSI.
 * Starts new activity with selected eddystone if activity was provided.
 *
 * @author wiktor.gworek@estimote.com (Wiktor Gworek)
 */
public class ListEddystoneActivity extends BaseActivity {

  private static final String TAG = ListEddystoneActivity.class.getSimpleName();

  public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
  public static final String EXTRAS_EDDYSTONE = "extrasEddystone";

  private static final int REQUEST_ENABLE_BT = 1234;

  private BeaconManager beaconManager;
  private EddystonesListAdapter adapter;

  @Override protected int getLayoutResId() {
    return R.layout.main;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Configure device list.
    adapter = new EddystonesListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);
    list.setOnItemClickListener(createOnItemClickListener());

    //Initialize Beacon Manager
    beaconManager = new BeaconManager(this);
  }

  @Override protected void onDestroy() {
    beaconManager.disconnect();
    super.onDestroy();
  }

  @Override protected void onStart() {
    super.onStart();

    // Check if device supports Bluetooth Low Energy.
    if (!beaconManager.hasBluetooth()) {
      Toast.makeText(this, "Device does not have Bluetooth Low Energy", Toast.LENGTH_LONG).show();
      return;
    }

    // If Bluetooth is not enabled, let user enable it.
    if (!beaconManager.isBluetoothEnabled()) {
      Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
      startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    } else {
      connectToService();
    }
  }

  @Override protected void onStop() {
    beaconManager.disconnect();
    super.onStop();
  }

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_ENABLE_BT) {
      if (resultCode == Activity.RESULT_OK) {
        connectToService();
      } else {
        Toast.makeText(this, "Bluetooth not enabled", Toast.LENGTH_LONG).show();
        toolbar.setSubtitle("Bluetooth not enabled");
      }
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  private void connectToService() {
    toolbar.setSubtitle("Scanning...");
    adapter.replaceWith(Collections.<Eddystone>emptyList());

    beaconManager.setEddystoneListener(new BeaconManager.EddystoneListener() {
      @Override public void onEddystonesFound(List<Eddystone> eddystones) {
        toolbar.setSubtitle("Found beacons with Eddystone protocol: " + eddystones.size());
        adapter.replaceWith(eddystones);
      }
    });

    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override public void onServiceReady() {
        beaconManager.startEddystoneScanning();
      }
    });
  }

  private AdapterView.OnItemClickListener createOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
          try {
            Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
            Intent intent = new Intent(ListEddystoneActivity.this, clazz);
            intent.putExtra(EXTRAS_EDDYSTONE, adapter.getItem(position));
            startActivity(intent);
          } catch (ClassNotFoundException e) {
            Log.e(TAG, "Finding class by name failed", e);
          }
        }
      }
    };
  }
}
