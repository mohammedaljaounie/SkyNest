package com.example.SkyNest.dto;

import java.util.List;

public class RoomResponse {

     private Long id;
     private double basePrice;
     private double currentPrice;

     private int  room_count ;
     private String   room_type;
     private boolean status;
     private String hotelName;
     private String ownerName;
     private List<ImageDTO> imageDTOList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public int getRoom_count() {
        return room_count;
    }

    public void setRoom_count(int room_count) {
        this.room_count = room_count;
    }

    public String getRoom_type() {
        return room_type;
    }

    public void setRoom_type(String room_type) {
        this.room_type = room_type;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public List<ImageDTO> getImageDTOList() {
        return imageDTOList;
    }

    public void setImageDTOList(List<ImageDTO> imageDTOList) {
        this.imageDTOList = imageDTOList;
    }
}
