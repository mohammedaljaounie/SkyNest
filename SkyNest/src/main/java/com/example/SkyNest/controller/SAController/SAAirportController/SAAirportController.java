package com.example.SkyNest.controller.SAController.SAAirportController;

import com.example.SkyNest.dto.airportdto.AirportRequest;
import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.AirportUpdateRequest;
import com.example.SkyNest.service.SuperAdminService.SAAirportService.SAAirportService;
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
@RequestMapping("/super_admin/airport")
public class SAAirportController {
    private final SAAirportService saAirportService;

    public SAAirportController(SAAirportService saAirportService){
        this.saAirportService = saAirportService;
    }


    @PostMapping("/create-airport")
    public ResponseEntity<Map<String,String>> createAirport(@RequestBody AirportRequest airportRequest){

        Map<String ,String> message = this.saAirportService.createAirport(airportRequest);
        if (message.get("message").equals("Successfully Created")){
            return ResponseEntity.ok(message);
        } else if (message.get("message").equals("Sorry , you don't have account in our system")) {

            return ResponseEntity.status(400).body(message);
        }else {

            return ResponseEntity.status(403).body(message);
        }
    }

    @PutMapping("/update-airport")
    public ResponseEntity<Map<String,String>> updateAirport(@RequestBody AirportUpdateRequest updateRequest){
        Map<String,String> message  = this.saAirportService.updateAirportInfo(updateRequest);

        if (message.get("message").equals("Successfully Updated")){

            return ResponseEntity.ok(message);
        } else if (message.get("message").equals("Sorry , this airport is not found in our system")) {
            return ResponseEntity.status(400).body(message);
        }else{

            return ResponseEntity.status(403).body(message);
        }
    }

    @GetMapping("/airport-info/{airportId}")
    public ResponseEntity<?> airportInfo(@PathVariable Long airportId){

        AirportResponse airportResponse = this.saAirportService.airportInfo(airportId);
        if (airportResponse!=null){
            return ResponseEntity.ok(airportResponse);
        }
        return ResponseEntity.status(400).body(Map.of("message","Wrong , this airport is not found in our system"));
    }

    @GetMapping("/airports-info")
    public ResponseEntity<List<AirportResponse>> airportsInfo(){
        return ResponseEntity.ok(this.saAirportService.airportsInfo());
    }


    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
        try {
            Resource image = saAirportService.loadImage(fileName);
            String contentType = saAirportService.getImageContentType(fileName);

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


    @PostMapping("/delete/{airportId}")
    public ResponseEntity<Map<String,String>> airportDelete(@PathVariable Long airportId){
        Map<String,String> message = this.saAirportService.deleteAirport(airportId);
        if (message.get("message").equals("successfully deleted")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

}




