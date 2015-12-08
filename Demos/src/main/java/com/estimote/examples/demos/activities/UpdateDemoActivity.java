package com.estimote.examples.demos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.cloud.model.Firmware;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.BeaconOta;
import com.estimote.sdk.exception.EstimoteDeviceException;
import com.estimote.sdk.exception.EstimoteException;

/**
 * Demo that shows how to update beacon's firmware.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class UpdateDemoActivity extends BaseActivity {

  private Beacon beacon;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView beaconDetailsView;
  private Button updateButton;

  @Override protected int getLayoutResId() {
    return R.layout.update_demo;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    statusView = (TextView) findViewById(R.id.status);
    beaconDetailsView = (TextView) findViewById(R.id.beacon_details);
    updateButton = (Button) findViewById(R.id.update);
    updateButton.setOnClickListener(createUpdateButtonListener());

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    connection = new BeaconConnection(this, beacon, createConnectionCallback());
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (!connection.isConnected()) {
      statusView.setText("Status: Connecting...");
      connection.authenticate();
    }
  }

  @Override
  protected void onDestroy() {
    connection.close();
    super.onDestroy();
  }

  /**
   * Returns click listener on update beacon button.
   */
  private View.OnClickListener createUpdateButtonListener() {
    return new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        updateButton.setVisibility(View.GONE);
        statusView.setText("Status: updating beacon's firmware");
        connection.updateBeacon(new BeaconOta.Callback() {
          @Override
          public void onSuccess() {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                statusView.setText("Status: beacon updated to newest firmware");
                beaconDetailsView.setText(null);
              }
            });
          }

          @Override
          public void onProgress(final float progress, final String text) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                beaconDetailsView.setText("Progress: " + (int) (progress * 100) + "% (" + text + ")");
              }
            });
          }

          @Override
          public void onFailure(final EstimoteException e) {
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                statusView.setText("Status: there was a problem while updating beacon's firmware");
                beaconDetailsView.setText(e.getLocalizedMessage());
              }
            });
          }
        });
      }
    };
  }

  private BeaconConnection.ConnectionCallback createConnectionCallback() {
    return new BeaconConnection.ConnectionCallback() {
      @Override public void onConnected(final BeaconInfo beaconInfo) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to beacon, checking firmware status");
            connection.checkFirmwareUpdate(new BeaconConnection.CheckFirmwareCallback() {
              @Override
              public void onBeaconUpToDate(Firmware firmware) {
                statusView.setText("Status: beacon's firmware up to date");
              }

              @Override
              public void onBeaconNeedsUpdate(Firmware firmware) {
                statusView.setText("Status: there is a new firmware for beacon");
                beaconDetailsView.setText("Current firmware: " + connection.getSoftwareVersion() + "\n" +
                    "New firmware: " + firmware.software);
                updateButton.setVisibility(View.VISIBLE);
              }

              @Override
              public void onError(EstimoteException e) {
                statusView.setText("Status: there was a problem while checking firmware status");
                beaconDetailsView.setText(e.getLocalizedMessage());
              }
            });
          }
        });
      }

      @Override public void onAuthorized(BeaconInfo beaconInfo) {
        //Do nothing
      }

      @Override public void onAuthenticationError(final EstimoteDeviceException exception) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Cannot connect to beacon. \n" +
                "Error: " + exception.getMessage() + "\n" +
                "Did you change App ID and App Token in DemosApplication?");
          }
        });
      }

      @Override public void onDisconnected() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Disconnected from beacon");
          }
        });
      }
    };
  }
}
