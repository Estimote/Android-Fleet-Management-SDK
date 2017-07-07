package com.estimote.proximitycontent.estimote;


import com.estimote.coresdk.cloud.model.Color;

public class EstimoteCloudBeaconDetails {

    private String beaconName;
    private Color beaconColor;

    public EstimoteCloudBeaconDetails(String beaconName, Color beaconColor) {
        this.beaconName = beaconName;
        this.beaconColor = beaconColor;
    }

    public String getBeaconName() {
        return beaconName;
    }

    public Color getBeaconColor() {
        return beaconColor;
    }

    @Override
    public String toString() {
        return "[beaconName: " + getBeaconName() + ", beaconColor: " + getBeaconColor() + "]";
    }
}
