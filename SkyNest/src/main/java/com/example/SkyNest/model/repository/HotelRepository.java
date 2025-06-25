package com.example.SkyNest.model.repository;

import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long>{
    Optional<Hotel> findByIdAndUserId(Long hotelId,Long userId);

    Optional<Hotel> findByUserId(Long id);

    @Query(value = "select h from Hotel h where h.address like concat('%',:location,'%')")
    List<Hotel> findByAddress(String location);

    @Query(value = """
    SELECT * 
    FROM (
        SELECT *, 
        (6371 * acos(
            cos(radians(:userLat)) * 
            cos(radians(latitude)) * 
            cos(radians(longitude) - radians(:userLng)) + 
            sin(radians(:userLat)) * 
            sin(radians(latitude))
        )) AS distance
        FROM hotel
    ) AS temp
    ORDER BY distance
    LIMIT :limit
    """, nativeQuery = true)
    List<Hotel> findClosestHotels(
            @Param("userLat") double userLat,
            @Param("userLng") double userLng,
            @Param("limit") int limit);


    @Query(value = "select h from Hotel h order by h.avgRating")
    List<Hotel> filterHotelByRating();

}
