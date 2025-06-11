package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.HotelRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelRatingRepository extends JpaRepository<HotelRating,Long> {

    boolean existsByUserIdAndHotelId(Long userId,Long hotelId);

    Optional<HotelRating> findByUserIdAndHotelId(Long userId,Long hotelId);


    @Query(value = "select sum(h.rating) from HotelRating h where h.hotel.id = :hotelId")
    int sumForAllRating(Long hotelId);

}
