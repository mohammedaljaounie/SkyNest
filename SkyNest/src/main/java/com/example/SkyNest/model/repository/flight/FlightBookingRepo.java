package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.FlightBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightBookingRepo extends JpaRepository<FlightBooking,Long> {
}
