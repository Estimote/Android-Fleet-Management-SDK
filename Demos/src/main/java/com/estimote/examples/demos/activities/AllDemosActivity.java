package com.estimote.examples.demos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.estimote.examples.demos.R;

/**
 * Shows all available demos.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class AllDemosActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.all_demos);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    toolbar.setTitle(getTitle());

    findViewById(R.id.distance_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startListBeaconsActivity(DistanceBeaconActivity.class.getName());
      }
    });

    findViewById(R.id.notify_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startListBeaconsActivity(NotifyDemoActivity.class.getName());
      }
    });

    findViewById(R.id.characteristics_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startListBeaconsActivity(CharacteristicsDemoActivity.class.getName());
      }
    });

    findViewById(R.id.update_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startListBeaconsActivity(UpdateDemoActivity.class.getName());
      }
    });

    findViewById(R.id.eddystone_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(AllDemosActivity.this, ListEddystoneActivity.class);
        intent.putExtra(ListEddystoneActivity.EXTRAS_TARGET_ACTIVITY, EddystoneDemoActivity.class.getName());
        startActivity(intent);
      }
    });

    findViewById(R.id.nearables_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Intent intent = new Intent(AllDemosActivity.this, ListNearablesActivity.class);
        intent.putExtra(ListNearablesActivity.EXTRAS_TARGET_ACTIVITY, NearablesDemoActivity.class.getName());
        startActivity(intent);
      }
    });

    findViewById(R.id.sensors_demo_button).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        startListBeaconsActivity(SensorsActivity.class.getName());
      }
    });
  }

  private void startListBeaconsActivity(String extra) {
    Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
    intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, extra);
    startActivity(intent);
  }
}
