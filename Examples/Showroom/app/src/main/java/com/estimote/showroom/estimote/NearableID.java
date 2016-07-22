package com.estimote.showroom.estimote;

public class NearableID {

    private String nearableIDString;

    public NearableID(String nearableIDString) {
        this.nearableIDString = nearableIDString;
    }

    public String toString() {
        return nearableIDString;
    }

    public String getNearableIDString() {
        return nearableIDString;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (o == this) {
            return true;
        }

        if (getClass() != o.getClass()) {
            return super.equals(o);
        }

        NearableID other = (NearableID) o;

        return getNearableIDString().equals(other.getNearableIDString());
    }
}
