package com.estimote.proximitycontent.estimote;

import android.util.Log;

import com.estimote.coresdk.cloud.api.CloudCallback;
import com.estimote.coresdk.cloud.api.EstimoteCloud;
import com.estimote.coresdk.cloud.model.BeaconInfo;
import com.estimote.coresdk.cloud.model.Color;
import com.estimote.coresdk.common.exception.EstimoteCloudException;

public class EstimoteCloudBeaconDetailsFactory implements BeaconContentFactory {

    private static final String TAG = "BeaconDetailsFactory";

    @Override
    public void getContent(final BeaconID beaconID, final Callback callback) {
        EstimoteCloud.getInstance().fetchBeaconDetails(
                beaconID.getProximityUUID(), beaconID.getMajor(), beaconID.getMinor(),
                new CloudCallback<BeaconInfo>() {

            @Override
            public void success(BeaconInfo beaconInfo) {
                callback.onContentReady(new EstimoteCloudBeaconDetails(
                        beaconInfo.name, beaconInfo.color));
            }

            @Override
            public void failure(EstimoteCloudException e) {
                Log.e(TAG, "Couldn't fetch data from Estimote Cloud for beacon " + beaconID
                        + ", will use default values instead. Double-check if the app ID and app "
                        + "token provided in the MyApplication are correct, and if the beacon with "
                        + "such ID is assigned to your Estimote Account. The error was: "
                        + e.toString());
                callback.onContentReady(new EstimoteCloudBeaconDetails("beacon", Color.UNKNOWN));
            }
        });
    }
}
