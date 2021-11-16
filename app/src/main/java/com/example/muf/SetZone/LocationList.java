package com.example.muf.SetZone;

import androidx.recyclerview.widget.RecyclerView;

public class LocationList {
    private String name;
    private double distance;
    private double latitude;
    private double longitude;
    private String englishname;

    public LocationList(String Name, double Distance, double Latitude, double Longitude, String Englishname){
        this.name = Name;
        this.englishname = Englishname;
        this.distance = Distance;
        this.latitude = Latitude;
        this.longitude = Longitude;
    }

    public LocationList(){}

    public String getEnglishname() { return englishname; }
    public void setEnglishname(String englishname) { this.englishname = englishname; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

}
