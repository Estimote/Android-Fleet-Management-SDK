package com.estimote.examples.demos.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.eddystone.Eddystone;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Demo that shows how to connect to eddystone and change its url or namespace values depending on Eddystone type
 * (Eddystone-UID or Eddystone-URL).
 *
 * @author wiktor@estimote.com (Wiktor Gworek)
 */
public class EddystoneDemoActivity extends BaseActivity {

  private Eddystone eddystone;
  private BeaconConnection connection;

  private TextView statusView;
  private TextView eddystoneDetailsView;
  private EditText eddystoneEditView;
  private View afterConnectedView;

  @Override protected int getLayoutResId() {
    return R.layout.eddystones_demo;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    statusView = (TextView) findViewById(R.id.status);
    eddystoneDetailsView = (TextView) findViewById(R.id.eddystone_details);
    TextView eddystoneIdLabel = (TextView) findViewById(R.id.eddystone_id_label);
    afterConnectedView = findViewById(R.id.after_connected);
    eddystoneEditView = (EditText) findViewById(R.id.eddystone_id);

    eddystone = getIntent().getParcelableExtra(ListEddystoneActivity.EXTRAS_EDDYSTONE);
    connection = new BeaconConnection(this, eddystone.macAddress, createConnectionCallback());

    eddystoneIdLabel.setText(eddystone.isUrl() ? "Eddystone's url" : "Eddystone's namespace");
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

  private BeaconConnection.ConnectionCallback createConnectionCallback() {
    return new BeaconConnection.ConnectionCallback() {
      @Override public void onAuthenticated(final BeaconInfo beaconInfo) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Connected to eddystone");
            StringBuilder sb = new StringBuilder()
                .append("Url: ").append(eddystone.isUrl() ? eddystone.url : "-").append("\n")
                .append("Namespace: ").append(eddystone.isUid() ? eddystone.namespace : "-").append("\n")
                .append("Instance ID: ").append(eddystone.isUid() ? eddystone.instance : "-").append("\n")
                .append("Broadcasting power: ").append(connection.broadcastingPower().get()).append(" dBm\n")
                .append("Battery: ").append(connection.getBatteryPercent()).append(" %\n")
                .append("Firmware: ").append(connection.getSoftwareVersion());
            eddystoneDetailsView.setText(sb.toString());
            eddystoneEditView.setText(String.valueOf(eddystone.isUrl() ? eddystone.url : eddystone.namespace));
            afterConnectedView.setVisibility(View.VISIBLE);
          }
        });
      }

      @Override public void onAuthenticationError(final EstimoteDeviceException exception) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Cannot connect to eddystone. \n" +
                "Error: " + exception.getMessage() + "\n" +
                "Did you change App ID and App Token in DemosApplication?");
          }
        });
      }

      @Override public void onDisconnected() {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Status: Disconnected from eddystone");
          }
        });
      }
    };
  }

  /**
   * Returns click listener on update minor button.
   * Triggers namespace or url value update on the eddystone.
   */
  private View.OnClickListener createUpdateButtonListener() {
    return new View.OnClickListener() {
      @Override public void onClick(View v) {
        String newValue = eddystoneEditView.getText().toString();
          updateEddystone(newValue);
      }
    };
  }

  private void updateEddystone(String value) {
    connection.edit()
        .set(eddystone.isUrl() ? connection.eddystoneUrl() : connection.eddystoneNamespace(), value)
        .commit(new BeaconConnection.WriteCallback() {
          @Override public void onSuccess() {
            runOnUiThread(new Runnable() {
              @Override public void run() {
                showToast("Value updated");
              }
            });
          }

          @Override public void onError(final EstimoteDeviceException exception) {
            runOnUiThread(new Runnable() {
              @Override public void run() {
                showToast(exception.getLocalizedMessage());
              }
            });
          }
        });
  }

  private void showToast(String text) {
    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
  }
}
