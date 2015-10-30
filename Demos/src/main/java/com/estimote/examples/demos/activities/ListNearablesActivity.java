package com.estimote.examples.demos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.estimote.examples.demos.R;
import com.estimote.examples.demos.adapters.NearableListAdapter;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import com.estimote.sdk.SystemRequirementsChecker;
import java.util.Collections;
import java.util.List;

/**
 * Displays list of found nearables sorted by RSSI.
 * Starts new activity with selected nearable if activity was provided.
 *
 * @author wiktor.gworek@estimote.com (Wiktor Gworek)
 */
public class ListNearablesActivity extends BaseActivity {

  private static final String TAG = ListNearablesActivity.class.getSimpleName();

  public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
  public static final String EXTRAS_NEARABLE = "extrasNearable";

  private BeaconManager beaconManager;
  private NearableListAdapter adapter;

  @Override protected int getLayoutResId() {
    return R.layout.main;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Configure device list.
    adapter = new NearableListAdapter(this);
    ListView list = (ListView) findViewById(R.id.device_list);
    list.setAdapter(adapter);
    list.setOnItemClickListener(createOnItemClickListener());

    //Initialize Beacon Manager
    beaconManager = new BeaconManager(this);
  }

  @Override
  protected void onDestroy() {
    beaconManager.disconnect();
    super.onDestroy();
  }

  @Override protected void onResume() {
    super.onResume();

    if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
      startScanning();
    }
  }

  @Override
  protected void onStop() {
    beaconManager.disconnect();
    super.onStop();
  }

  protected void startScanning() {
    toolbar.setSubtitle("Scanning...");
    adapter.replaceWith(Collections.<Nearable>emptyList());

    beaconManager.setNearableListener(new BeaconManager.NearableListener() {
      @Override public void onNearablesDiscovered(List<Nearable> nearables) {
        toolbar.setSubtitle("Found nearables: " + nearables.size());
        adapter.replaceWith(nearables);
      }
    });

    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override
      public void onServiceReady() {
        beaconManager.startNearableDiscovery();
      }
    });
  }

  private AdapterView.OnItemClickListener createOnItemClickListener() {
    return new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY) != null) {
          try {
            Class<?> clazz = Class.forName(getIntent().getStringExtra(EXTRAS_TARGET_ACTIVITY));
            Intent intent = new Intent(ListNearablesActivity.this, clazz);
            intent.putExtra(EXTRAS_NEARABLE, adapter.getItem(position));
            startActivity(intent);
          } catch (ClassNotFoundException e) {
            Log.e(TAG, "Finding class by name failed", e);
          }
        }
      }
    };
  }

}
