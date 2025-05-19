package com.example.SkyNest.model.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_booking")
public class UserBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate bookingStartDate;
    private LocalDate bookingEndDate;
    private String bookingType;
    private int status;
    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getBookingStartDate() {
        return bookingStartDate;
    }

    public void setBookingStartDate(LocalDate bookingStartDate) {
        this.bookingStartDate = bookingStartDate;
    }

    public LocalDate getBookingEndDate() {
        return bookingEndDate;
    }

    public void setBookingEndDate(LocalDate bookingEndDate) {
        this.bookingEndDate = bookingEndDate;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
