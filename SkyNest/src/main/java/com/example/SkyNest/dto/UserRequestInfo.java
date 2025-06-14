package com.example.SkyNest.dto;

import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public class UserRequestInfo {

    @Length(min = 8,max = 20)
    @Min(value = 8)
    private String password;

    private String fullName;

    private double longitude;

    private double latitude;


    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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
}
