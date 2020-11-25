package com.group05.mylocation;

public class Tracker {
    String UID;
    String name;
    double location_x;
    double location_y;

    public Tracker(String Uid,String name, double location_x, double location_y){
        this.UID=Uid;
        this.name=name;
        this.location_x=location_x;
        this.location_y=location_y;
    }

    public String getName(){ return name; }
    public double getLocationX(){ return location_x; }
    public double getLocationY(){ return location_y; }
    public String getUid(){return UID;}
    public void setName(String name){
        this.name=name;
    }
    public void setLocationX(double location){
        this.location_x=location;
    }
    public void setLocationY(double location){
        this.location_y=location;
    }
    public void setUid(String Uid){
        this.UID=Uid;
    }
}
