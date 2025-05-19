package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel,Long>{
    Optional<Hotel> findByUserId(Long id);

    List<Hotel> findByLocation(String location);

}
