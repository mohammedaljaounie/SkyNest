package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.model.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;

@Repository
public interface AirportRepo extends JpaRepository<Airport,Long> {


    @Query("select a from Airport a order by a.avgRating desc")
    List<Airport> getAirportOrderByRating();

    @Query("select a from Airport a where a.name like  CONCAT('%', :airportName, '%') ")
    Optional<Airport> findByName(String airportName);

    @Query(value = """
    SELECT a.*, (
        6371 * acos(
            cos(radians(:userLat)) * cos(radians(a.latitude)) *
            cos(radians(a.longitude) - radians(:userLng)) +
            sin(radians(:userLat)) * sin(radians(a.latitude))
        )
    ) AS distance
    FROM Airport a 
    HAVING distance <= :radiusKm
    ORDER BY distance ASC
    LIMIT :limitCount
    """, nativeQuery = true)
    List<Airport> findNearAirport(
            @Param("userLat") double userLat,
            @Param("userLng") double userLng,
            @Param("radiusKm") double radiusKm,
            @Param("limitCount") int limitCount
    );

    List<Airport> findByUserId(Long userId);
}
