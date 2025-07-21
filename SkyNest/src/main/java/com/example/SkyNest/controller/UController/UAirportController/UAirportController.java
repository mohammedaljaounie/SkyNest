package com.example.SkyNest.controller.UController.UAirportController;

import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.EvaluationRequestInfo;
import com.example.SkyNest.dto.airportdto.FlightBookingDetails;
import com.example.SkyNest.service.SuperAdminService.SAAirportService.SAAirportService;
import com.example.SkyNest.service.UserService.UAirportService.UAirportService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/airport")
public class UAirportController {

    private final SAAirportService service;
    private final UAirportService uAirportService;

    public UAirportController(SAAirportService service,UAirportService uAirportService){
        this.service = service;
        this.uAirportService = uAirportService;

    }

    @GetMapping("/viewAll")
    public ResponseEntity<List<AirportResponse>> getAirport(){
       return ResponseEntity.ok(this.service.airportsInfo());
    }

    @GetMapping("/viewCertainAirport/{airportId}")
    public ResponseEntity<AirportResponse> getCertainAirport(@PathVariable Long airportId){
        AirportResponse airportResponse = this.service.airportInfo(airportId);
        if (airportResponse!=null){
            return ResponseEntity.ok(airportResponse);
        }
        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("/viewNearUserAirport")
    public ResponseEntity<List<AirportResponse>> getNearAirport(){
        return ResponseEntity.ok(this.uAirportService.getNearAirport());
    }


    @GetMapping("/filterAirportByRating")
    public ResponseEntity<List<AirportResponse>> filterByRating(){
        return ResponseEntity.ok(this.uAirportService.getAirportOrderByRating());
    }


    @GetMapping("/searchByName")
    public ResponseEntity<AirportResponse> searchByName(@RequestParam String airportName){
        AirportResponse airportResponse = this.uAirportService.searchByName(airportName);
        if (airportResponse!=null){
            return ResponseEntity.ok(airportResponse);
        }
        return ResponseEntity.status(400).body(null);
    }


    @PostMapping("/flightReservation")
    public ResponseEntity<Map<String,String>> bookingFlightInAirport(@RequestBody FlightBookingDetails flightDetails){

        Map<String,String > message = this.uAirportService.flightBookingInAirport(flightDetails);

        if (message.get("message").equals("Your flight reservation has been completed successfully We wish you a pleasant journey.")){
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.status(400).body(message);

    }

    @PostMapping("/cancelFlightBooking")
    public ResponseEntity<Map<String,String>> cancelFlightBooking(@RequestParam Long fightBookingId){
        Map<String,String> message = this.uAirportService.cancelFlightBooking(fightBookingId);
        if (message.get("message").equals("The flight cancellation process was successfully completed and the 75% ticket cancellation tax was deducted.")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }


    @PostMapping("/airportEvaluation")
    public ResponseEntity<Map<String,String>> airportEvaluation(@RequestBody EvaluationRequestInfo evaluationInfo){

        Map<String,String > message = this.uAirportService.airportEvaluation(evaluationInfo);

        if (message.get("message").equals("Your rating has been successfully completed.\nThank you for your feedback on this airport.")) {
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);


    }


    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
        try {
            Resource image = uAirportService.loadImage(fileName);
            String contentType = uAirportService.getImageContentType(fileName);

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
