package com.example.SkyNest.model.entity.hotel;

import com.example.SkyNest.myEnum.TripTypeAndReservation;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.*;

@Entity
@Table(name = "room")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private TripTypeAndReservation roomType;
    private int roomCount;
    private double basePrice;
    private double currentPrice;
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "hotel_id",referencedColumnName = "id")
    private Hotel hotel;

    @ManyToMany(mappedBy = "rooms")
    @JsonIgnore
    private Set<HotelBooking> hotelBookings = new HashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TripTypeAndReservation getRoomType() {
        return roomType;
    }

    public void setRoomType(TripTypeAndReservation roomType) {
        this.roomType = roomType;
    }

    public int getRoomCount() {
        return roomCount;
    }

    public void setRoomCount(int roomCount) {
        this.roomCount = roomCount;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Set<HotelBooking> getHotelBookings() {
        return hotelBookings;
    }

    public void setHotelBookings(Set<HotelBooking> hotelBookings) {
        this.hotelBookings = hotelBookings;
    }


//
//    @Override
//    public boolean equals(Object o){
//        RoomResponse room = (RoomResponse) o;
//        return id.equals(room.getId()) ;
//    }
//
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id);
//    }
//

}
