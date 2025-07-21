package com.example.SkyNest.dto.airportdto;

import java.time.LocalDate;

public class FilterFlightInfo {

    private String startPoint;
    private String destination;
    private LocalDate userDate;


    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public LocalDate getUserDate() {
        return userDate;
    }

    public void setUserDate(LocalDate userDate) {
        this.userDate = userDate;
    }
}
