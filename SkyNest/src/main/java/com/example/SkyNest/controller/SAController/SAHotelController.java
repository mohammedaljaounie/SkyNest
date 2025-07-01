package com.example.SkyNest.controller.SAController;

import com.example.SkyNest.dto.HotelRequest;
import com.example.SkyNest.dto.HotelRequestUpdate;
import com.example.SkyNest.dto.SAHotelResponse;
import com.example.SkyNest.service.SuperAdminService.SAHotelService.SAHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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



}
