package com.example.SkyNest.dto;

public class PlaceNearTheHotelRequest {

    private String placeName;
    private String description;
    private Long hotelId;

    public  PlaceNearTheHotelRequest(){
        this.description = null;
        this.placeName   = null;
        this.hotelId = 0L;
    }

    public PlaceNearTheHotelRequest(String placeName,String description , Long hotelId){
        this.placeName = placeName ;
        this.description = description ;
        this.hotelId = hotelId ;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getHotelId() {
        return hotelId;
    }

    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}
