package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {


   List<Room> findByHotelId(Long id);
   List<Room> findByStatusAndRoomTypeAndHotelId(boolean status,String roomType,Long hotelId);

   int countByHotelId(Long id);

   @Query("SELECT COUNT(r) FROM Room r WHERE  r.hotel.id = :hotelId")
   int countRoomByHotelId(Long hotelId);


   Optional<Room> findByIdAndHotelId(Long roomId, Long id);
   List<Room> findByStatusAndHotelId(boolean status,Long id);

}
