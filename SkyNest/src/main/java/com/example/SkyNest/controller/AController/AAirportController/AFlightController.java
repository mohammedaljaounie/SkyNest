package com.example.SkyNest.controller.AController.AAirportController;

import com.example.SkyNest.dto.airportdto.FlightInfoUpdate;
import com.example.SkyNest.dto.airportdto.FlightRequest;
import com.example.SkyNest.dto.airportdto.FlightResponse;
import com.example.SkyNest.service.AdminService.AAirportService.AFlightService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/flight")
public class AFlightController {

    private final AFlightService aFlightService;

    public AFlightController(
    AFlightService aFlightService
    ){

        this.aFlightService = aFlightService;
    }

    @PostMapping("/upload/{flightId}")
    public ResponseEntity<Map<String,String>> loadImageToFlight(@PathVariable Long flightId, @RequestParam MultipartFile image) throws Exception {
        Map<String,String> message = this.aFlightService.saveImageToFlight(flightId, image);
        if (message.get("message").equals("Successfully Uploaded")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }


    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String fileName) {
        try {
            Resource image = aFlightService.loadImage(fileName);
            String contentType = aFlightService.getImageContentType(fileName);

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

    @PostMapping("/createFlight")
    public ResponseEntity<Map<String,String>> createFlight(@RequestBody FlightRequest flightRequest){
        Map<String,String> message = this.aFlightService.createFlight(flightRequest);
        if (message.get("message").equals("Successfully added flight")){

            return  ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

    @PostMapping("/updateFlightTime")
    public ResponseEntity<Map<String,String>> updateFlightTime(@RequestBody FlightInfoUpdate flightInfoUpdate) throws Exception {
        Map<String,String> message = this.aFlightService.updateFlightTime(flightInfoUpdate);
        if (message.get("message").equals("successfully updated")){

            return  ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

    @PostMapping("/updatePrice/{flightId}")
    public ResponseEntity<Map<String,String>> updatePrice(@PathVariable Long flightId,@RequestParam double price) throws Exception{
        Map<String ,String> message = this.aFlightService.updatePrice(flightId, price);
        if (message.get("message").equals("successfully updated price")){

            return  ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

    @GetMapping("/allFlight/{airportId}")
    public ResponseEntity<List<FlightResponse>> getAllFlight(@PathVariable Long airportId){
        List<FlightResponse> flightResponseList = this.aFlightService.getAllYourFlight(airportId);
        if (flightResponseList!=null){
            return ResponseEntity.ok(flightResponseList);
        }
        return ResponseEntity.status(400).body(Collections.emptyList());
     }


     @GetMapping("/flightDetails/{flightId}")
    public ResponseEntity<FlightResponse> flightDetails(@PathVariable Long flightId){
        FlightResponse flightResponse = this.aFlightService.flightDetails(flightId);
        if (flightResponse!=null){
            return ResponseEntity.ok(flightResponse);
        }
        return ResponseEntity.status(400).body(null);
    }

    @DeleteMapping("/deleteFlight/{flightId}")
    public ResponseEntity<Map<String,String>> deleteFlight(@PathVariable Long flightId) throws Exception {
        Map<String,String> message = this.aFlightService.deleteFlight(flightId);
        if (message.get("message").equals("Successfully Deleted Flight")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

}

