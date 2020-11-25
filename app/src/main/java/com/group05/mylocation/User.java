package com.group05.mylocation;

import android.net.Uri;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private Uri image;
    private String email;
    private String fullname;
    private String uid;
    private int age;
    private int gender;
    private int minage;
    private int maxage;
    private int interest;
    private int distance;
    private boolean status;
    private String biography;
    private String phone;
    private String birthday;

    public User() {
        this.image = Uri.parse("drawable://" + R.drawable.sample1);
        this.email = "";
        this.fullname = "fullname";
        this.uid = "";
        this.age = 20;
        this.gender = 1;
        this.minage = 0;
        this.maxage = 100;
        this.interest = 2;
        this.distance = 100;
        this.phone = "034567890";
        this.biography = "Nothing";
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFullName(String fullName) {
        this.fullname = fullName;
    }

    public String getFullName() {
        return fullname;
    }

    public String getEmail() {
        return email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return gender;
    }

    public void setSex(int sex) {
        this.gender = sex;
    }

    public int getMinage() {
        return minage;
    }

    public void setMinage(int minage) {
        this.minage = minage;
    }

    public int getMaxage() {
        return maxage;
    }

    public void setMaxage(int maxage) {
        this.maxage = maxage;
    }

    public int getInterest() {
        return interest;
    }

    public void setInterest(int interest) {
        this.interest = interest;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    static public User MapToUser(Map<String, Object> map, String uid){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        User tmp = gson.fromJson(jsonElement, User.class);
        tmp.setUid(uid);

        return tmp;
    }

    public User(String name, String email, String uid){
        this.fullname = name;
        this.email = email;
        this.uid = uid;
    }

    public Uri getImage() {
        return image;
    }

    public User(Uri image, String name, int age, String bio, String uid) {
        this.image = image;
        this.fullname = name;
        this.age = age;
        this.biography = bio;
        this.uid = uid;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("email", getEmail());
        result.put("fullname", getFullName());
        result.put("minage", getMinage());
        result.put("maxage", getMaxage());
        result.put("gender", getSex());
        result.put("interest", getInterest());
        result.put("age", getAge());
        result.put("distance", getDistance());
        result.put("birthday", getBirthday());
        result.put("phone", getPhone());
        result.put("biography", getBiography());

        return result;
    }

}
