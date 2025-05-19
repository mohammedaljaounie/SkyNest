package com.example.SkyNest.dto;

import java.time.LocalDate;

public class UserBookingResponse {
    private Long id;
    private LocalDate bookingStartDate;
    private LocalDate bookingEndDate;
    private String bookingType;


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

}
