package com.example.android.quakereport;

public class Earthquake {

    // @param magnitude earth quake magnitude
    private Double magnitude;

    // @param city place of earthquake
    private String location;

    // @param date , the  date of the earthquake
    private long date;

    // @param url, the url of the earthquake data
    private String url;

    public Earthquake(Double magnitude, String location, long date, String url) {
        this.magnitude = magnitude;
        this.location = location;
        this.date = date;
        this.url = url;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public String getLocation() {
        return location;
    }

    public long getDate() {
        return date;
    }

    public String getUrl() {
        return url;
    }
}
