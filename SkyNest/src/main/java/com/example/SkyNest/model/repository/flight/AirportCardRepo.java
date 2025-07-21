package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.AirportCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportCardRepo extends JpaRepository<AirportCard,Long> {
    Optional<AirportCard> findByAirportId(Long airportId);

    @Transactional
    @Modifying
    @Query("DELETE FROM AirportCard ac WHERE ac.airport.id = :airportId")
    void deleteByAirportId(@Param("airportId") Long airportId);

}
