package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long>{
    Optional<Hotel> findByIdAndUserId(Long hotelId,Long userId);

    List<Hotel> findByUserId(Long id);

    @Query(value = "select h from Hotel h where h.address like concat('%',:location,'%')")
    List<Hotel> findByAddress(String location);

    @Query(value = """
    SELECT h.*, (
        6371 * acos(
            cos(radians(:userLat)) * cos(radians(h.latitude)) *
            cos(radians(h.longitude) - radians(:userLng)) +
            sin(radians(:userLat)) * sin(radians(h.latitude))
        )
    ) AS distance
    FROM hotel h
    HAVING distance <= :radiusKm
    ORDER BY distance ASC
    LIMIT :limitCount
    """, nativeQuery = true)
    List<Hotel> findNearbyHotels(
            @Param("userLat") double userLat,
            @Param("userLng") double userLng,
            @Param("radiusKm") double radiusKm,
            @Param("limitCount") int limitCount
    );



    @Query(value = "select h from Hotel h order by h.avgRating desc")
    List<Hotel> filterHotelByRating();

}
