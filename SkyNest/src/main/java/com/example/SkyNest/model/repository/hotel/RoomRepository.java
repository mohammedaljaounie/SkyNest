package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.Room;
import com.example.SkyNest.myEnum.RoomStatus;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room,Long> {


   List<Room> findByHotelId(Long id);

   int countByHotelId(Long id);

   @Query("SELECT COUNT(r) FROM Room r WHERE  r.hotel.id = :hotelId")
   int countRoomByHotelId(Long hotelId);


   Optional<Room> findByIdAndHotelId(Long roomId, Long id);
   List<Room> findByStatusAndHotelId(RoomStatus status, Long id);

   @Modifying
   @Transactional
   @Query("DELETE FROM Room r WHERE r.hotel.id = :hotelId")
   void deleteByHotelId(@Param("hotelId") Long hotelId);
}
