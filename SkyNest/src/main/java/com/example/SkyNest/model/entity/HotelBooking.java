package com.example.SkyNest.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "hotel_booking")
public class HotelBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numberOfPerson;
    private int numberOfRoom;
    private String roomType;
    private boolean status;
    private LocalDate launchDate;
    private LocalDate departureDate;
    @Min(value = 20)
    @Max(value = 100)
    private int paymentRatio;
    private List<Long> listOfReservedRoomNumbers;

    private double totalAmount;
    private double amountPaid;



    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "hotel_id",referencedColumnName = "id")
    private Hotel hotel;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumberOfPerson() {
        return numberOfPerson;
    }

    public void setNumberOfPerson(int numberOfPerson) {
        this.numberOfPerson = numberOfPerson;
    }

    public int getNumberOfRoom() {
        return numberOfRoom;
    }

    public void setNumberOfRoom(int numberOfRoom) {
        this.numberOfRoom = numberOfRoom;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDate getLaunchDate() {
        return launchDate;
    }

    public void setLaunchDate(LocalDate launchDate) {
        this.launchDate = launchDate;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(LocalDate departureDate) {
        this.departureDate = departureDate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public int getPaymentRatio() {
        return paymentRatio;
    }

    public void setPaymentRatio(int paymentRatio) {
        this.paymentRatio = paymentRatio;
    }

    public List<Long> getListOfReservedRoomNumbers() {
        return listOfReservedRoomNumbers;
    }

    public void setListOfReservedRoomNumbers(List<Long> listOfReservedRoomNumbers) {
        this.listOfReservedRoomNumbers = listOfReservedRoomNumbers;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }
}
