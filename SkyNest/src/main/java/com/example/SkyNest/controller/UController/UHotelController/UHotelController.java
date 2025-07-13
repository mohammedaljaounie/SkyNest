package com.example.SkyNest.controller.UController.UHotelController;


import com.example.SkyNest.dto.hoteldto.HotelBookingRequest;
import com.example.SkyNest.dto.hoteldto.HotelResponse;
import com.example.SkyNest.dto.hoteldto.PlaceNearHotelResponse;
import com.example.SkyNest.dto.hoteldto.UserBookingResponse;
import com.example.SkyNest.service.AdminService.AHotelService.AHotelService;
import com.example.SkyNest.service.UserService.UHotelService.UHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user/hotel")
public class UHotelController {

    @Value("${image.upload.dir}")
    private String imageUploadUser;

    @Value("${image.upload.place.near.hotel}")
    private String uploadImagePlace;


    @Autowired
    private UHotelService uHotelService;

    @Autowired
    private AHotelService aHotelService;

    @GetMapping("/all_hotel")
    public ResponseEntity<List<HotelResponse>> showAllHotel() {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this
                        .uHotelService
                        .showAllHotel());
    }

    @GetMapping("/show-hotel/{id}")
    public ResponseEntity<HotelResponse> showHotelInfo(@PathVariable Long id) {
        HotelResponse hotelResponse = this.uHotelService.showHotelInfo(id);
        if (hotelResponse != null)
            return ResponseEntity.ok(this.uHotelService.showHotelInfo(id));
        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("/showHotelByLocation")
    public ResponseEntity<List<HotelResponse>> showHotelInfoByLocation(@RequestParam String location) {
       List<HotelResponse> hotelResponse = this.uHotelService.showAllHotelByLocation(location);
        if (hotelResponse != null)
            return ResponseEntity.ok(hotelResponse);
        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("/showHotelDirect")
    public ResponseEntity<List<HotelResponse>> showAllHotelDirect() {
        return ResponseEntity.ok(this.uHotelService.showHotelDirect());
    }

    @GetMapping("/hotelImage/{imageName}")
    public ResponseEntity<byte[]> viewHotelImage(@PathVariable String imageName) throws Exception {

        byte[] image = this.uHotelService.viewImage(imageName);

        String contentType = Files.probeContentType(Path.of(imageUploadUser + imageName));

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(contentType)).body(image);

    }


    @GetMapping("/activeReservation")
    public ResponseEntity<List<UserBookingResponse>> viewActiveReservation(){
        return ResponseEntity.ok()
                .body(this.uHotelService.viewActiveReservation());
    }

    @GetMapping("/canceledReservation")
    public ResponseEntity<List<UserBookingResponse>> viewCanceledReservation(){
        return ResponseEntity.ok()
                .body(this.uHotelService.viewCanceledReservation());
    }

    @GetMapping("/incorrectReservation")
    public ResponseEntity<List<UserBookingResponse>> viewIncorrectReservation(){
        return ResponseEntity.ok()
                .body(this.uHotelService.viewIncorrectReservation());
    }




    @GetMapping("/viewTotalBalance")
    public ResponseEntity<?> viewTotalBalance(){
        double totalBalance = this.uHotelService.viewTotalBalance();
        if (totalBalance!=-1){
            return ResponseEntity.ok().body(Map.of("totalBalance",
                    totalBalance));
        }
        return ResponseEntity.status(403)
                .body(Map.of("message",
                        "you don't have a balance in your bank account"));
    }

    @GetMapping("/filterByRating")
    public ResponseEntity<List<HotelResponse>> filterHotelByRating(){

        List<HotelResponse> hotelResponseList = this.uHotelService.filterHotelByRating();

        if (hotelResponseList==null||hotelResponseList.isEmpty()){
            return ResponseEntity.status(400).body(null);
        }

        return ResponseEntity.ok().body(hotelResponseList);
    }

    @PostMapping("hotelEvaluation/{hotelId}")
    public ResponseEntity<?> hotelEvaluation(@PathVariable Long hotelId,
                                             @RequestParam int rating,
                                             @RequestParam String comment)
    {

        Map<String ,String> message = this.uHotelService.hotelEvaluation(hotelId, rating, comment);
        if (message.get("message").equals("Successfully Rating")){
            return ResponseEntity.ok().body(message);
        }
        return ResponseEntity.status(400).body(message);
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> bookRooms(@RequestBody HotelBookingRequest hotelBookingRequest){

        return this.uHotelService.bookingRooms(hotelBookingRequest);
    }

    @PutMapping("cancelReservation/{bookingId}")
    public ResponseEntity<Map<String,String>> cancelReservation(@PathVariable Long bookingId){
        Map<String,String > message = this.uHotelService.cancelBooking(bookingId);

        if (message.get("message").equals("Successfully Canceled")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }


    @GetMapping("/showHotelPlace/{hotelId}")
    public ResponseEntity<List<PlaceNearHotelResponse>> showPlaceNearHotel(@PathVariable Long hotelId){

        List<PlaceNearHotelResponse> placeResponse = this.aHotelService.showAllPlaceNearHotel(hotelId,false);
        if (placeResponse!=null) {
            return ResponseEntity.ok(placeResponse);
        }
        return ResponseEntity.status(400).body(null);
     }


    @GetMapping("/placeImage/{imageName}")
    public ResponseEntity<byte[]> viewPaceHotelImage(@PathVariable String imageName) throws Exception {

        byte[] image = this.uHotelService.viewImage(imageName);

        String contentType = Files.probeContentType(Path.of(uploadImagePlace + imageName));

        return ResponseEntity.ok().
                contentType(MediaType.parseMediaType(contentType)).body(image);

    }

}

