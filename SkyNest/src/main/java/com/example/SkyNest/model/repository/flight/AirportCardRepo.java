package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.AirportCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AirportCardRepo extends JpaRepository<AirportCard,Long> {
}
