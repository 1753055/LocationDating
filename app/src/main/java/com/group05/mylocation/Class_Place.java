package com.group05.mylocation;

public class Class_Place {
    private String name;
    private String Location;

    public Class_Place(String name, String location) {
        this.name = name;
        Location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
    }
}
