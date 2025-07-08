package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.PlaceNearTheHotelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceNearHotelImageRepo extends JpaRepository<PlaceNearTheHotelImage,Long> {
}
