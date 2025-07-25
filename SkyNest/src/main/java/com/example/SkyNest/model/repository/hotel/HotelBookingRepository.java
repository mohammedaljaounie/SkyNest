package com.example.SkyNest.model.repository.hotel;

import com.example.SkyNest.model.entity.hotel.HotelBooking;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
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

    List<HotelBooking> findAllByHotelId(Long hotelId);

    @Query("SELECT hb FROM HotelBooking hb WHERE hb.user.id = :userId AND hb.hotel.id = :hotelId And hb.status = :status")
    List<HotelBooking> findByUserIdAndHotelIdAndStatus(Long userId, Long hotelId, StatusEnumForBooking status);

    @Query ("select r from HotelBooking r where r.launchDate = :startDate and r.status = :status")
    List<HotelBooking> bringInBookingsThatWillExpire(LocalDate startDate, StatusEnumForBooking status);

    @Query("select r from HotelBooking r where r.departureDate = :endDate AND r.status = :status")
    List<HotelBooking> bringBookingsThatWillStart(LocalDate endDate, StatusEnumForBooking status);

    Optional<HotelBooking> findByHotelId(Long hotelId);

    @Query( value = "select h from HotelBooking h where h.hotel.id = :hotelId And " +
            "h.launchDate <=  :newEnd And h.departureDate >= :newStart And h.status = :status")
    List<HotelBooking> filterByDate(Long hotelId, LocalDate newStart,LocalDate newEnd , StatusEnumForBooking status);

    @Query(value = "select h from HotelBooking h where h.hotel.id = :hotelId And "+
    "h.launchDate <= :nowDate And h.departureDate >= :nowDate And h.status = :status")
    List<HotelBooking> filterBookingByLocalDate(Long hotelId, LocalDate nowDate, StatusEnumForBooking status);


    List<HotelBooking> findByStatusAndUserId(StatusEnumForBooking status, Long userId);


    @Query(value = """
    SELECT COUNT(*) AS total_bookings
    FROM hotel_booking
    WHERE MONTH(launch_date) = :month AND YEAR(launch_date) = :year AND hotel_id = :hotelId AND status = :status
""", nativeQuery = true)
    Long countBookingsForHotelInMonth(@Param("hotelId") Long hotelId, @Param("month") int month, @Param("year") int year,@Param("status") StatusEnumForBooking status);


    void deleteByHotelId(Long id);
}
