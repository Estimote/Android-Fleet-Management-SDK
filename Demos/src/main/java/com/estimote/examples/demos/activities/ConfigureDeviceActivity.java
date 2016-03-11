package com.estimote.examples.demos.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import com.estimote.examples.demos.R;
import com.estimote.sdk.cloud.model.DeviceFirmware;
import com.estimote.sdk.connection.DeviceConnection;
import com.estimote.sdk.connection.DeviceConnectionCallback;
import com.estimote.sdk.connection.DeviceConnectionProvider;
import com.estimote.sdk.connection.exceptions.DeviceConnectionException;

import com.estimote.sdk.connection.scanner.ConfigurableDevice;
import com.estimote.sdk.connection.settings.Gpio;
import com.estimote.sdk.connection.settings.SettingCallback;
import com.estimote.sdk.connection.settings.Version;

/**
 * Demo how to configure device.
 *
 * @author lukasz.pobereznik@estimote.com (Lukasz Pobereznik)
 */
public class ConfigureDeviceActivity extends BaseActivity {

  private static final String TAG = ConfigureDeviceActivity.class.getSimpleName();

  private ConfigurableDevice device;
  private DeviceConnection connection;
  private DeviceConnectionProvider connectionProvider;
  private Handler refreshHandler;

  private TextView statusView;
  private TextView nameView;
  private TextView appVersionView;
  private TextView hardwareVersionView;
  private TextView uptimeView;
  private TextView temperatureView;
  private TextView lightView;
  private TextView motionView;
  private CheckBox[] gpios;
  private Button updateDevice;

  @Override protected int getLayoutResId() {
    return R.layout.configure_device_demo;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    statusView = (TextView) findViewById(R.id.status);
    nameView = (TextView) findViewById(R.id.name);
    appVersionView = (TextView) findViewById(R.id.app_version);
    hardwareVersionView = (TextView) findViewById(R.id.hardware_version);
    uptimeView = (TextView) findViewById(R.id.uptime);
    temperatureView = (TextView) findViewById(R.id.temperature);
    lightView = (TextView) findViewById(R.id.light);
    motionView = (TextView) findViewById(R.id.motion);
    updateDevice = (Button) findViewById(R.id.update_button);
    device = getIntent().getParcelableExtra(ConfigurableDevicesListActivity.EXTRAS_DEVICE);
    Log.d(TAG, "Connecting to device: " + device.deviceId.toHexString());
    gpios = new CheckBox[2];
    gpios[0] = (CheckBox) findViewById(R.id.gpio_0);
    gpios[1] = (CheckBox) findViewById(R.id.gpio_1);

    connectionProvider = new DeviceConnectionProvider(this);
    refreshHandler = new Handler();
    updateDevice.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        connection.checkForFirmwareUpdate(new DeviceConnection.CheckFirmwareCallback() {

          @Override public void onDeviceUpToDate(DeviceFirmware firmware) {
            setStatus("Firmware is up to date");
            connection.updateDevice(new DeviceConnection.FirmwareUpdateCallback() {
              @Override
              public void onSuccess() {
                setStatus("Device firmware updated");
                connection.reconnect();
              }

              @Override
              public void onProgress(float progress, String message) {
                setStatus("" + (int) (progress * 100.0f) + "%: " + message);
              }

              @Override
              public void onFailure(DeviceConnectionException e) {
                handleError(e);
              }
            });
          }

          @Override public void onDeviceNeedsUpdate(DeviceFirmware firmware) {
            setStatus("Firmware needs update. Latest: " + firmware.software);
            connection.updateDevice(new DeviceConnection.FirmwareUpdateCallback() {
              @Override public void onSuccess() {
                setStatus("Device firmware updated");
                connection.reconnect();
              }

              @Override public void onProgress(float progress, String message) {
                setStatus("" + (int) (progress * 100.0f) + "%: " + message);
              }

              @Override public void onFailure(DeviceConnectionException e) {
                handleError(e);
              }
            });
          }

          @Override public void onError(DeviceConnectionException exception) {
            handleError(exception);
          }
        });
      }
    });

    setStatus("Connecting...");
    connectionProvider.connectToService(new DeviceConnectionProvider.ConnectionProviderCallback() {
      @Override public void onConnectedToService() {
        connection = connectionProvider.getConnection(device);
        if(connection == null) {
          setStatus("Device type not supported: " + device.type);
          return;
        }
        connection.connect(new DeviceConnectionCallback() {
          @Override public void onConnected() {
            setStatus("Connected");
            ConfigureDeviceActivity.this.connection = connection;
            runOnUiThread(new Runnable() {
              @Override public void run() {
                updateDevice.setEnabled(true);
              }
            });
            readSettings();
            readSensors();
          }

          @Override public void onDisconnected() {
            setStatus("Disconnected");
            runOnUiThread(new Runnable() {
              @Override public void run() {
                updateDevice.setEnabled(false);
              }
            });
            connection = null;
          }

          @Override public void onConnectionFailed(DeviceConnectionException exception) {
            setStatus("Connection failed:  " + exception.getMessage());
            connection = null;
          }
        });
      }
    });
  }

  @Override protected void onStart() {
    super.onStart();
  }

  @Override protected void onStop() {
    super.onStop();
    refreshHandler.removeCallbacks(null);
  }

  @Override protected void onDestroy() {
    connectionProvider.destroy();
    super.onDestroy();
  }

  private void readSettings() {
    connection.settings.deviceInfo.firmware().get(new SettingCallback<Version>() {
      @Override public void onSuccess(final Version value) {
        appVersionView.setText("Application version: " + value);
      }

      @Override public void onFailure(DeviceConnectionException exception) {
        handleError(exception);
      }
    });
    connection.settings.deviceInfo.name().get(new SettingCallback<String>() {
      @Override
      public void onSuccess(final String value) {
        nameView.setText("Device name: " + value);
      }

      @Override
      public void onFailure(DeviceConnectionException exception) {
        handleError(exception);
      }
    });
    connection.settings.deviceInfo.hardware().get(new SettingCallback<String>() {
      @Override public void onSuccess(final String value) {
        hardwareVersionView.setText("Hardware version: " + value);
      }

      @Override public void onFailure(DeviceConnectionException exception) {
        handleError(exception);
      }
    });

    if (connection.settings.other.uptime().isAvailable()) {
      connection.settings.other.uptime().get(new SettingCallback<Integer>() {
        @Override public void onSuccess(final Integer value) {
          uptimeView.setText(String.format("Uptime: %dd %02d:%02d:%02d", value / (3600 * 24), (value / 3600) % 24,
              (value / 60) % 60, value % 60));
        }

        @Override public void onFailure(DeviceConnectionException exception) {
          handleError(exception);
        }
      });
    }

    if (connection.settings.gpio.data().isAvailable()) {
      configureGPIOs();
    }

    if (connection.settings.sensors.motion.state().isAvailable()) {
      connection.settings.sensors.motion.enabled().set(true, new SettingCallback<Boolean>() {
        @Override public void onSuccess(Boolean value) {

        }

        @Override public void onFailure(DeviceConnectionException exception) {
          handleError(exception);
        }
      });
      connection.settings.sensors.motion.state().registerStateChangeListener(new SettingCallback<Boolean>() {
        @Override public void onSuccess(final Boolean value) {
          motionView.setText("Device in motion: " + value);
        }

        @Override public void onFailure(DeviceConnectionException exception) {
          handleError(exception);
        }
      });
    }
  }

  private void handleError(DeviceConnectionException exception) {
    setStatus("Error: " + exception.getClass().getSimpleName() + ":" + exception.getMessage());
    Log.e("Demos", "Connection error", exception);
  }

  private void setStatus(final String status) {
    runOnUiThread(new Runnable() {
      @Override public void run() {
        statusView.setText("Status: " + status);
      }
    });
  }

  public void readSensors() {
    if (connection == null) {
      return;
    }
    connection.settings.sensors.temperature.temperature().get(new SettingCallback<Float>() {
      @Override public void onSuccess(final Float value) {
        temperatureView.setText("Temperature: " + value + "C");
      }

      @Override public void onFailure(DeviceConnectionException exception) {
        handleError(exception);
      }
    });
    if (connection.settings.sensors.light.ambientLight().isAvailable()) {
      connection.settings.sensors.light.ambientLight().get(new SettingCallback<Float>() {
        @Override public void onSuccess(final Float value) {
          lightView.setText("Light sensor: " + value + "lux");
        }

        @Override public void onFailure(DeviceConnectionException exception) {
          handleError(exception);
        }
      });
    } else {
      runOnUiThread(new Runnable() {
        @Override public void run() {
          lightView.setText("Light sensor: N/A");
        }
      });
    }
    refreshHandler.postDelayed(new Runnable() {
      @Override public void run() {
        readSensors();
      }
    }, 2000);
  }

  void configureGPIOs() {
    connection.edit()
        .set(connection.settings.gpio.config(Gpio.Pin.GPIO_0), Gpio.PinConfig.OUTPUT)
        .set(connection.settings.gpio.config(Gpio.Pin.GPIO_1), Gpio.PinConfig.OUTPUT)
        .commit(new SettingCallback<Boolean>() {
          @Override public void onSuccess(Boolean value) {
            runOnUiThread(new Runnable() {
              @Override public void run() {
                for (CheckBox checkBox : gpios) {
                  checkBox.setEnabled(true);
                }
              }
            });
            connection.settings.gpio.data().get(new SettingCallback<Byte>() {
              @Override public void onSuccess(final Byte value) {
                runOnUiThread(new Runnable() {
                  @Override public void run() {
                    gpios[0].setChecked((value & 0b0001) > 0);
                    gpios[1].setChecked((value & 0b0010) > 0);
                  }
                });
              }

              @Override public void onFailure(DeviceConnectionException exception) {
                handleError(exception);
              }
            });
          }

          @Override public void onFailure(DeviceConnectionException exception) {
            handleError(exception);
          }
        });
    for (int i = 0; i < gpios.length; i++) {
      final int gpioNum = i;
      gpios[i].setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
          gpios[gpioNum].setTextColor(Color.RED);
          connection.settings.gpio.data(Gpio.Pin.values()[gpioNum])
              .set(gpios[gpioNum].isChecked(), new SettingCallback<Boolean>() {
                @Override public void onSuccess(Boolean value) {
                  runOnUiThread(new Runnable() {
                    @Override public void run() {
                      gpios[gpioNum].setTextColor(Color.BLACK);
                    }
                  });
                }

                @Override public void onFailure(DeviceConnectionException exception) {
                  handleError(exception);
                }
              });
        }
      });
    }
  }
}
