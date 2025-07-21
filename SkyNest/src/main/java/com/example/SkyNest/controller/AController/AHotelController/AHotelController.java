package com.example.SkyNest.controller.AController.AHotelController;

import com.example.SkyNest.dto.hoteldto.HotelResponse;
import com.example.SkyNest.dto.hoteldto.PlaceNearHotelResponse;
import com.example.SkyNest.dto.hoteldto.PlaceNearTheHotelRequest;
import com.example.SkyNest.model.repository.hotel.HotelImageRepository;
import com.example.SkyNest.model.repository.hotel.HotelRepository;
import com.example.SkyNest.service.AdminService.AHotelService.AHotelService;
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
    @Autowired
    private HotelImageRepository hotelImageRepository;

    @GetMapping("/showHotel_direct")
    public ResponseEntity<List<HotelResponse>> showHotelDirect(){

        return ResponseEntity.
                ok(this.aHotelService.showHotelInfo());
    }

@PostMapping("/upload")
public ResponseEntity<Map<String,String>> uploadImage(@RequestParam Long hotelId ,@RequestParam("image") MultipartFile image) {
    try {
        Map<String,String > savedImage = aHotelService.saveImage(hotelId,image);
        if (savedImage.get("message").equals("successfully uploaded"))
        return ResponseEntity.ok(savedImage);
        else
            return ResponseEntity.status(400).body(savedImage);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message","not successfully uploaded"));
    }
}




        @GetMapping("/{fileName}")
        public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
            try {
                Resource image = aHotelService.loadImage(fileName,true);
                String contentType = aHotelService.getImageContentType(fileName,true);

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
            if (message.get("message").equals("Successfully Upload"))
            return ResponseEntity.ok(message);
            else
                return ResponseEntity.status(400).body(message);
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
        System.out.println("fad");
       return ResponseEntity.ok(this.aHotelService.deletePlace(placeId));

    }

    @GetMapping("/placeImage/{imageName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String imageName) {
        try {
            Resource image = aHotelService.loadImage(imageName,false);
            String contentType = aHotelService.getImageContentType(imageName,false);

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



    @GetMapping("/bookingCount/{hotelId}")
    public ResponseEntity<?> getBookingsForHotel(
            @PathVariable Long hotelId,
            @RequestParam int month,
            @RequestParam int year) {
        Long count = aHotelService.getHotelBookingsForHotel(hotelId, month, year);
        return ResponseEntity.ok(Map.of("hotelId", hotelId, "totalBookings", count));
    }

}