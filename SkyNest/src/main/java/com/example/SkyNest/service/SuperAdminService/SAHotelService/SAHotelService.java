package com.example.SkyNest.service.SuperAdminService.SAHotelService;

import com.example.SkyNest.dto.HotelRequest;
import com.example.SkyNest.dto.HotelRequestUpdate;
import com.example.SkyNest.dto.SAHotelResponse;
import com.example.SkyNest.model.entity.hotel.Hotel;
import com.example.SkyNest.model.entity.hotel.HotelCard;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.hotel.HotelCardRepository;
import com.example.SkyNest.model.repository.hotel.HotelRepository;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            this.hotelRepository.delete(hotel.get());
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
                hotelResponse.setHotelName(hotelList.get(i).getName());
                hotelResponse.setAddress(hotelList.get(i).getAddress());
                hotelResponse.setLongitude(hotelList.get(i).getLongitude());
                hotelResponse.setLatitude(hotelList.get(i).getLatitude());
                hotelResponse.setDescription(hotelList.get(i).getDescription());
                hotelResponse.setAvgRating(hotelList.get(i).getAvgRating());
                hotelResponse.setRatingCount(hotelList.get(i).getRatingCount());
                hotelResponse.setOwnerName(hotelList.get(i).getUser().getFullName());
                hotelResponse.setOwnerEmail(hotelList.get(i).getUser().getEmail());
                listHotelInfo.add(hotelResponse);
            }
            return listHotelInfo;
        }
    }

    public SAHotelResponse showHotelInfo(Long id){
        Optional<Hotel> hotel = this.hotelRepository.findById(id);
        if (hotel.isPresent()){
            SAHotelResponse hotelResponse  = new SAHotelResponse();
            hotelResponse.setHotelName(hotel.get().getName());
            hotelResponse.setAddress(hotel.get().getAddress());
            hotelResponse.setLongitude(hotel.get().getLongitude());
            hotelResponse.setLatitude(hotel.get().getLatitude());
            hotelResponse.setDescription(hotel.get().getDescription());
            hotelResponse.setAvgRating(hotel.get().getAvgRating());
            hotelResponse.setRatingCount(hotel.get().getRatingCount());
            hotelResponse.setOwnerName(hotel.get().getUser().getFullName());
            hotelResponse.setOwnerEmail(hotel.get().getUser().getEmail());
            return hotelResponse ;
        }
        return null;
    }







}
