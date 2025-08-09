package com.example.SkyNest.controller.UController.UAirportController;

import com.example.SkyNest.dto.airportdto.FilterFlightInfo;
import com.example.SkyNest.dto.airportdto.FlightBookingResponse;
import com.example.SkyNest.dto.airportdto.FlightResponse;
import com.example.SkyNest.model.entity.flight.Flight;
import com.example.SkyNest.model.repository.flight.FlightRepo;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.service.AdminService.AAirportService.AFlightService;
import com.example.SkyNest.service.UserService.UAirportService.UFlightService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/user/flight")
public class UFlightController {

    @Autowired
    private FlightRepo flightRepo;

    private final AFlightService aFlightService;
    private final UFlightService uFlightService;

    public UFlightController(
            AFlightService aFlightService,
            UFlightService uFlightService){
        this.aFlightService = aFlightService;
        this.uFlightService = uFlightService;
    }


    @GetMapping("/viewFlights/{airportId}")
    public ResponseEntity<List<FlightResponse>> getAllAirportFlight(@PathVariable Long airportId){
       List<FlightResponse> flightResponses = this.aFlightService.getAllYourFlight(airportId);
       if (flightResponses!=null){
           return ResponseEntity.ok(flightResponses);

       }
       return ResponseEntity.status(400).body(Collections.emptyList());
    }

    @GetMapping("/viewFlightDetails/{flightId}")
    public ResponseEntity<FlightResponse> flightDetails(@PathVariable Long flightId){
        FlightResponse  flightResponse = this.aFlightService.flightDetails(flightId);
        if (flightResponse!=null){
            return ResponseEntity.ok(flightResponse);
        }
        return ResponseEntity.status(400).body(null);
    }


    @GetMapping("/viewActiveFlight")
    public ResponseEntity<List<FlightBookingResponse>> viewActiveFlight(){
        return ResponseEntity.ok(this.uFlightService.viewActiveFlight());
    }

    @GetMapping("/viewCanceledFlight")
    public ResponseEntity<List<FlightBookingResponse>> viewCanceledFlight(){
        return ResponseEntity.ok(this.uFlightService.viewCanceledFlight());
    }

    @GetMapping("/viewIncorrectFlight")
    public ResponseEntity<List<FlightBookingResponse>> viewIncorrectFlight(){
        return ResponseEntity.ok(this.uFlightService.viewIncorrectFlight());
    }

    @GetMapping("/searchByStartingAndEndingPointInAll")
    public ResponseEntity<List<FlightResponse>> findAvailableFlightsForStartAndEndPointInAll(@RequestParam String startPoint,@RequestParam String destination){
        return ResponseEntity.ok(this.uFlightService.searchFlightByStartPointAndDestinationInAllAirport(startPoint, destination));
    }


    @GetMapping("/searchByStartingAndEndingPoint/{airportId}")
    public ResponseEntity<List<FlightResponse>> findAvailableFlightsForStartAndEndPointInCertainAirport(@PathVariable Long airportId,@RequestParam String startPoint,@RequestParam String destination){
        return ResponseEntity.ok(this.uFlightService.searchFlightByStartPointAndDestinationInCertainAirport(airportId,startPoint, destination));
    }

    @GetMapping("/searchAvailableFlightsForDateAndLocation")
    public ResponseEntity<List<FlightResponse>> searchAvailableFlightsForDateAndLocation(@RequestBody FilterFlightInfo info){
        return ResponseEntity.ok(this.uFlightService.searchAvailableFlightsForDateAndLocation(info));
    }


    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String fileName) {
        try {
            Resource image = uFlightService.loadImage(fileName);
            String contentType = uFlightService.getImageContentType(fileName);

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(image);

        } catch (FileNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }



}
