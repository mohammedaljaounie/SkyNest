package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.HotelImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelImageRepository extends JpaRepository<HotelImage,Long> {

    Optional<HotelImage> findByName(String name);

    List<HotelImage> findByHotelId(Long id);


    void deleteAllByHotelId(Long id);
}
