package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.AirportRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AirportRatingRepo extends JpaRepository<AirportRating,Long> {
    Optional<AirportRating> findByUserId(Long userId);

    @Query("select  sum(a.rating) from AirportRating a where a.airport.id = :airportId ")
    int sumRating(Long airportId);

    void deleteAllByAirportId(Long airportId);
}
