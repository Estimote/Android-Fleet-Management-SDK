package com.estimote.examples.demos.activities;

import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.sdk.Beacon;
import com.estimote.sdk.cloud.model.BeaconInfo;
import com.estimote.sdk.connection.BeaconConnection;
import com.estimote.sdk.connection.MotionState;
import com.estimote.sdk.connection.Property;
import com.estimote.sdk.exception.EstimoteDeviceException;

/**
 * Displays activity for monitoring sensor status (temperature and motion).
 *
 * @author lukasz.pobereznik@estimote.com (Lukasz Pobereznik)
 */
public class SensorsActivity extends BaseActivity {
  private static final String TAG = SensorsActivity.class.getSimpleName();

  private TextView temperatureView;
  private TextView motionView;
  private Beacon beacon;
  private BeaconConnection connection;
  private TextView statusView;
  private Handler temperatureRefreshHandler;

  @Override protected int getLayoutResId() {
    return R.layout.sensor_demo;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    statusView = (TextView) findViewById(R.id.status_view);
    temperatureView = (TextView) findViewById(R.id.temperature);
    motionView = (TextView) findViewById(R.id.motion);
    temperatureRefreshHandler = new Handler();

    beacon = getIntent().getParcelableExtra(ListBeaconsActivity.EXTRAS_BEACON);
    connection = new BeaconConnection(this, beacon, new BeaconConnection.ConnectionCallback() {
      @Override public void onAuthorized(BeaconInfo beaconInfo) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Authorized. Connecting...");
          }
        });
      }

      @Override public void onConnected(BeaconInfo beaconInfo) {
        runOnUiThread(new Runnable() {
          @Override public void run() {
            statusView.setText("Connected");
          }
        });
        // First step after connection is to enable motion detection on beacon. Otherwise no
        // motion notifications will be sent.
        connection.edit().set(connection.motionDetectionEnabled(), true).commit(new BeaconConnection.WriteCallback() {
          @Override public void onSuccess() {
            // After on beacon connect all values are read so we can read them immediately and update UI.
            setMotionText(connection.motionDetectionEnabled().get() ? connection.motionState().get() : null);
            setTemperature(connection.temperature().get());
            // Motion sensor sends status updates on physical state change.
            enableMotionListner();
            // Temperature must be read periodically.
            refreshTemperature();
          }

          @Override public void onError(EstimoteDeviceException exception) {
            showToast("Failed to enable motion detection");
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
    });
  }

  private void refreshTemperature() {
    connection.temperature().getAsync(new Property.Callback<Float>() {
      @Override public void onValueReceived(final Float value) {
        if (isDestroyed()) {
          return;
        }
        setTemperature(value);
        // Schedule next temperature read in 2s.
        temperatureRefreshHandler.postDelayed(new Runnable() {
          @Override public void run() {
            refreshTemperature();
          }
        }, 2000);
      }

      @Override public void onFailure() {
        showToast("Unable to read temperature from beacon");
      }
    });
  }

  private void enableMotionListner() {
    connection.setMotionListener(new Property.Callback<MotionState>() {
      @Override public void onValueReceived(final MotionState value) {
        setMotionText(value);
      }

      @Override public void onFailure() {
        showToast("Unable to register motion listener");
      }
    });
  }

  private void setMotionText(final MotionState motionState) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        if (motionState != null) {
          motionView.setText(motionState == MotionState.MOVING ? "In Motion" : "Not in motion");
        } else {
          motionView.setText("Disabled");
        }
      }
    });
  }

  @Override protected void onPause() {
    if (connection != null) {
      connection.setMotionListener(null);
    }
    temperatureRefreshHandler.removeCallbacks(null);
    super.onPause();
  }

  @Override protected void onResume() {
    super.onResume();
    if (!connection.isConnected()) {
      statusView.setText("Status: Connecting...");
      connection.authenticate();
    } else {
      enableMotionListner();
      refreshTemperature();
    }
  }

  @Override protected void onDestroy() {
    connection.close();
    super.onDestroy();
  }

  private void showToast(final String text) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void setTemperature(final Float value) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        temperatureView.setText(String.format("%.1f\u2103", value));
      }
    });
  }
}
