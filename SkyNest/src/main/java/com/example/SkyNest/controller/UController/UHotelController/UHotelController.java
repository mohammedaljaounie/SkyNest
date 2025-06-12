package com.example.SkyNest.controller.UController.UHotelController;


import com.example.SkyNest.dto.HotelBookingRequest;
import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.dto.HotelRoomRequest;
import com.example.SkyNest.dto.UserBookingResponse;
import com.example.SkyNest.model.entity.HotelBooking;
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

    @Autowired
    private UHotelService uHotelService;

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

    @PostMapping("/booking/{hotelId}")
    public ResponseEntity<Map<String,String>> roomBooking(@PathVariable Long hotelId,
                                                          @RequestBody HotelBookingRequest hotelBookingRequest){
        Map<String,String> message = this.uHotelService.roomBooking(hotelId, hotelBookingRequest);
        if (message.get("message").equals(
                "your reservation has been successfully completed.\n" +
                "wr hope you have a comfortable stay")){
            return ResponseEntity.ok()
                    .body(message);
        }
        return ResponseEntity.status(403)
                .body(message);

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


    @GetMapping("/bookingDirect/{hotelId}")
    public ResponseEntity<Map<String,String >> roomBookingDirect(
            @PathVariable Long hotelId,
            @RequestParam Long roomId,
            @RequestBody HotelRoomRequest hotelRoomRequest
            ){
        Map<String,String> message = this.uHotelService.roomBookingDirect(hotelId, roomId, hotelRoomRequest);
        if (message.get("message").equals("your reservation has been successfully completed.\n" +
                "wr hope you have a comfortable stay")){
            return ResponseEntity.ok()
                    .body(message);
        }
        return ResponseEntity.status(403)
                .body(message);
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


    // todo :booking cansel
    @PostMapping("/bookingCansel/{userBookingId}")
    public ResponseEntity<Map<String,String>> bookingCancel(@PathVariable Long userBookingId){

        Map<String,String> message = this.uHotelService.bookingCansel(userBookingId);

       if (!message.get("message").equals("Successfully Canceled"))
            return ResponseEntity.status(400).body(message);

      else
            return ResponseEntity.ok(message);
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





}

