package com.example.SkyNest.service.SuperAdminService.SAHotelService;

import com.example.SkyNest.dto.hoteldto.HotelRequest;
import com.example.SkyNest.dto.hoteldto.HotelRequestUpdate;
import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.dto.hoteldto.SAHotelResponse;
import com.example.SkyNest.model.entity.hotel.Hotel;
import com.example.SkyNest.model.entity.hotel.HotelCard;
import com.example.SkyNest.model.entity.hotel.HotelImage;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.hotel.*;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
// SA -> S : it mean Super && A : it mean Admin --> SA : this is mean [ SuperAdmin ]
public class SAHotelService {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HotelCardRepository hotelCardRepository;

    @Autowired
    private HotelImageRepository hotelImageRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private RoomRepository roomRepository;
     @Autowired
     private RoomImageRepository roomImageRepository;
    @Value("${image.upload.dir}")
    private String uploadDir;


    public Map<String,String> createHotel(HotelRequest hotelInfo){
        Optional<User> user = this.userRepository.findByEmail(hotelInfo.getEmail());
        if (user.isPresent()){
            if (isAdmin(user.get())){
                Hotel hotel  = loadHotelToDB(hotelInfo, user.get());
                createHotelCard(hotel);

                return Map.of(
                        "message",
                        "Successfully Hotel Added"
                );
            }
            return Map.of(
                    "message",
                    "Not Successfully Hotel Added , This email to user not admin"
            );
        }else {
            return Map.of(
                    "message",
                    "Not Successfully Hotel Added , This email is wrong Try again"
            );
        }
    }
    private boolean isAdmin(User user){
        return user.getRole().getName().equals("admin");
    }
    private Hotel loadHotelToDB(HotelRequest hotelInfo, User user){
        Hotel hotel  = new Hotel();
        hotel.setName(hotelInfo.getName());
        hotel.setAddress(hotelInfo.getAddress());
        hotel.setLongitude(hotelInfo.getLongitude());
        hotel.setLatitude(hotelInfo.getLatitude());
        hotel.setDescription(hotelInfo.getDescription());
        hotel.setAvgRating(0);
        hotel.setRatingCount(0);
        hotel.setUser(user);
        return this.hotelRepository.save(hotel);
    }
    private void createHotelCard(Hotel hotel){
        HotelCard hotelCard = new HotelCard();
        hotelCard.setHotel(hotel);
        hotelCard.setTotalBalance(0);
        hotelCardRepository.save(hotelCard);
    }

    public Map<String,String> updateHotel(HotelRequestUpdate hotelInfo){
        Optional<Hotel> hotel = this.hotelRepository.findById(hotelInfo.getHotel().getId());
        if (hotel.isPresent()){
            Hotel hotelUpdate = hotel.get();
            if (hotelInfo.getName()!=null) {
                hotelUpdate.setName(hotelInfo.getName());
            }
            if (hotelInfo.getLongitude()!=0){
                hotelUpdate.setLongitude(hotelInfo.getLongitude());
            }
            if (hotelInfo.getLatitude()!=0){
                hotelUpdate.setLatitude(hotelInfo.getLatitude());
            }
            if (hotelInfo.getAddress()!=null){
                hotelUpdate.setAddress(hotelInfo.getAddress());
            }
            if (hotelInfo.getDescription()!=null){
                hotelUpdate.setDescription(hotelInfo.getDescription());
            }

            this.hotelRepository.save(hotelUpdate);

            return Map.of(
                    "message",
                    "Successfully Hotel Updated"
            );
        }

        return Map.of(
                "message",
                "Not Successfully Updated"
        );

    }

    public Map<String,String> deleteHotel(Long id){

        Optional<Hotel> hotel = this.hotelRepository.findById(id);
        if (hotel.isPresent()){
            System.out.println("This is in side");
            this.hotelImageRepository.deleteAllByHotelId(id);
            System.out.println("Image");
            this.hotelBookingRepository.deleteByHotelId(id);
            System.out.println("Reservation");
            this.hotelCardRepository.deleteByHotelId(id);
            System.out.println("hotel card");
            this.roomImageRepository.deleteByHotelId(id);
            System.out.println("room image");
            this.roomRepository.deleteByHotelId(id);
            System.out.println("room");
            this.hotelRepository.deleteById(hotel.get().getId());
            System.out.println("hotel");
            return Map.of(
                    "message",
                    "Successfully Deleted"
            );

        }

        return  Map.of(
                "message",
                "Not Successfully Deleted , this hotel is not found in our application"
        );



    }

    public List<SAHotelResponse> showAllHotels(){
        ArrayList<SAHotelResponse> listHotelInfo = new ArrayList<>();
        List<Hotel> hotelList = this.hotelRepository.findAll();
        if (hotelList.isEmpty()){
            return null;
        }else {
            for (int i = 0; i < this.hotelRepository.findAll().size(); i++) {
                SAHotelResponse hotelResponse = new SAHotelResponse();
                hotelResponse.setHotelId(hotelList.get(i).getId());
                hotelResponse.setHotelName(hotelList.get(i).getName());
                hotelResponse.setAddress(hotelList.get(i).getAddress());
                hotelResponse.setLongitude(hotelList.get(i).getLongitude());
                hotelResponse.setLatitude(hotelList.get(i).getLatitude());
                hotelResponse.setDescription(hotelList.get(i).getDescription());
                hotelResponse.setAvgRating(hotelList.get(i).getAvgRating());
                hotelResponse.setRatingCount(hotelList.get(i).getRatingCount());
                hotelResponse.setOwnerName(hotelList.get(i).getUser().getFullName());
                hotelResponse.setOwnerEmail(hotelList.get(i).getUser().getEmail());

                List<ImageDTO>  imageDTOList = new ArrayList<>();
                for (HotelImage hotelImage: hotelList.get(i).getHotelImageList()){
                    ImageDTO imageDTO = new ImageDTO();
                    imageDTO.setId(hotelImage.getId());
                    imageDTO.setImageUrl("http://localhost:8080/super_admin/hotelImage/"+hotelImage.getName());
                    imageDTOList.add(imageDTO);
                }
                hotelResponse.setImageDTOS(imageDTOList);
                listHotelInfo.add(hotelResponse);
            }
            return listHotelInfo;
        }
    }

    public SAHotelResponse showHotelInfo(Long id){
        Optional<Hotel> hotel = this.hotelRepository.findById(id);
        if (hotel.isPresent()){
            SAHotelResponse hotelResponse  = new SAHotelResponse();
            hotelResponse.setHotelId(hotel.get().getId());
            hotelResponse.setHotelName(hotel.get().getName());
            hotelResponse.setAddress(hotel.get().getAddress());
            hotelResponse.setLongitude(hotel.get().getLongitude());
            hotelResponse.setLatitude(hotel.get().getLatitude());
            hotelResponse.setDescription(hotel.get().getDescription());
            hotelResponse.setAvgRating(hotel.get().getAvgRating());
            hotelResponse.setRatingCount(hotel.get().getRatingCount());
            hotelResponse.setOwnerName(hotel.get().getUser().getFullName());
            hotelResponse.setOwnerEmail(hotel.get().getUser().getEmail());
            List<ImageDTO> imageDTOList = new ArrayList<>();
            for (HotelImage hotelImage: hotel.get().getHotelImageList()){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImage.getId());
                imageDTO.setImageUrl("http://localhost:8080/super_admin/hotelImage/"+hotelImage.getName());
                imageDTOList.add(imageDTO);
            }
            hotelResponse.setImageDTOS(imageDTOList);
            return hotelResponse ;
        }
        return null;
    }


    public Resource loadImage(String fileName) throws IOException {


        Path filePath = Paths.get(uploadDir).resolve(fileName);


        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {
        Path filePath = Paths.get(uploadDir).resolve(fileName);
        return Files.probeContentType(filePath);
    }




}
