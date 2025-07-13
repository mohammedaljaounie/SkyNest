package com.example.SkyNest.controller.UController.UHotelController;

import com.example.SkyNest.dto.hoteldto.UserRoomResponse;
import com.example.SkyNest.service.UserService.UHotelService.URoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/user/room")
public class URoomController {
    @Value("${image.upload.room}")
    private String imageUploadUser;

    @Autowired
    private URoomService uRoomService;


    @GetMapping("/view-rooms/{hotelId}")
    public ResponseEntity<List<UserRoomResponse>>  viewAllRoomInHotel(@PathVariable Long hotelId){
        List<UserRoomResponse> userRoomResponses = this.uRoomService.getAllRoomInHotel(hotelId);

        if (userRoomResponses == null){
            return ResponseEntity.status(204).body(null);
        }

        return ResponseEntity.ok().body(userRoomResponses);




    }

    @GetMapping("/details/{roomId}")
    public ResponseEntity<UserRoomResponse>  viewRoomDetails(@PathVariable Long roomId){
        UserRoomResponse userRoomResponses = this.uRoomService.getRoomInformation(roomId);

        if (userRoomResponses==null){
            return ResponseEntity.status(204).body(null);
        }

        return ResponseEntity.ok().body(userRoomResponses);




    }

    @GetMapping("/{imageName}")
    public ResponseEntity<byte[]> viewHotelImage(@PathVariable String imageName) throws Exception {

        byte[] image = this.uRoomService.viewImage(imageName);

        String contentType = Files.probeContentType(Path.of(imageUploadUser + imageName));

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(contentType)).body(image);

    }



}
