package com.estimote.examples.demos;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.connection.BeaconConnection;

/**
 * Demo that shows how to connect to beacon and change its minor value.
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class CharacteristicsDemoActivity extends Activity {

  private Beacon beacon;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView beaconDetailsView;
  private EditText minorEditView;
  private View afterConnectedView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.characteristics_demo);
    getActionBar().setDisplayHomeAsUpEnabled(true);

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

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish();
      return true;
    }
    return super.onOptionsItemSelected(item);
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
    connection.writeMinor(minor, new BeaconConnection.WriteCallback() {
      @Override public void onSuccess() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            showToast("Minor value updated");
          }
        });
      }

      @Override public void onError() {
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
      @Override public void onAuthenticated(final BeaconConnection.BeaconCharacteristics beaconChars) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to beacon");
            StringBuilder sb = new StringBuilder()
                .append("Major: ").append(beacon.getMajor()).append("\n")
                .append("Minor: ").append(beacon.getMinor()).append("\n")
                .append("Advertising interval: ").append(beaconChars.getAdvertisingIntervalMillis()).append("ms\n")
                .append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
                .append("Battery: ").append(beaconChars.getBatteryPercent()).append(" %");
            beaconDetailsView.setText(sb.toString());
            minorEditView.setText(String.valueOf(beacon.getMinor()));
            afterConnectedView.setVisibility(View.VISIBLE);
          }
        });
      }

      @Override public void onAuthenticationError() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Cannot connect to beacon. Authentication problems.");
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
