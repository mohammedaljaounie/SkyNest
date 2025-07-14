package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.PlaceNearTheHotelImage;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaceNearHotelImageRepo extends JpaRepository<PlaceNearTheHotelImage,Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM PlaceNearTheHotelImage i WHERE i.placeNearTheHotel.id = :placeId")
    void deleteAllByPlaceId(@Param("placeId") Long placeId);


//    void deleteAllByPlaceNearTheHotelId(Long placeId);



}
