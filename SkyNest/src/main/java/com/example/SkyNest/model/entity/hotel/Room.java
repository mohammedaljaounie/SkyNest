package com.example.SkyNest.model.entity.hotel;

import com.example.SkyNest.myEnum.RoomStatus;
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
    private int numberOfPerson;
    private int numberOfBed;
    private String isHasKitchen;
    private RoomStatus status;

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

    public RoomStatus isStatus() {
        return status;
    }

    public void setStatus(RoomStatus status) {
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

    public RoomStatus getStatus() {
        return status;
    }
}
