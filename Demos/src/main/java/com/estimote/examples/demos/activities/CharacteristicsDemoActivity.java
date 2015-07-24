package com.estimote.examples.demos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Demo that shows how to connect to beacon and change its minor value.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class CharacteristicsDemoActivity extends BaseActivity {

  private Beacon beacon;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView beaconDetailsView;
  private EditText minorEditView;
  private View afterConnectedView;

  @Override protected int getLayoutResId() {
    return R.layout.characteristics_demo;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    statusView = (TextView) findViewById(R.id.status);
    beaconDetailsView = (TextView) findViewById(R.id.beacon_details);
    afterConnectedView = findViewById(R.id.after_connected);
    minorEditView = (EditText) findViewById(R.id.minor);

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    connection = new BeaconConnection(this, beacon, createConnectionCallback());
    findViewById(R.id.update).setOnClickListener(createUpdateButtonListener());
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
   * Returns click listener on update minor button.
   * Triggers update minor value on the beacon.
   */
  private View.OnClickListener createUpdateButtonListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        int minor = parseMinorFromEditView();
        if (minor == -1) {
          showToast("Minor must be a number");
        } else {
          updateMinor(minor);
        }
      }
    };
  }

  /**
   * @return Parsed integer from edit text view or -1 if cannot be parsed.
   */
  private int parseMinorFromEditView() {
    try {
      return Integer.parseInt(String.valueOf(minorEditView.getText()));
    } catch (NumberFormatException e) {
      return -1;
    }
  }

  private void updateMinor(int minor) {
    // Minor value will be normalized if it is not in the range.
    // Minor should be 16-bit unsigned integer.
    connection.edit()
        .set(connection.minor(), minor)
        .commit(new BeaconConnection.WriteCallback() {
          @Override public void onSuccess() {
            runOnUiThread(new Runnable() {
              @Override public void run() {
                showToast("Minor value updated");
              }
            });
          }

          @Override public void onError(EstimoteDeviceException exception) {
            runOnUiThread(new Runnable() {
              @Override public void run() {
                showToast("Minor not updated");
              }
            });
          }
        });
  }

  private BeaconConnection.ConnectionCallback createConnectionCallback() {
    return new BeaconConnection.ConnectionCallback() {
      @Override public void onAuthenticated(final BeaconInfo beaconInfo) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to beacon");
            StringBuilder sb = new StringBuilder()
                .append("Major: ").append(beacon.getMajor()).append("\n")
                .append("Minor: ").append(beacon.getMinor()).append("\n")
                .append("Advertising interval: ").append(connection.advertisingIntervalMillis().get()).append("ms\n")
                .append("Broadcasting power: ").append(connection.broadcastingPower().get()).append(" dBm\n")
                .append("Battery: ").append(connection.getBatteryPercent()).append(" %\n")
                .append("Firmware: ").append(connection.getSoftwareVersion());
            beaconDetailsView.setText(sb.toString());
            minorEditView.setText(String.valueOf(beacon.getMinor()));
            afterConnectedView.setVisibility(View.VISIBLE);
          }
        });
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

  private void showToast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
  }
}
