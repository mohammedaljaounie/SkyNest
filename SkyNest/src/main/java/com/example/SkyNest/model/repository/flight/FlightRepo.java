package com.example.SkyNest.model.repository.flight;

import com.example.SkyNest.model.entity.flight.Flight;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FlightRepo extends JpaRepository<Flight,Long> {

    List<Flight> findAllByAirportIdAndEnable(Long airportId,boolean enable);



    @Query("select  f From Flight f where f.airport.id = :airportId and f.tripType = :enumForFlight and f.enable = :enable")
    List<Flight> findInCompleteFlight(Long airportId, StatusEnumForFlight enumForFlight, boolean enable);

    @Query(value = "select f from Flight f where f.startingPoint like  CONCAT(:startingPoint, '%') and f.destination like  CONCAT(:destination, '%') and f.status = :status and f.startingPointDate > :threshold and f.enable = :enable")
    List<Flight> findAvailableFlightsForStartAndEndPointInAll(String startingPoint, String destination, StatusEnumForFlight status, LocalDateTime threshold,boolean enable);

    @Query(value = "select f from Flight f where f.airport.id = :airportId and f.startingPoint like  CONCAT(:startingPoint, '%') and f.destination like  CONCAT(:destination, '%') and f.status = :status and f.startingPointDate > :threshold and f.enable = :enable")
    List<Flight> findAvailableFlightsForStartAndEndPointInCertainAirport(Long airportId, String startingPoint, String destination, StatusEnumForFlight status, LocalDateTime threshold,boolean enable);

    @Query("SELECT f FROM Flight f WHERE f.startingPointDate BETWEEN :startDate AND :endDate AND f.startingPointDate > :nowPlus10 AND f.startingPoint like  CONCAT(:startingPoint, '%') AND f.destination like  CONCAT(:destination, '%') AND f.status = :status and f.enable = :enable")
    List<Flight> findAvailableFlightsForDateAndLocation(
            LocalDateTime startDate,
            LocalDateTime endDate,
            LocalDateTime nowPlus10,
            String startingPoint,
            String destination,
            StatusEnumForFlight status,
            boolean enable
    );

    @Query("SELECT f FROM Flight f WHERE f.startingPointDate BETWEEN :startDate AND :endDate AND f.startingPoint like  CONCAT(:startingPoint, '%') AND f.destination like  CONCAT(:destination, '%') AND f.status = :status and f.enable = :enable")
    List<Flight> findAvailableFlightsForFutureDateAndLocation(
            LocalDateTime startDate,
            LocalDateTime endDate,
            String startingPoint,
            String destination,
            StatusEnumForFlight status,
            boolean enable
    );


    void deleteAllByAirportId(Long airportId);
}
