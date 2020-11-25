package com.group05.mylocation.Modal;

public class SavePlaceClass {
    private String longtitude;
    private String latitude;

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public SavePlaceClass(){

    }

    public SavePlaceClass(String longti, String lati){
        this.longtitude = longti;
        this.latitude = lati;
    }
}
