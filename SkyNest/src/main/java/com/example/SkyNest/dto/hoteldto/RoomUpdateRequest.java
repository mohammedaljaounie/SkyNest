package com.example.SkyNest.dto.hoteldto;

import com.example.SkyNest.myEnum.RoomStatus;
import com.example.SkyNest.myEnum.TripTypeAndReservation;

public class RoomUpdateRequest {
    private double  price;
    private int  room_count ;
    private TripTypeAndReservation room_type;
    private RoomStatus status;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getRoom_count() {
        return room_count;
    }

    public void setRoom_count(int room_count) {
        this.room_count = room_count;
    }

    public TripTypeAndReservation getRoom_type() {
        return room_type;
    }

    public void setRoom_type(TripTypeAndReservation room_type) {
        this.room_type = room_type;
    }

    public RoomStatus isStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
        this.status = status;
    }
}
