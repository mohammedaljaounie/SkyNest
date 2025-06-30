package com.example.SkyNest.service.UserService.UHotelService;


import com.example.SkyNest.dto.ImageDTO;
import com.example.SkyNest.dto.UserRoomResponse;
import com.example.SkyNest.model.entity.Hotel;
import com.example.SkyNest.model.entity.Room;
import com.example.SkyNest.model.entity.RoomImage;
import com.example.SkyNest.model.repository.HotelRepository;
import com.example.SkyNest.model.repository.RoomImageRepository;
import com.example.SkyNest.model.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
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
            List<ImageDTO> imageDTOList  = new ArrayList<>();
            for (Room room : rooms){
                List<RoomImage> roomImages  = roomImageRepository.findByRoomId(room.getId());
                UserRoomResponse roomResponse = new UserRoomResponse();
                roomResponse.setId(room.getId());
                roomResponse.setBasePrice(room.getBasePrice());
                roomResponse.setCurrentPrice(room.getCurrentPrice());
                roomResponse.setRoom_count(room.getRoomCount());
                roomResponse.setRoom_type(room.getRoomType());
                roomResponseList.add(roomResponse);
                for (RoomImage roomImage : roomImages) {
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(roomImage.getId());
                    imageDTO.setImageUrl("/user/hotel/" + roomImage.getName());
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
            roomResponse.setRoom_type(room.get().getRoomType());
            for (RoomImage roomImage : roomImageList) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImage.getId());
                imageDTO.setImageUrl("/user/room/" + roomImage.getName());
                imageDTOList.add(imageDTO);
            }
            roomResponse.setImageDTOList(imageDTOList);

            return roomResponse;
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




    }


