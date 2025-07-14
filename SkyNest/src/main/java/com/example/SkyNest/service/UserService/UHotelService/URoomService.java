package com.example.SkyNest.service.UserService.UHotelService;


import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.dto.hoteldto.UserRoomResponse;
import com.example.SkyNest.model.entity.hotel.Hotel;
import com.example.SkyNest.model.entity.hotel.Room;
import com.example.SkyNest.model.entity.hotel.RoomImage;
import com.example.SkyNest.model.repository.hotel.HotelRepository;
import com.example.SkyNest.model.repository.hotel.RoomImageRepository;
import com.example.SkyNest.model.repository.hotel.RoomRepository;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class URoomService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Value("${image.upload.room}")
    private String uploadRoomImage;

        public List<UserRoomResponse> getAllRoomInHotel(Long hotelId){

          Optional<Hotel> hotelOptional = this.hotelRepository.findById(hotelId);
          if (hotelOptional.isEmpty()){
              return null;
          }
          List<Room> roomList = roomRepository.findByHotelId(hotelId);
          if (roomList.size() <= 0){
              return null;
          }

          return getRoomResponse(roomList);

      }

        private  List<UserRoomResponse> getRoomResponse(List< Room > rooms) {
            List<UserRoomResponse> roomResponseList = new ArrayList<>();

            for (Room room : rooms){
                List<RoomImage> roomImages  = roomImageRepository.findByRoomId(room.getId());
                List<ImageDTO> imageDTOList  = new ArrayList<>();
                UserRoomResponse roomResponse = new UserRoomResponse();
                roomResponse.setId(room.getId());
                roomResponse.setBasePrice(room.getBasePrice());
                roomResponse.setCurrentPrice(room.getCurrentPrice());
                roomResponse.setRoom_count(room.getRoomCount());
                if (room.getRoomType().equals(TripTypeAndReservation.Deluxe)){
                    roomResponse.setRoom_type("Deluxe");
                }else{
                    roomResponse.setRoom_type("Regular");
                }
                roomResponseList.add(roomResponse);
                for (RoomImage roomImage : roomImages) {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(roomImage.getId());
                    imageDTO.setImageUrl("/user/room/" + roomImage.getName());
                    imageDTOList.add(imageDTO);
                }
                roomResponse.setImageDTOList(imageDTOList);
            }
            return roomResponseList;
        }

        public UserRoomResponse getRoomInformation(Long roomId){

            Optional<Room> room = this.roomRepository.findById(roomId);
            if (room.isEmpty()){
                return null;
            }
            List<RoomImage> roomImageList = this.roomImageRepository.findByRoomId(roomId);
            List<ImageDTO> imageDTOList = new ArrayList<>();
            UserRoomResponse roomResponse = new UserRoomResponse();
            roomResponse.setId(room.get().getId());
            roomResponse.setBasePrice(room.get().getBasePrice());
            roomResponse.setCurrentPrice(room.get().getCurrentPrice());
            roomResponse.setRoom_count(room.get().getRoomCount());
            if (room.get().getRoomType().equals(TripTypeAndReservation.Deluxe)){
                roomResponse.setRoom_type("Deluxe");

            }else{
                roomResponse.setRoom_type("Regular");
            }
            for (RoomImage roomImage : roomImageList) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImage.getId());
                imageDTO.setImageUrl("/user/room/" + roomImage.getName());
                imageDTOList.add(imageDTO);
            }
            roomResponse.setImageDTOList(imageDTOList);

            return roomResponse;
        }

    public Resource loadImage(String fileName) throws IOException {


        Path filePath = Paths.get(uploadRoomImage).resolve(fileName);


        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {
        Path filePath = Paths.get(uploadRoomImage).resolve(fileName);
        return Files.probeContentType(filePath);
    }




}


