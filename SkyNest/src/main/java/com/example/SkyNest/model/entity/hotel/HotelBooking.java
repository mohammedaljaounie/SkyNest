package com.example.SkyNest.model.entity.hotel;

import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.myEnum.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotel_booking")
public class HotelBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int numberOfPerson;
    private int numberOfRoom;
    private StatusEnum status;
    private LocalDate launchDate;
    private LocalDate departureDate;
    @Min(value = 20)
    @Max(value = 100)
    private int paymentRatio;
    private double totalAmount;
    private double amountPaid;
    private double currentTotalAmount;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "hotel_id",referencedColumnName = "id")
    private Hotel hotel;
    @ManyToMany
    @JoinTable(name = "booking_room",joinColumns = @JoinColumn(name = "booking_id"),
     inverseJoinColumns = @JoinColumn(name = "room_id"))
    private Set<Room> rooms = new HashSet<>();

    public HotelBooking() {
    }

    public HotelBooking(int numberOfPerson, int numberOfRoom, StatusEnum status
            , LocalDate launchDate, LocalDate departureDate, int paymentRatio
            , double totalAmount,double currentTotalAmount, double amountPaid, User user, Hotel hotel
            , Set<Room> rooms) {
        this.numberOfPerson = numberOfPerson;
        this.numberOfRoom = numberOfRoom;
        this.status = status;
        this.launchDate = launchDate;
        this.departureDate = departureDate;
        this.paymentRatio = paymentRatio;
        this.totalAmount = totalAmount;
        this.currentTotalAmount = currentTotalAmount;
        this.amountPaid = amountPaid;
        this.user = user;
        this.hotel = hotel;
        this.rooms = rooms;
    }

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

    public StatusEnum isStatus() {
        return status;
    }

    public void setStatus(StatusEnum status) {
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

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }

    public double getCurrentTotalAmount() {
        return currentTotalAmount;
    }

    public void setCurrentTotalAmount(double currentTotalAmount) {
        this.currentTotalAmount = currentTotalAmount;
    }
}
