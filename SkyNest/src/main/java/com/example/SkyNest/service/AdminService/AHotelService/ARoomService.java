package com.example.SkyNest.service.AdminService.AHotelService;

import com.example.SkyNest.dto.ImageDTO;
import com.example.SkyNest.dto.RoomRequest;
import com.example.SkyNest.dto.RoomResponse;
import com.example.SkyNest.dto.RoomUpdateRequest;
import com.example.SkyNest.model.entity.Hotel;
import com.example.SkyNest.model.entity.Room;
import com.example.SkyNest.model.entity.RoomImage;
import com.example.SkyNest.model.entity.User;
import com.example.SkyNest.model.repository.HotelRepository;
import com.example.SkyNest.model.repository.RoomImageRepository;
import com.example.SkyNest.model.repository.RoomRepository;
import com.example.SkyNest.model.repository.UserRepository;
import com.example.SkyNest.service.authService.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.query.spi.QueryOptionsAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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

    // fetch hotel by user id then check  if user can accesses to this hotel
    public Map<String,String> createRoom(Long hotel_id, RoomRequest roomInfo){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotel = this.hotelRepository.findByUserId(userID);
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
        room.setStatus(false);
        room.setHotel(hotel.get());
        this.roomRepository.save(room);

        return Map.of(
                "message",
                "Successfully Room Added");
    }

    public Map<String,String> uploadImageToRoom(Long roomId,MultipartFile image) throws IOException {
        String jwt = request.getHeader("Authorization");
        String token  = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<Hotel> hotel = this.hotelRepository.findByUserId(userId);
        if (hotel.isEmpty()){
            return Map.of("message","this hotel is not found");
        }
        Optional<Room> room = this.roomRepository.findByIdAndHotelId(roomId,hotel.get().getId());
        if (room.isEmpty()){
            return Map.of("message","this room is not found in your hotel");
        }
        String imagePath = uploadImageRoom+image.getOriginalFilename();
        String uniqueImageName = UUID.randomUUID()+"_"+image.getOriginalFilename();
        RoomImage roomImage = new RoomImage();
        roomImage.setName(uniqueImageName);
        roomImage.setPath(imagePath);
        roomImage.setType(image.getContentType());
        roomImage.setRoom(room.get());
        this.roomImageRepository.save(roomImage);
        image.transferTo(new File(imagePath).toPath());
        return Map.of("message","Successfully Upload");
    }

    public byte[] viewImage(String imageName) throws Exception {
     Optional<RoomImage> image = this.roomImageRepository.findByName( imageName);
     if (image.isPresent()){
         String path = image.get().getPath();
         File file = new File(path);
         if (!file.exists()) throw new FileNotFoundException("Image file not found");
         return Files.readAllBytes(file.toPath());
     }
       return null;
    }
    
    public List<RoomResponse> getAllRoom(Long id){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByUserId(userID);
        if (hotelOptional.isEmpty()){
            return null;
        }
        if (!Objects.equals(hotelOptional.get().getId(), id)){
            return null;
        }
        Optional<Hotel> hotel  = this.hotelRepository.findById(id);
        if (hotel.isEmpty()){
            return null;
        }
        
        List<Room> rooms = this.roomRepository.findByHotelId(id);
        if (rooms.isEmpty()){
            return null;
        }
        return getRoomResponses(rooms);

    }

    public List<RoomResponse> getBookingRoom(Long id){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByUserId(userID);
        if (hotelOptional.isEmpty()){
            return null;
        }
        if (!Objects.equals(hotelOptional.get().getId(), id)){
            return null;
        }
        List<Room> rooms  = this.roomRepository.findByStatusAndHotelId(true,id);
        if (rooms.isEmpty()){
            return null;
        }

        return getRoomResponses(rooms);
    }

    public List<RoomResponse> getNotBookingRoom(Long id){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userID = jwtService.extractId(token);
        Optional<Hotel> hotelOptional = this.hotelRepository.findByUserId(userID);
        if (hotelOptional.isEmpty()){
            return null;
        }
        if (!Objects.equals(hotelOptional.get().getId(), id)){
            return null;
        }
        List<Room> rooms  = this.roomRepository.findByStatusAndHotelId(false,id);
        if (rooms.isEmpty()){
            return null;
        }

        return getRoomResponses(rooms);
    }

    private  List<RoomResponse> getRoomResponses(List<Room> rooms) {
        List<RoomResponse> roomResponseList = new ArrayList<>();
        List<ImageDTO> imageDTOList  = new ArrayList<>();
        for (Room room : rooms){
            List<RoomImage> roomImages  = roomImageRepository.findByRoomId(room.getId());
            RoomResponse roomResponse = new RoomResponse();
            roomResponse.setId(room.getId());
            roomResponse.setBasePrice(room.getBasePrice());
            roomResponse.setCurrentPrice(room.getCurrentPrice());
            roomResponse.setRoom_count(room.getRoomCount());
            roomResponse.setRoom_type(room.getRoomType());
            roomResponse.setStatus(room.isStatus());
            roomResponse.setHotelName(room.getHotel().getName());
            roomResponse.setOwnerName(room.getHotel().getUser().getFullName());
            roomResponseList.add(roomResponse);
            for (int i = 0; i <roomImages.size() ; i++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImages.get(i).getId());
                imageDTO.setImageUrl("http://localhost:8080/admin/hotel/"+roomImages.get(i).getName());
                imageDTOList.add(imageDTO);
            }
            roomResponse.setImageDTOList(imageDTOList);
        }
        return roomResponseList;
    }

    public Map<String,String> updateRoomInfo(Long hotelId, Long roomId, RoomUpdateRequest roomRequest){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<Hotel> hotel = this.hotelRepository.findByUserId(userId);
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

    public Map<String, String> deleteRoom(Long hotelId, Long roomId) {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = this.jwtService.extractId(token);
        Optional<Hotel> hotelOpt = this.hotelRepository.findByUserId(userId);
        if (hotelOpt.isEmpty()) {
            return Map.of("message",
                    "I'm Sorry , you don't have hotel yet");
        }
        if (!Objects.equals(hotelOpt.get().getId(), hotelId)) {
            return Map.of("message",
                    "I'm Sorry , you can't access to this hotel");
        }
        Optional<Room> room = this.roomRepository.findByIdAndHotelId(roomId, hotelId);
        if (room.isEmpty()) {
            return Map.of("message",
                    "I'm Sorry , this room is not found in your hotel");
        }
        this.roomRepository.delete(room.get());
        return Map.of("message",
                "Successfully Deleted");
    }

    @Transactional
    public Map<String ,String> updateRoomPrice(Long roomId, double price){
        String jwt = request.getHeader("Authorization");
        String token  = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this account is not found in our system");
        }
        Optional<Hotel> hotelOptional = this.hotelRepository.findByUserId(userId);
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

        Room room = roomOptional.get();
        room.setCurrentPrice(price);
        this.roomRepository.save(room);
        return Map.of("message",
                "Successfully  updated price");
    }




}
