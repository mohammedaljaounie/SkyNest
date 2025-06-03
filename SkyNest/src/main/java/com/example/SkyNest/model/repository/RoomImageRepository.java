package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.RoomImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomImageRepository extends JpaRepository<RoomImage,Long> {
    Optional<RoomImage> findByName(String imageName);

    List<RoomImage> findByRoomId(Long id);
}
