package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.PlaceNearTheHotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceNearHotelRepo extends JpaRepository<PlaceNearTheHotel,Long> {
}
