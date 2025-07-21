package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.FlightBooking;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightBookingRepo extends JpaRepository<FlightBooking,Long> {


    List<FlightBooking> findByUserIdAndStatus(Long userId, StatusEnumForBooking statusEnumForBooking);

    Optional<FlightBooking> findByIdAndUserIdAndStatus(Long flightBookingId, Long userId, StatusEnumForBooking statusEnumForBooking);

    List<FlightBooking> findByAirportIdAndUserIdAndStatus(Long airportId, Long userId, StatusEnumForBooking statusEnumForBooking);

    @Query("SELECT f FROM FlightBooking f WHERE f.startingPointDate BETWEEN :start AND :end and f.status = :status")
    List<FlightBooking> findFlightsWithinTimeWindow( LocalDateTime start, LocalDateTime end,StatusEnumForBooking status);

    @Query("SELECT f FROM FlightBooking f WHERE f.destinationDate BETWEEN :start AND :end and f.status = :status")
    List<FlightBooking> findFlightsWithinTimeWindowArrived( LocalDateTime start,  LocalDateTime end,StatusEnumForBooking status);


    List<FlightBooking> findByFlightIdAndStatus(Long flightId, StatusEnumForBooking statusEnumForBooking);

    void deleteAllByAirportId(Long airportId);

    @Query(value = """
    SELECT COUNT(*) AS total_bookings
    FROM airport_booking
    WHERE MONTH(starting_point_date) = :month AND YEAR(starting_point_date) = :year AND airport_id = :airportId and status = :status
""", nativeQuery = true)
    Long countBookingsForAirportInMonth(@Param("airportId") Long airportId, @Param("month") int month, @Param("year") int year,@Param("status")StatusEnumForBooking status);


}
