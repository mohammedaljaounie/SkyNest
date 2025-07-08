package com.example.SkyNest.dto;

import java.util.List;

public class PlaceNearHotelResponse {
    private Long placeId;
    private String placeName;
    private String placeDescription;
    private List<ImageDTO> imagePlaceList;


    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return placeName;
    }

    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }

    public String getPlaceDescription() {
        return placeDescription;
    }

    public void setPlaceDescription(String placeDescription) {
        this.placeDescription = placeDescription;
    }

    public List<ImageDTO> getImagePlaceList() {
        return imagePlaceList;
    }

    public void setImagePlaceList(List<ImageDTO> imagePlaceList) {
        this.imagePlaceList = imagePlaceList;
    }
}
