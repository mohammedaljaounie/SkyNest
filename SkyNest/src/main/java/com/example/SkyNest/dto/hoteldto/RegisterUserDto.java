package com.example.SkyNest.dto.hoteldto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RegisterUserDto {
    @Email
    @Column(unique = true)
    private String email;

    @Column(nullable = false,length = 20)
    @Min(value = 8 )
    @Max(value = 20)
    private String password;

    @Column(nullable = false,length = 100)
    @Min(value = 8)
    private String fullName;

    @Column(nullable = false)
    private double longitude;
    @Column(nullable = false)
    private double latitude;

    private String fcmToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

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

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
