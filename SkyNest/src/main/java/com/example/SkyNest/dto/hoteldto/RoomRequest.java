package com.example.SkyNest.dto.hoteldto;

import com.example.SkyNest.myEnum.TripTypeAndReservation;

public class RoomRequest {

    private TripTypeAndReservation roomType;

    private double price;


    public TripTypeAndReservation getRoomType() {
        return roomType;
    }

    public void setRoomType(TripTypeAndReservation roomType) {
        this.roomType = roomType;
    }



    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }


}
