package com.group05.mylocation.Modal;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {
    private String email;
    private String fullname;
    private String uid;
    private ArrayList<String> liked;
    private ArrayList<String> like;
    private ArrayList<String> conection;
    private ArrayList<Map<String, Object>> place;
    private int age;
    private int sex;
    private int minage;
    private int maxage;
    private int interest;
    private int distance;

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public User(String email, String fullname, String uid, int age, int sex, int minage, int maxage, int interest) {
        this.email = email;
        this.fullname = fullname;
        this.uid = uid;
        this.age = age;
        this.sex = sex;
        this.minage = minage;
        this.maxage = maxage;
        this.interest = interest;
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

    public ArrayList<String> getLike() {
        return like;
    }

    public void setLike(ArrayList<String> like) {
        this.like = like;
    }

    public ArrayList<String> getConection() {
        return conection;
    }

    public void setConection(ArrayList<String> conection) {
        this.conection = conection;
    }

    public ArrayList<Map<String, Object>> getPlace() {
        return place;
    }

    public void setPlace(ArrayList<Map<String, Object>> place) {
        this.place = place;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
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

    public ArrayList<String> getLiked() {
        return liked;
    }

    static public User MapToUser(Map<String, Object> map, String uid){
        Gson gson = new Gson();
        JsonElement jsonElement = gson.toJsonTree(map);
        User tmp = gson.fromJson(jsonElement, User.class);
        tmp.setUid(uid);

        return tmp;
    }
    public void setLiked(ArrayList<String> liked) {
        this.liked = liked;
    }

    public User(){

    }

    public User(String name, String email, String uid){
        this.fullname = name;
        this.email = email;
        this.uid = uid;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> result = new HashMap<>();
        result.put("email", getEmail());
        result.put("fullname", getFullName());
//        result.put("like", getLike());
//        result.put("liked", getLiked());
//        result.put("conection", getConection());
        result.put("minage", getMinage());
        result.put("maxage", getMaxage());
        result.put("sex", getSex());
        result.put("interest", getInterest());
//        result.put("place", getPlace());
        result.put("age", getAge());
//        result.put("distance", getDistance());


        return result;
    }

}
