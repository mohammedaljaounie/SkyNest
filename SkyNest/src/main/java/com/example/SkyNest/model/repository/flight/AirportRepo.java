package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.Airport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportRepo extends JpaRepository<Airport,Long> {
}
