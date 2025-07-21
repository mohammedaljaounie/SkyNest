package com.example.SkyNest.dto.hoteldto;

import java.util.List;
import java.util.Objects;

public class RoomResponse {

     private Long id;
     private double basePrice;
     private double currentPrice;
     private int  room_count ;
     private String   room_type;
     private String status;
     private String hotelName;
     private String ownerName;
     private int numberOfPerson;
     private int numberOfBed;
     private String isHasKitchen;
     private List<ImageDTO> imageDTOList;


     public RoomResponse(){

     }
    public RoomResponse(Long id) {
        this.id = id;
    }

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

    public String isStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getStatus() {
        return status;
    }

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public int getNumberOfBed() {
        return numberOfBed;
    }

    public void setNumberOfBed(int numberOfBed) {
        this.numberOfBed = numberOfBed;
    }

    public String getIsHasKitchen() {
        return isHasKitchen;
    }

    public void setIsHasKitchen(String isHasKitchen) {
        this.isHasKitchen = isHasKitchen;
    }

    @Override
    public boolean equals(Object o){
        if (!(o instanceof RoomResponse roomResponse)) return false;
        return id.equals(roomResponse.id) &&
                basePrice==roomResponse.basePrice&&
                currentPrice==roomResponse.currentPrice&&
                room_count==roomResponse.room_count&&
                room_type.equals(roomResponse.room_type)&&
                status==roomResponse.status&&
                hotelName.equals(roomResponse.hotelName)&&
                ownerName.equals(roomResponse.ownerName)&&
                numberOfPerson==roomResponse.numberOfPerson&&
                numberOfBed==roomResponse.numberOfBed&&
                isHasKitchen.equals(roomResponse.isHasKitchen)&&
                imageDTOList.size()==roomResponse.imageDTOList.size();
    }


    @Override
    public int hashCode(){
        return Objects.hash(id,basePrice,currentPrice,room_count,room_type,status,hotelName,ownerName,numberOfPerson,numberOfBed,isHasKitchen,imageDTOList.size());
    }



}
