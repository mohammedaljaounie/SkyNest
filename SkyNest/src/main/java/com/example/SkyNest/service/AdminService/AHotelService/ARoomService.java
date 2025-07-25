package com.example.SkyNest.service.AdminService.AHotelService;

import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.dto.hoteldto.RoomRequest;
import com.example.SkyNest.dto.hoteldto.RoomResponse;
import com.example.SkyNest.dto.hoteldto.RoomUpdateRequest;
import com.example.SkyNest.firebase.service.FirebaseService;
import com.example.SkyNest.model.entity.hotel.*;
import com.example.SkyNest.model.entity.userDetails.Notification;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.entity.userDetails.UserCard;
import com.example.SkyNest.model.repository.hotel.*;
import com.example.SkyNest.model.repository.userDetails.NotificationRepo;
import com.example.SkyNest.model.repository.userDetails.UserCardRepository;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.RoomStatus;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class ARoomService {

    @Value("${image.upload.room}")
    private String uploadImageRoom;

    @Autowired
    private RoomRepository roomRepository ;

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private HotelCardRepository hotelCardRepository ;
    @Autowired
    private UserCardRepository userCardRepository;
    @Autowired
    private FirebaseService firebaseService;
    @Autowired
    private NotificationRepo notificationRepo;

    // fetch hotel by user id then check  if user can accesses to this hotel
    public Map<String,String> createRoom(Long hotel_id, RoomRequest roomInfo){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotel = this.hotelRepository.findByIdAndUserId(hotel_id,userID);
        if (hotel.isEmpty()){
            return Map.of(
                    "message",
                   "I'm Sorry , you can't create room because of this hotel is not found in our application" );
        }
        if (!Objects.equals(hotel.get().getId(), hotel_id)){
            return Map.of(
                    "message",
                    "I'm Sorry , you can't create room because of you can't able to accesses this hotel" );
        }

        int rooms = this.roomRepository.countByHotelId(hotel_id);

        Room room = new Room();
        room.setRoomType(roomInfo.getRoomType());
        room.setBasePrice(roomInfo.getPrice());
        room.setCurrentPrice(roomInfo.getPrice());
        room.setRoomCount(rooms+1);
        room.setStatus(RoomStatus.EMPTY);
        room.setNumberOfPerson(roomInfo.getNumberOfPerson());
        room.setNumberOfBed(roomInfo.getNumberOfBed());
        if(roomInfo.isHasKitchen()){
            room.setIsHasKitchen("Deluxe Kitchen");
        }else {
            room.setIsHasKitchen("Regular Kitchen");
        }

        room.setHotel(hotel.get());
        this.roomRepository.save(room);

        return Map.of(
                "message",
                "Successfully Room Added");
    }

    public Map<String,String> uploadImageToRoom(Long hotelId,Long roomId,MultipartFile image) throws IOException {
        String jwt = request.getHeader("Authorization");
        String token  = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<Hotel> hotel = this.hotelRepository.findByIdAndUserId(hotelId,userId);
        if (hotel.isEmpty()){
            return Map.of("message","this hotel is not found");
        }
        Optional<Room> room = this.roomRepository.findByIdAndHotelId(roomId,hotel.get().getId());
        if (room.isEmpty()){
            return Map.of("message","this room is not found in your hotel");
        }

        String contentType = image.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IOException(" can you upload only JPG و PNG");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String fileName = UUID.randomUUID() + extension;

        Path folderPath = Paths.get(uploadImageRoom);
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        RoomImage roomImage = new RoomImage();
        roomImage.setName(fileName);
        roomImage.setPath(folderPath.toString()+fileName);
        roomImage.setType(image.getContentType());
        roomImage.setRoom(room.get());
        this.roomImageRepository.save(roomImage);

        return Map.of("message","Successfully Upload");
    }
    public Resource loadImage(String fileName) throws IOException {

          Path  filePath = Paths.get(uploadImageRoom).resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {
        Path filePath = Paths.get(uploadImageRoom).resolve(fileName);
        return Files.probeContentType(filePath);
    }
    
    public ResponseEntity<List<RoomResponse>> getAllRoom(Long hotelID){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(hotelID,userID);
        if (hotelOptional.isEmpty()){
            return ResponseEntity.status(400).body(null);
        }
        List<Room> rooms = this.roomRepository.findByHotelId(hotelID);
        if (rooms.isEmpty()){
            return null;
        }
        return ResponseEntity.ok(getRoomResponses(rooms));

    }

    public List<RoomResponse> getBookingRoom(Long hotelId){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(hotelId,userID);
        if (hotelOptional.isEmpty()){
            return null;
        }
        if (!Objects.equals(hotelOptional.get().getId(), hotelId)){
            return null;
        }
        List<Room> rooms  = this.roomRepository.findByStatusAndHotelId(RoomStatus.BOOKING,hotelId);
        if (rooms.isEmpty()){
            return null;
        }

        return getRoomResponses(rooms);
    }

    public List<RoomResponse> getNotBookingRoom(Long hotelId){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(hotelId,userID);
        if (hotelOptional.isEmpty()){
            return null;
        }
        if (!Objects.equals(hotelOptional.get().getId(), hotelId)){
            return null;
        }
        List<Room> rooms  = this.roomRepository.findByStatusAndHotelId(RoomStatus.EMPTY,hotelId);
        if (rooms.isEmpty()){
            return null;
        }

        return getRoomResponses(rooms);
    }

    private  List<RoomResponse> getRoomResponses(List<Room> rooms) {
        List<RoomResponse> roomResponseList = new ArrayList<>();

        for (Room room : rooms){
            List<RoomImage> roomImages  = roomImageRepository.findByRoomId(room.getId());
            List<ImageDTO> imageDTOList  = new ArrayList<>();
            RoomResponse roomResponse = new RoomResponse();
            roomResponse.setId(room.getId());
            roomResponse.setBasePrice(room.getBasePrice());
            roomResponse.setCurrentPrice(room.getCurrentPrice());
            roomResponse.setNumberOfPerson(room.getNumberOfPerson());
            roomResponse.setNumberOfBed(room.getNumberOfBed());
            roomResponse.setIsHasKitchen(room.getIsHasKitchen());
            roomResponse.setRoom_count(room.getRoomCount());
            if (room.getRoomType().equals(TripTypeAndReservation.Deluxe)){
                roomResponse.setRoom_type("Deluxe");

            }else {
                roomResponse.setRoom_type("Regular");

            }
            if (room.isStatus().equals(RoomStatus.BOOKING)){
                roomResponse.setStatus("Booking");
            }else{
                roomResponse.setStatus("Empty");
            }

            roomResponse.setHotelName(room.getHotel().getName());
            roomResponse.setOwnerName(room.getHotel().getUser().getFullName());

            for (RoomImage roomImage : roomImages) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImage.getId());
                imageDTO.setImageUrl("http://localhost:8080/admin/room/" + roomImage.getName());
                imageDTOList.add(imageDTO);
            }
            roomResponse.setImageDTOList(imageDTOList);
            roomResponseList.add(roomResponse);
        }
        return roomResponseList;
    }

    public Map<String,String> updateRoomInfo(Long hotelId, Long roomId, RoomUpdateRequest roomRequest){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional  = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){

            return Map.of("message",
                    "This user is not register in our application");
        }
        Optional<Hotel> hotel = this.hotelRepository.findByIdAndUserId(hotelId,userId);
        if (!Objects.equals(hotel.get().getId(),hotelId)){
            return Map.of("message","I'm sorry you can't access to this hotel");
        }
        Optional<Room> room = this.roomRepository.findByIdAndHotelId(roomId,hotelId);
        if (room.isEmpty()){
            return Map.of("message","I'm sorry this room is not found in you hotel");
        }
        Room roomUpdate = room.get();
        roomUpdate.setBasePrice(roomRequest.getPrice());
        roomUpdate.setRoomCount(roomRequest.getRoom_count());
        roomUpdate.setRoomType(roomRequest.getRoom_type());
        roomUpdate.setStatus(roomRequest.isStatus());
        this.roomRepository.save(roomUpdate);
        return Map.of("message",
                "Successfully Updated");
    }

    @Transactional
    public Map<String, String> deleteRoom(Long hotelId, Long roomId) {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = this.jwtService.extractId(token);

        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this account is not found in our system");
        }

        Optional<Hotel> hotelOpt = this.hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()) {
            return Map.of("message",
                    "I'm Sorry , this hotel is not found");
        }

        if (!Objects.equals(hotelOpt.get().getUser().getId(), userId)) {
            return Map.of("message",
                    "I'm Sorry , you can't access to this hotel");
        }
        Optional<Room> room = this.roomRepository.findByIdAndHotelId(roomId, hotelId);
        if (room.isEmpty()) {
            return Map.of("message",
                    "I'm Sorry , this room is not found in your hotel");
        }

        delete(hotelId,room.get());
        this.roomImageRepository.deleteAllByRoomId(roomId);
        this.roomRepository.deleteById(room.get().getId());
        return Map.of("message",
                "Successfully Deleted");
    }


    private void delete(Long hotelId,Room roomInfo){
        List<HotelBooking> bookingList = this.hotelBookingRepository.findAllByHotelId(hotelId);

        for (HotelBooking hotelBooking : bookingList){
          Set<Room> updateRoom = new HashSet<>();
         for (Room room : hotelBooking.getRooms()){

             if ((room.getId().equals(roomInfo.getId())))
             {
                 LocalDate today = LocalDate.now();
                 LocalDate start = hotelBooking.getLaunchDate();
                 LocalDate end = hotelBooking.getDepartureDate();
                 StatusEnumForBooking isActive = hotelBooking.isStatus();

                 if (( !today.isBefore(start) && !today.isAfter(end) ) && isActive== StatusEnumForBooking.Activated) {

                     //المبلغ الكلي للغرفه خلال الايام يلي رح يتم الغائها من الحجز
                     double totalPrice = ARoomService.calculateTotalPrice(LocalDate.now(), hotelBooking.getDepartureDate(), room.getCurrentPrice());
                     double discountedAmount = totalPrice + (hotelBooking.getCurrentTotalAmount() * 5 / 100);
                     if (hotelBooking.getPaymentRatio() == 100) {

                         // todo : update user card and hotel card + 5%
                         updateHotelCard(hotelId, discountedAmount);
                         updateUserCard(hotelBooking.getUser().getId(), discountedAmount);
                         hotelBooking.setCurrentTotalAmount(hotelBooking.getCurrentTotalAmount() - discountedAmount);
                         // todo : send notification

                     } else {

                         // todo : update total cost to booking  + 5%
                         hotelBooking.setCurrentTotalAmount(hotelBooking.getCurrentTotalAmount() - discountedAmount);
                         // todo : send notification

                     }
                 }

             }

             else
             {
                 updateRoom.add(room);
             }



         }
            hotelBooking.setRooms(updateRoom);

        }
        this.hotelBookingRepository.saveAll(bookingList);

    }

    private static double calculateTotalPrice(LocalDate startDate,LocalDate endDate,double roomPrice){
        long totalDays = ChronoUnit.DAYS.between(startDate,endDate)+1;

        return roomPrice*totalDays;
    }

    private void updateUserCard(Long  userId, double amount){

        Optional<UserCard> userCardOptional = this.userCardRepository.findByUserId(userId);
        if (userCardOptional.isEmpty())
            return;
        UserCard userCard = userCardOptional.get();
        userCard.setTotalBalance(userCard.getTotalBalance()+amount);
        this.userCardRepository.save(userCard);
    }

    private void updateHotelCard(Long  hotelId , double amount){
        Optional<HotelCard> hotelCardOptional = this.hotelCardRepository.findByHotelId(hotelId);
        if (hotelCardOptional.isEmpty())
            return;
        HotelCard hotelCard = hotelCardOptional.get();
        hotelCard.setTotalBalance(hotelCard.getTotalBalance()-amount);
        this.hotelCardRepository.save(hotelCard);
    }

    @Transactional
    public Map<String ,String> updateRoomPrice(Long hotelId,Long roomId, double price) throws Exception {
        String jwt = request.getHeader("Authorization");
        String token  = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this account is not found in our system");
        }
        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(hotelId,userId);
        if (hotelOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this hotel is not found in our app");
        }

        if (price<=0){
            return Map.of("message",
                    "Sorry , Wrong price should not be zero or down");
        }
        Optional<Room> roomOptional = this.roomRepository.findById(roomId);
        if (roomOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this room is not found in our system");
        }
        if(!roomOptional.get().getHotel().getId().equals(hotelOptional.get().getId())){
          return Map.of("message",
                  "Sorry , you can't update room price your not authorization");
        }
        if (price<roomOptional.get().getCurrentPrice()){
            List<User> users = this.userRepository.findAllByRoleName("user");

            for (User user : users){
                String title = "SkyNest Application";
                String body ="Hello Mohammed,\n" +
                        " there are special offers available for you at Al Sham Hotel.\n" +
                        " Hurry up to book and take advantage of the offer to achieve maximum savings.";
                if (user.getFcmToken()!=null){
                   this.firebaseService.sendNotification(title,body, user.getFcmToken());
                   this.notificationRepo.save(new Notification(title+"\n"+body,user));
                }
            }
            Room room = roomOptional.get();
            room.setCurrentPrice(price);
            this.roomRepository.save(room);
        }else {
            Room room = roomOptional.get();
            room.setCurrentPrice(price);
            this.roomRepository.save(room);
        }
        return Map.of("message",
                "Successfully  updated price");
    }

}
