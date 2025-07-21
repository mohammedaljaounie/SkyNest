package com.example.SkyNest.controller.AController.AAirportController;

import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.UpdateAirportInfoRequest;
import com.example.SkyNest.service.AdminService.AAirportService.AAirportService;
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
@RequestMapping("/admin/airport")
public class AAirportController {

    private final AAirportService aAirportService;

    public AAirportController(
    AAirportService aAirportService
    ){

        this.aAirportService = aAirportService;
    }

    @PostMapping("/upload/{airportId}")
    public ResponseEntity<Map<String,String>> loadImageToFlight(@PathVariable Long airportId, @RequestParam MultipartFile image) throws Exception {
        Map<String,String> message = this.aAirportService.saveImageToAirport(airportId, image);
        if (message.get("message").equals("Successfully Uploaded")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }




    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String fileName) {
        try {
            Resource image = aAirportService.loadImage(fileName);
            String contentType = aAirportService.getImageContentType(fileName);

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


    @GetMapping("/totalBalance/{airportId}")
    public ResponseEntity<Double> getTotalBalanceForAirport(@PathVariable Long airportId){

        double totalBalance = this.aAirportService.getTotalBalanced(airportId);
        if (totalBalance==-1) {
            return ResponseEntity.status(400).body(totalBalance);
        }
        return ResponseEntity.ok(totalBalance);
    }


    @GetMapping("/bookingCount/{airportId}")
    public ResponseEntity<?> getBookingsForAirport(
            @PathVariable Long airportId,
            @RequestParam int month,
            @RequestParam int year) {
        Long count = aAirportService.getAirportBookingsForAirport(airportId, month, year);
        return ResponseEntity.ok(Map.of("airportId", airportId, "totalBookings", count));
    }

    @GetMapping("/showAirportsDirect")
    public ResponseEntity<List<AirportResponse>> gitAirportForAdmin(){
        List<AirportResponse> airportResponseList = this.aAirportService.gitAirportForAdmin();
        if (airportResponseList!=null){
            return ResponseEntity.ok(airportResponseList);
        }
        return ResponseEntity.status(400).body(Collections.emptyList());
    }

    @GetMapping("/showCertainAirport/{airportId}")
    public ResponseEntity<AirportResponse> getCertainAirportForAdmin(@PathVariable Long airportId){
        AirportResponse airportResponse = this.aAirportService.getCertainAirportForAdmin(airportId);
        if (airportResponse!=null){
            return ResponseEntity.ok(airportResponse);
        }
        return ResponseEntity.status(400).body(null);
    }

    @PutMapping("/updateAirportInfo")
    public ResponseEntity<Map<String,String>> updateAirportInfo(@RequestBody UpdateAirportInfoRequest request){

        Map<String,String> message = this.aAirportService.updateAirportInfo(request);
        if (message.get("message").equals("Successfully Updated")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }



}
