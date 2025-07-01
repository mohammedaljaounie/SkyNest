package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.HotelCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelCardRepository extends JpaRepository<HotelCard,Long> {
    Optional<HotelCard> findByHotelId(Long id);
}
