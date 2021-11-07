package com.example.muf.model;

public class Location {
    private double latitude, longitude;
    private String uid;

    public Location(){}

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }
}
