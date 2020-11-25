package com.group05.mylocation.Modal;

import java.util.Calendar;
import java.util.Date;

public class LocationHistory {

    private String address ;
    private String city ;
    //    private String state ;
    private String country ;
    //    private String postalCode ;
//    private String knownName ;
    private Double latitude;
    private Double longitude;
    Date date;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longtitude) {
        this.longitude = longtitude;
    }

    public LocationHistory() {
        longitude = 0.0;
        latitude = 0.0;
//        address = "null";
//        city = "null";
//        postalCode = "null";
//        country = "null";
//        knownName = "null";
//        state = "null";
    }

    public LocationHistory(Double longti, Double lati, String address, String name){
        this.city = name;
        longitude = longti;
        latitude = lati;
        this.address = address;
        this.date = Calendar.getInstance().getTime();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
    //
//    public String getState() {
//        return state;
//    }
//
//    public void setState(String state) {
//        this.state = state;
//    }
//
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
//
//    public String getPostalCode() {
//        return postalCode;
//    }
//
//    public void setPostalCode(String postalCode) {
//        this.postalCode = postalCode;
//    }
//
//    public String getKnownName() {
//        return knownName;
//    }
//
//    public void setKnownName(String knownName) {
//        this.knownName = knownName;
//    }

    @Override
    public String toString() {
        return "" +
                "address='" + address + '\'' +
                ", city='" + city + '\'' +
//                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
//                ", postalCode='" + postalCode + '\'' +
//                ", knownName='" + kN + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", date= " + date.toString();
    }
}
