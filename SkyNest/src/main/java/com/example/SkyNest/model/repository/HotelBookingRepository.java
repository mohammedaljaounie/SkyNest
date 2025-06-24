package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.HotelBooking;
import org.hibernate.sql.ast.tree.expression.JdbcParameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HotelBookingRepository extends JpaRepository<HotelBooking,Long> {
    Optional<HotelBooking> findByIdAndUserId(Long bookingId, Long userId);


    //@Query("SELECT hb FROM HotelBooking hb WHERE hb.user.id = :userId AND hb.hotel.id = :hotelId")
    List<HotelBooking> findByUserIdAndHotelId(Long userId, Long hotelId);


    Optional<HotelBooking> findByHotelId(Long hotelId);

    @Query( value = "select h from HotelBooking h where h.hotel.id = :hotelId And " +
            "h.launchDate <=  :newEnd And h.departureDate >= :newStart And h.status = :status")
    List<HotelBooking> filterByDate(Long hotelId, LocalDate newStart,LocalDate newEnd , boolean status);




}
