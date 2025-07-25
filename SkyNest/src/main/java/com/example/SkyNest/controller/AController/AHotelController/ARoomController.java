package com.example.SkyNest.controller.AController.AHotelController;

import com.example.SkyNest.dto.hoteldto.RoomRequest;
import com.example.SkyNest.dto.hoteldto.RoomResponse;
import com.example.SkyNest.dto.hoteldto.RoomUpdateRequest;
import com.example.SkyNest.service.AdminService.AHotelService.ARoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

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


    @PostMapping("/uploadRoomImage/{hotelId}")
    public ResponseEntity<Map<String,String>> uploadRoomImage(
                                  @PathVariable Long hotelId ,
                                                           @RequestParam Long roomId
                                                    , @RequestParam MultipartFile image)
                                                        throws IOException {
        Map<String,String> message = this.aRoomService.uploadImageToRoom(hotelId,roomId, image);
        if (message.get("message").equals("Successfully Upload")){
            return ResponseEntity.ok()
                    .body(message);
        }

        return ResponseEntity.status(403)
                .body(message);

    }

    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
        try {
            Resource image = aRoomService.loadImage(fileName);
            String contentType = aRoomService.getImageContentType(fileName);

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

    @GetMapping("/showAllRoom/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getAllRoom(@PathVariable Long hotelId){

       return this.aRoomService.getAllRoom(hotelId);
    }

    @GetMapping("/showBookingRoom/{hotelId}")
    public ResponseEntity<List<RoomResponse>> getBookingRoom(@PathVariable Long hotelId){

        List<RoomResponse> roomResponseList = this.aRoomService.getBookingRoom(hotelId);
        if (roomResponseList!=null)
            return ResponseEntity.ok()
                    .body(roomResponseList);

        return ResponseEntity.status(204)
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
            @PathVariable Long hotelId, @RequestParam Long roomId, @RequestBody RoomUpdateRequest request
            ){
        Map<String,String> message = this.aRoomService.updateRoomInfo(
                hotelId, roomId, request);
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

    @PostMapping("/update_price/{hotelId}")
    public ResponseEntity<?> updateRoomPrice(@PathVariable Long hotelId,@RequestParam Long roomId,@RequestParam double price) throws Exception {
        System.out.println("controller1 ");
        Map<String ,String> message = this.aRoomService.updateRoomPrice(hotelId,roomId, price);
        System.out.println("controller2");
        if (message.get("message").equals("Successfully  updated price")){
            return ResponseEntity.ok(message);
        } else if (message.get("message").equals("Sorry , Wrong price should not be zero or down")) {

            return ResponseEntity.status(400).body(message);
        } else {
            return ResponseEntity.status(403).body(message);
        }
    }





}
