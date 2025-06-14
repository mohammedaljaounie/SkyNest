package com.example.SkyNest.dto;



public class UserInfo {

     private Long id;

     private String fullName;

     private String email;


     private  double longitude;

     private double latitude;
     private int level;

    public UserInfo() {
    }

    public UserInfo(Long id, String fullName, String email, double latitude , double longitude, int level) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.longitude = longitude;
        this.latitude = latitude;
        this.level = level;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
