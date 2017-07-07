package com.estimote.bulkupdater.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.estimote.bulkupdater.R;
import com.estimote.bulkupdater.model.UpdateStatus;
import com.estimote.coresdk.common.exception.EstimoteException;
import com.estimote.coresdk.common.requirements.SystemRequirementsChecker;
import com.estimote.coresdk.recognition.packets.ConfigurableDevice;
import com.estimote.coresdk.service.BeaconManager;
import com.estimote.mgmtsdk.feature.bulk_updater.BulkUpdater;
import com.estimote.mgmtsdk.feature.bulk_updater.BulkUpdaterBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Bulk updater!
 * <p>
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

    private BeaconManager beaconManager;
    private BulkUpdater bulkUpdater;


    private int updatedDevicesCount;
    private int allDevicesCount;
    private List<UpdateStatus> statusList = new ArrayList<>();

    BeaconManager.ConfigurableDevicesListener configurableDevicesListener = new BeaconManager.ConfigurableDevicesListener() {

        @Override
        public void onConfigurableDevicesFound(List<ConfigurableDevice> devices) {
            // Pass scan results to BulkUpdater
            bulkUpdater.onDevicesFound(devices);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Bulk updater");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // setup view bulkUpdater
        bulkUpdater = new BulkUpdaterBuilder(this)
                .withCloudFetchInterval(5, TimeUnit.SECONDS)
                .withFirmwareUpdate()
                .withRetryCount(3)
                .withTimeout(0)
                .build();

        // Setup beacon manager. It will scan for devices that are available for you to connect.
        // Remember to setup your AppID and AppToken in MyApplicaton class
        beaconManager = new BeaconManager(this);

        // set foreground scan periods. This one will scan for 2s and wait 2s
        beaconManager.setForegroundScanPeriod(2000, 2000);

    }

    @Override
    protected void onDestroy() {
        bulkUpdater.destroy();
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Setup requirements checker (bluetooth, permissions etc.)
        if (SystemRequirementsChecker.checkWithDefaultDialogs(this)) {
            changeSubtitle("Scanning...");
            bulkUpdater.start(createBulkUpdaterCallback());
            beaconManager.connect(new BeaconManager.ServiceReadyCallback() {

                @Override
                public void onServiceReady() {
                    beaconManager.setConfigurableDevicesListener(configurableDevicesListener);
                    // Enable configurable devices discovery
                    beaconManager.startConfigurableDevicesDiscovery();
                }
            });

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Returns main callback for handling bulk updater events -> device status change, failures, errors etc.
     */
    private BulkUpdater.BulkUpdaterCallback createBulkUpdaterCallback() {
        return new BulkUpdater.BulkUpdaterCallback() {

            /**
             * This callback is called when BU is interacting with individual device.
             * @param device device which is being interacted with BU. It contains ID and MAC address.
             * @param newStatus Actual status
             * @param message Additional message. Mostly used for firmware update progress (after establishing connection(
             */
            @Override
            public void onDeviceStatusChange(ConfigurableDevice device, BulkUpdater.Status newStatus, String message) {
                // You can change this code here to your own implementation

                switch (newStatus) {
                    case PENDING_UPDATE:
                        if (adapter.isDeviceOnList(device.deviceId)) {
                            updateCounter(--updatedDevicesCount, allDevicesCount);
                        }
                        break;
                    case SUCCEED:
                        updateCounter(++updatedDevicesCount, allDevicesCount);
                        message = "Device updated";
                        break;
                    // you can handle different status here
                }

                // update views
                if (adapter.isDeviceOnList(device.deviceId)) {
                    adapter.changeStatus(device.deviceId, newStatus, message);
                } else {
                    statusList.add(new UpdateStatus(device.deviceId, newStatus, message));
                    adapter.setDataset(statusList);
                    ++allDevicesCount;
                }

                updateCounter(updatedDevicesCount, allDevicesCount);

            }

            /**
             * When BU finishes its job.
             */
            @Override
            public void onFinished() {
                // You can change this code here to your own implementation
                changeSubtitle("Finished");
            }

            /**
             * Called on any error with BU
             * @param e exception
             */
            @Override
            public void onError(EstimoteException e) {
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
     *
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