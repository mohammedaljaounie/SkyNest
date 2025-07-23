package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.HotelCard;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelCardRepository extends JpaRepository<HotelCard,Long> {
    Optional<HotelCard> findByHotelId(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM HotelCard hc WHERE hc.hotel.id = :hotelId")
    void deleteByHotelId(@Param("hotelId") Long hotelId);
}
