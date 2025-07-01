package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.HotelBooking;
import com.example.SkyNest.myEnum.StatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking,Long> {
    Optional<HotelBooking> findByIdAndUserId(Long bookingId, Long userId);


    @Query("SELECT hb FROM HotelBooking hb WHERE hb.user.id = :userId AND hb.hotel.id = :hotelId And hb.status = :status")
    List<HotelBooking> findByUserIdAndHotelIdAndStatus(Long userId, Long hotelId, StatusEnum status);


    Optional<HotelBooking> findByHotelId(Long hotelId);

    @Query( value = "select h from HotelBooking h where h.hotel.id = :hotelId And " +
            "h.launchDate <=  :newEnd And h.departureDate >= :newStart And h.status = :status")
    List<HotelBooking> filterByDate(Long hotelId, LocalDate newStart,LocalDate newEnd , StatusEnum status);

    @Query(value = "select h from HotelBooking h where h.hotel.id = :hotelId And "+
    "h.launchDate <= :nowDate And h.departureDate >= :nowDate And h.status = :status")
    List<HotelBooking> filterBookingByLocalDate(Long hotelId,LocalDate nowDate,StatusEnum status);


    List<HotelBooking> findByStatusAndUserId(StatusEnum status,Long userId);




}
