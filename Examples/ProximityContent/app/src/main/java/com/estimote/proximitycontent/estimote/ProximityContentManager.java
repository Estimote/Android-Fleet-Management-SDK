package com.estimote.proximitycontent.estimote;

import android.content.Context;

import java.util.List;

public class ProximityContentManager {

    private NearestBeaconManager nearestBeaconManager;

    private Listener listener;

    public ProximityContentManager(Context context,
                                   List<BeaconID> beaconIDs,
                                   BeaconContentFactory beaconContentFactory) {
        final BeaconContentCache beaconContentCache = new BeaconContentCache(beaconContentFactory);

        nearestBeaconManager = new NearestBeaconManager(context, beaconIDs);
        nearestBeaconManager.setListener(new NearestBeaconManager.Listener() {
            @Override
            public void onNearestBeaconChanged(BeaconID beaconID) {
                if (listener == null) {
                    return;
                }

                if (beaconID != null) {
                    beaconContentCache.getContent(beaconID, new BeaconContentFactory.Callback() {
                        @Override
                        public void onContentReady(Object content) {
                            listener.onContentChanged(content);
                        }
                    });
                } else {
                    listener.onContentChanged(null);
                }
            }
        });

    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onContentChanged(Object content);
    }

    public void startContentUpdates() {
        nearestBeaconManager.startNearestBeaconUpdates();
    }

    public void stopContentUpdates() {
        nearestBeaconManager.stopNearestBeaconUpdates();
    }

    public void destroy() {
        nearestBeaconManager.destroy();
    }
}
