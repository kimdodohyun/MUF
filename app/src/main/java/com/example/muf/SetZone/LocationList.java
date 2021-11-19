package com.example.muf.SetZone;

import java.util.HashMap;

public class LocationList {
    private double distance;
    private double latitude;
    private double longitude;
    private HashMap<String, String> zonename;

    public LocationList(double Distance, double Latitude, double Longitude, HashMap<String, String> Map){
        this.distance = Distance;
        this.latitude = Latitude;
        this.longitude = Longitude;
        this.zonename = new HashMap<>(Map);
    }

    public LocationList(){}

    public HashMap<String, String> getZonename() { return zonename; }
    public void setZonename(HashMap<String, String> zonename) { this.zonename = zonename; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

}
