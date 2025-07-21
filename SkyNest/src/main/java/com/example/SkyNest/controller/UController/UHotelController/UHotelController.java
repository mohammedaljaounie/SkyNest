package com.example.SkyNest.controller.UController.UHotelController;


import com.example.SkyNest.dto.hoteldto.*;
import com.example.SkyNest.service.AdminService.AHotelService.AHotelService;
import com.example.SkyNest.service.UserService.UHotelService.UHotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/user/hotel")
public class UHotelController {


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

    @GetMapping("/searchByName")
    public ResponseEntity<List<HotelResponse>> showAllHotelByName(@RequestParam String hotelName) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(this
                        .uHotelService
                        .getHotelByName(hotelName));
    }

    @GetMapping("/show-hotel/{id}")
    public ResponseEntity<HotelResponse> showHotelInfo(@PathVariable Long id) {
        HotelResponse hotelResponse = this.uHotelService.showHotelInfo(id);
        if (hotelResponse != null)
            return ResponseEntity.ok(this.uHotelService.showHotelInfo(id));
        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("/hotelImage/{fileName}")
    public ResponseEntity<Resource> viewImage(@PathVariable String fileName) {
        try {
            Resource image = uHotelService.loadImage(fileName,true);
            String contentType = uHotelService.getImageContentType(fileName,true);

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
    @GetMapping("/{fileName}")
    public ResponseEntity<Resource> viewHotelImage(@PathVariable String fileName) {
        try {
            Resource image = uHotelService.loadImage(fileName,true);
            String contentType = uHotelService.getImageContentType(fileName,true);

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


    @GetMapping("/showHotelByLocation")
    public ResponseEntity<List<HotelResponse>> showHotelInfoByLocation(@RequestParam String location) {
       List<HotelResponse> hotelResponse = this.uHotelService.showAllHotelByLocation(location);
        if (hotelResponse != null)
            return ResponseEntity.ok(hotelResponse);
        return ResponseEntity.status(400).body(Collections.emptyList());
    }

    @GetMapping("/showHotelDirect")
    public ResponseEntity<List<HotelResponse>> showAllHotelDirect() {
        return ResponseEntity.ok(this.uHotelService.showHotelDirect());
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
            return ResponseEntity.status(400).body(Collections.emptyList());
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
        return ResponseEntity.status(400).body(Collections.emptyList());
     }

    @GetMapping("/placeImage/{fileName}")
    public ResponseEntity<Resource> viewPlaceImage(@PathVariable String fileName) {
        try {
            Resource image = uHotelService.loadImage(fileName,false);
            String contentType = uHotelService.getImageContentType(fileName,false);

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

    @GetMapping("/filterAvailableRoomsInHotel/{hotelId}")
    public ResponseEntity<Set<RoomResponse>> filterAvailableRoomsInHotel(@PathVariable Long hotelId,@RequestParam LocalDate startDate,@RequestParam LocalDate endDate){

        Set<RoomResponse> responses = this.uHotelService.filterAvailableRoomsInHotel(hotelId, startDate, endDate);

        if (responses!=null){
            return ResponseEntity.ok(responses);
        }


        return ResponseEntity.status(400).body(Collections.emptySet());
    }


    @GetMapping("/filterAvailableRoomsInAllHotel")
    public ResponseEntity<?> filterAvailableRoomsInAllHotel(
            @RequestParam String address,@RequestParam LocalDate startDate,@RequestParam LocalDate endDate ){
        return this.uHotelService.filterAvailableRoomsInAllHotel(address, startDate, endDate);

    }





    }

