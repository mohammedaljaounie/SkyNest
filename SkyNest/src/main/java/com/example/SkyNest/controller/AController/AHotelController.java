package com.example.SkyNest.controller.AController;

import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.dto.PlaceNearHotelResponse;
import com.example.SkyNest.dto.PlaceNearTheHotelRequest;
import com.example.SkyNest.model.repository.hotel.HotelRepository;
import com.example.SkyNest.service.AdminService.AHotelService.AHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hotel")
public class AHotelController {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Value("${image.upload.place.near.hotel}")
    private String uploadImagePlace;

    @Autowired
    private AHotelService aHotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/showHotel_direct")
    public ResponseEntity<List<HotelResponse>> showHotelDirect(){

        return ResponseEntity.
                ok(this.aHotelService.showHotelInfo());
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String,String>> uploadImage(@RequestParam Long hotelId,@RequestParam MultipartFile file) {
            try {
                Map<String,String > message = aHotelService.uploadImage(hotelId,file);
                return ResponseEntity.ok(message);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","Not Successfully uploaded"));
            }
        }

    @GetMapping("/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) throws Exception {

            byte[] imageData = this.aHotelService.viewImage(imageName);

            String contentType = Files.probeContentType(Path.of(uploadDir + imageName));
            return ResponseEntity.ok().
                    contentType(MediaType.parseMediaType(contentType)).body(imageData);

    }

    @GetMapping("/total_balance/{hotelId}")
    public ResponseEntity<Map<String,Double>> getTotalBalanceForHotel(@PathVariable Long hotelId){

        Double totalBalance = this.aHotelService.getTotalBalanceForHotel(hotelId);
        if (totalBalance != -1){
            return ResponseEntity.ok()
                    .body(Map.of("totalBalance",totalBalance));
        }

        return ResponseEntity.status(403)
                .body(null);
    }

    @PostMapping("/createPlace")
    public ResponseEntity<?> createPlaceNearHotel(@RequestBody PlaceNearTheHotelRequest placeNearTheHotelRequest){
        Map<String,String> message  = this.aHotelService.createPlaceNearHotel(placeNearTheHotelRequest);


        if (message.get("message").equals("Successfully added this place to your hotel")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }

    @PostMapping("/uploadImageToPlace")
    public ResponseEntity<Map<String,String>> uploadImageToPlace(@RequestParam Long placeId,@RequestParam MultipartFile file) {
        try {
            Map<String,String > message = aHotelService.uploadImageToPlace(placeId,file);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message","Not Successfully uploaded"));
        }
    }

    @GetMapping("/show-hotelPlace/{hotelId}")
    public ResponseEntity<List<PlaceNearHotelResponse>> showAllPlaceNearHotel(@PathVariable Long hotelId){

        return ResponseEntity.
                ok(this.aHotelService.showAllPlaceNearHotel(hotelId,true));
    }



    @PutMapping("/update-hotelPlace/{placeId}")
    public ResponseEntity<Map<String,String>> updatePlaceInformation(
            @PathVariable Long placeId,@RequestParam String placeName,@RequestParam String placeDescription ){

     Map<String,String> message = this.aHotelService.updatePlaceInformation(placeId, placeName, placeDescription);
     if (message.get("message").equals("Successfully updated place info")){
         return ResponseEntity.ok(message);
     }
     return ResponseEntity.status(400).body(message);

    }



    @DeleteMapping("/deletePlace/{placeId}")
    public ResponseEntity<Map<String,String>> deletePlace(@PathVariable Long placeId){
       return ResponseEntity.ok(this.aHotelService.deletePlace(placeId));

    }


    @GetMapping("/placeImage/{imageName}")
    public ResponseEntity<?> viewPlaceImage(@PathVariable String imageName) throws Exception {

        byte[] imageData = this.aHotelService.viewImage(imageName);

        String contentType = Files.probeContentType(Path.of(uploadImagePlace + imageName));

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(contentType)).body(imageData);


    }
}