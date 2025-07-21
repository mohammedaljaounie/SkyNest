package com.example.SkyNest.dto.airportdto;

public class FlightBookingDetails {

    private Long flightId;
    private int numberOfPerson;


    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getFlightId() {
        return this.flightId;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public int getNumberOfPerson() {
        return this.numberOfPerson;
    }
}
