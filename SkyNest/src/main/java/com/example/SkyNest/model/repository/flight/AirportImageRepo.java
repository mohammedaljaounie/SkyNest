package com.example.SkyNest.model.repository.flight;


import com.example.SkyNest.model.entity.flight.AirportImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportImageRepo extends JpaRepository<AirportImage,Long> {
    void deleteAllByAirportId(Long airportId);
}
