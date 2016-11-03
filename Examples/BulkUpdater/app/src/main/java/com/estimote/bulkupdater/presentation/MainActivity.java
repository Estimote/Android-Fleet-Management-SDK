package com.estimote.bulkupdater.presentation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.estimote.bulkupdater.model.UpdateStatus;
import com.estimote.bulkupdater.R;
import com.estimote.sdk.DeviceId;
import com.estimote.sdk.SystemRequirementsChecker;
import com.estimote.sdk.connection.exceptions.DeviceConnectionException;
import com.estimote.sdk.connection.scanner.BulkUpdater;
import com.estimote.sdk.connection.scanner.ConfigurableDevice;
import com.estimote.sdk.connection.scanner.ConfigurableDevicesScanner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Bulk updater!
 *
 * This is a simple example for updating all your beacons in range.
 * Please report all bugs/problems or questions you have.
 *
 * @author Pawel Dylag (pawel.dylag@estimote.com)
 */
public class MainActivity extends AppCompatActivity {

  protected Toolbar toolbar;

  private TextView detailsTextView;

  private RecyclerView recyclerView;
  private DevicesUpdateAdapter adapter;

  private ConfigurableDevicesScanner deviceScanner;

  private int updatedDevicesCount;
  private int allDevicesCount;

  ConfigurableDevicesScanner.ScannerCallback scanCallback = new ConfigurableDevicesScanner.ScannerCallback() {
    @Override
    public void onDevicesFound(final List<ConfigurableDevicesScanner.ScanResultItem> devices) {
      // Do here anything you want with scan results.
      // In this example we don't need to use any scan result, so we leave empty space here
    }
  };

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_main);

    toolbar = (Toolbar) findViewById(R.id.toolbar);

    toolbar.setTitle("Bulk updater");
    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        onBackPressed();
      }
    });

    // bind your views here
    detailsTextView = (TextView) findViewById(R.id.textViewDetails);
    recyclerView = (RecyclerView) findViewById(R.id.recycler);

    // use a linear layout manager
    recyclerView.setLayoutManager(new LinearLayoutManager(this));

    // setup view adapter
    adapter = new DevicesUpdateAdapter();
    recyclerView.setAdapter(adapter);

    // Setup configurable devices scanner. It will scan for devices that are available for you to connect.
    // Remember to setup your AppID and AppToken in MyApplicaton class
    deviceScanner = new ConfigurableDevicesScanner(this);

    // Enable bulk updating on our scanner and pass created callback to it
    deviceScanner.enableBulkFirmwareUpdate(createBulkUpdaterCallback());
  }

  @Override
  protected void onDestroy() {
    deviceScanner.disableBulkFirmwareUpdate();
    deviceScanner.stopScanning();
    super.onDestroy();
  }

  @Override
  protected void onStart() {
    super.onStart();
    // Setup requirements checker (bluetooth, permissions etc.)
    if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
      changeSubtitle("Scanning...");
      deviceScanner.scanForDevices(scanCallback);
    }
  }

  @Override
  protected void onStop() {
    super.onStop();
  }


  /**
   * Returns main callback for handling bulk updater events -> device status change, failures, errors etc.
   */
  private BulkUpdater.BulkUpdaterCallback createBulkUpdaterCallback () {
    return new BulkUpdater.BulkUpdaterCallback() {

      /**
       * This callback is called after BU gets list of devices to update from cloud.
       * It is strictly for UI purpose -> In this case you can show info about devices to update and their statuses.
       * @param devicesToUpdate List of devices to look for by BU
       * @param initialStatus Initial status being set to each device
       */
      @Override
      public void onReceivedDevicesToUpdate(final List<DeviceId> devicesToUpdate, final BulkUpdater.Status initialStatus) {
        // You can change this code here to your own implementation
        List<UpdateStatus> statusList = new ArrayList<>(devicesToUpdate.size());
        for (DeviceId id : devicesToUpdate) {
          statusList.add(new UpdateStatus(id, initialStatus, ""));
        }
        allDevicesCount = devicesToUpdate.size();
        adapter.setDataset(statusList);
        // update views
        updateCounter(0, allDevicesCount);
      }

      /**
       * This callback is called when BU is interacting with individual device.
       * @param device device which is being interacted with BU. It contains ID and MAC address.
       * @param newStatus Actual status
       * @param message Additional message. Mostly used for firmware update progress (after establishing connection(
       */
      @Override
      public void onDeviceStatusChange(final ConfigurableDevice device, BulkUpdater.Status newStatus, final String message) {
        adapter.changeStatus(device.deviceId, newStatus, message);
        switch(newStatus){
          case SUCCEEDED:
            updateCounter(++updatedDevicesCount, allDevicesCount);
            break;
          // you can handle different status here
        }
      }

      /**
       * When BU finishes its job.
       * @param updatedCount devices successfully updated
       * @param failedCount devices failed to update
       */
      @Override
      public void onFinished(final int updatedCount, final int failedCount) {
        // You can change this code here to your own implementation
        changeSubtitle("Finished");
      }

      /**
       * Called on any error with BU
       * @param e exception
       */
      @Override
      public void onError(final DeviceConnectionException e) {
        // log error
        changeSubtitle("Error");
      }
    };
  }

  /**
   * Updates main progress view.
   */
  private void updateCounter(final int updated, final int all) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        detailsTextView.setText(updated + " / " + all);
      }
    });
  }

  /**
   * Changes subtitle on toolbar
   * @param msg new message
   */
  private void changeSubtitle(final String msg) {
    runOnUiThread(new Runnable() {
      @Override
      public void run() {
        toolbar.setSubtitle(msg);
      }
    });
  }


}