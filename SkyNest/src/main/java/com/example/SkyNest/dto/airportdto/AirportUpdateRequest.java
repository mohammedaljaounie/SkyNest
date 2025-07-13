package com.example.SkyNest.dto.airportdto;

public class AirportUpdateRequest {
    private String name;
    private String description;
    private Long airportId;

    public void setAirportId(Long airportId) {
        this.airportId = airportId;
    }

    public Long getAirportId() {
        return this.airportId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
