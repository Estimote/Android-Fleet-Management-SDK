package com.estimote.examples.demos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.estimote.examples.demos.R;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Nearable;
import java.util.List;

/**
 * Demo that shows nearable details. Moving nearable updates displayed details.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class NearablesDemoActivity extends BaseActivity {

  private static final String TAG = NearablesDemoActivity.class.getSimpleName();

  private Nearable currentNearable;
  private BeaconManager beaconManager;

  @Override protected int getLayoutResId() {
    return R.layout.nearable_demo;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    currentNearable = getIntent().getExtras().getParcelable(ListNearablesActivity.EXTRAS_NEARABLE);
    displayCurrentNearableInfo();

    beaconManager = new BeaconManager(this);
  }

  @Override protected void onResume() {
    super.onResume();
    beaconManager.setNearableListener(new BeaconManager.NearableListener() {
      @Override public void onNearablesDiscovered(List<Nearable> nearables) {
        updateCurrentNearable(nearables);
        displayCurrentNearableInfo();
      }
    });

    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
      @Override public void onServiceReady() {
        beaconManager.startNearableDiscovery();
      }
    });
  }

  @Override protected void onStop() {
    beaconManager.disconnect();
    super.onStop();
  }

  private void displayCurrentNearableInfo() {
    StringBuilder builder = new StringBuilder()
        .append("Identifier: ").append(currentNearable.identifier).append("\n")
        .append("Major: ").append(currentNearable.region.getMajor()).append("\n")
        .append("Minor: ").append(currentNearable.region.getMinor()).append("\n")
        .append("Advertising interval: ").append("2000").append("ms\n")
        .append("Broadcasting power: ").append(currentNearable.power.powerInDbm).append(" dBm\n")
        .append("Battery level: ").append(currentNearable.batteryLevel.toString()).append("\n")
        .append("Firmware: ").append(currentNearable.firmwareVersion).append("\n\n")
        .append("Temperature: ").append(String.format("%.1f\u00b0C", currentNearable.temperature)).append("\n")
        .append("In Motion: ").append(currentNearable.isMoving ? "Yes" : "No").append("\n\n")
        .append(String.format("Motion Data: x: %.0f   y: %.0f   z: %.0f", currentNearable.xAcceleration, currentNearable.yAcceleration, currentNearable.zAcceleration)).append("\n")
        .append("Orientation: ").append(currentNearable.orientation.toString());

    TextView infoText = (TextView) findViewById(R.id.nearable_static_details);
    infoText.setText(builder.toString());
  }

  private void updateCurrentNearable(List<Nearable> nearables) {
    for (Nearable nearable : nearables) {
      if (nearable.equals(currentNearable)) {
        currentNearable = nearable;
      }
    }
  }
}
