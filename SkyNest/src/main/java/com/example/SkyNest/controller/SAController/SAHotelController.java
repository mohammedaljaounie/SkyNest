package com.example.SkyNest.controller.SAController;

import com.example.SkyNest.dto.hoteldto.HotelRequest;
import com.example.SkyNest.dto.hoteldto.HotelRequestUpdate;
import com.example.SkyNest.dto.hoteldto.SAHotelResponse;
import com.example.SkyNest.service.SuperAdminService.SAHotelService.SAHotelService;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/super_admin")
public class SAHotelController {

    @Autowired
    private SAHotelService saHotelService;

    @PostMapping("/create-hotel")
    public ResponseEntity<Map<String,String>> createHotel(@RequestBody HotelRequest hotelInfo){

        Map<String,String> message = saHotelService.createHotel(hotelInfo);

        if (message.get("message").equals("Successfully Hotel Added")){
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.status(403).body(message);

    }


    @PutMapping("/update-hotel")
    public ResponseEntity<Map<String,String>> updateHotel(@RequestBody HotelRequestUpdate hotelInfo){

        Map<String,String> message = saHotelService.updateHotel(hotelInfo);

        if (message.get("message").equals("Successfully Hotel Updated")){
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.status(403).body(message);

    }


    @DeleteMapping("/delete-hotel/")
    public ResponseEntity<Map<String,String>> deleteHotel(@RequestParam Long id){

        Map<String,String> message = saHotelService.deleteHotel(id);

        if (message.get("message").equals("Successfully Deleted")){
            return ResponseEntity.ok(message);
        }

        return ResponseEntity.status(403).body(message);

    }


    @GetMapping("/all-hotel")
    public ResponseEntity<List<SAHotelResponse>> showAllHotel(){
        return ResponseEntity.ok(this.saHotelService.showAllHotels());
    }

    @GetMapping("/hotel-info")
    public ResponseEntity<SAHotelResponse> showHotelInfo(@RequestParam Long id){
        return ResponseEntity.ok(this.saHotelService.showHotelInfo(id));
    }

    @GetMapping("/hotelImage/{fileName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String fileName) {
        try {
            Resource image = saHotelService.loadImage(fileName);
            String contentType = saHotelService.getImageContentType(fileName);

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
