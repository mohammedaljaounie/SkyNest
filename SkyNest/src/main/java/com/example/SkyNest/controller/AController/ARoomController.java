package com.example.SkyNest.controller.AController;

import com.example.SkyNest.dto.RoomRequest;
import com.example.SkyNest.dto.RoomResponse;
import com.example.SkyNest.dto.RoomUpdateRequest;
import com.example.SkyNest.model.entity.Room;
import com.example.SkyNest.service.AdminService.AHotelService.ARoomService;
import com.example.SkyNest.service.UserService.UHotelService.UHotelService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/admin/room")
public class ARoomController {
    @Value("${image.upload.room}")
    private String uploadImageRoom;

    @Autowired
    private ARoomService aRoomService;

    @PostMapping("/create/{hotelId}")
    public ResponseEntity<Map<String,String>> createRoom(
                                                @PathVariable Long hotelId
                                         , @RequestBody RoomRequest roomRequest){

        Map<String,String> message = this.aRoomService.createRoom(hotelId, roomRequest);

        if (message.get("message").equals("Successfully Room Added")){
            return ResponseEntity.ok(
                    message
            );
        }

        return ResponseEntity.
                status(403)
                .body(message);
    }


    @PostMapping("/uploadRoomImage")
    public ResponseEntity<Map<String,String>> uploadRoomImage(
                                                           @RequestParam Long id
                                                    , @RequestParam MultipartFile image)
                                                        throws IOException {
        Map<String,String> message = this.aRoomService.uploadImageToRoom(id, image);
        if (message.get("message").equals("Successfully Upload")){
            return ResponseEntity.ok()
                    .body(message);
        }

        return ResponseEntity.status(403)
                .body(message);

    }

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> viewImage(@PathVariable String imageName) throws Exception {
        byte[] image = this.aRoomService.viewImage(imageName);

        String contentType = Files.probeContentType(Path.of(uploadImageRoom + imageName));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType)).body(image);
    }


    @GetMapping("/showAllRoom/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getAllRoom(@PathVariable Long hotelId){

        List<RoomResponse> roomResponseList = this.aRoomService.getAllRoom(hotelId);
        if (roomResponseList!=null)
            return ResponseEntity.ok()
                    .body(roomResponseList);
        return ResponseEntity.status(403)
                .body(null);
    }

    @GetMapping("/showBookingRoom/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getBookingRoom(@PathVariable Long hotelId){

        List<RoomResponse> roomResponseList = this.aRoomService.getBookingRoom(hotelId);
        if (roomResponseList!=null)
            return ResponseEntity.ok()
                    .body(roomResponseList);

        return ResponseEntity.status(403)
                .body(null);
    }

    @GetMapping("/showNotBookingRoom/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getNotBookingRoom(@PathVariable Long hotelId){
        List<RoomResponse> roomResponseList = this.aRoomService.getNotBookingRoom(hotelId);
        if (roomResponseList!=null)
            return ResponseEntity.ok()
                    .body(roomResponseList);

        return ResponseEntity.status(403)
                .body(null);
    }

    @PutMapping("/updateRoomInfo/{hotelId}")
    public ResponseEntity<Map<String,String>> updateRoomInfo(
            @PathVariable Long hotelID, @RequestParam Long roomId, @RequestBody RoomUpdateRequest request
            ){
        Map<String,String> message = this.aRoomService.updateRoomInfo(
                hotelID, roomId, request);
        if (message.get("message").equals("Successfully Updated")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(403)
                .body(message);

    }


    @DeleteMapping("/deleteRoom/{hotelId}")
    public ResponseEntity<Map<String,String>> deleteRoomInformation(
            @PathVariable Long hotelId,@RequestParam Long roomId
    ){

        Map<String,String> message = this.aRoomService.deleteRoom(hotelId, roomId);

        if (message.get("message").equals("Successfully Deleted")){
            return ResponseEntity.ok()
                    .body(message);
        }
        return ResponseEntity.status(403)
                .body(message);
    }

    @PutMapping("/update_price/{roomId}")
    public ResponseEntity<?> updateRoomPrice(@PathVariable Long roomId,@RequestParam double price){
        Map<String ,String> message = this.aRoomService.updateRoomPrice(roomId, price);
        if (message.get("message").equals("Successfully  updated price")){
            return ResponseEntity.ok(message);
        } else if (message.get("message").equals("Sorry , Wrong price should not be zero or down")) {

            return ResponseEntity.status(400).body(message);
        } else {
            return ResponseEntity.status(403).body(message);
        }
    }





}
