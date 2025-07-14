package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage,Long> {
    Optional<RoomImage> findByName(String imageName);

    List<RoomImage> findByRoomId(Long id);



     void deleteAllByRoomId(Long roomId);



}
