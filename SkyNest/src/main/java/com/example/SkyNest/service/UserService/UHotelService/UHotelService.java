package com.example.SkyNest.service.UserService.UHotelService;

import com.example.SkyNest.dto.*;
import com.example.SkyNest.model.entity.*;
import com.example.SkyNest.model.repository.*;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

@Service
public class UHotelService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserCardRepository userCardRepository;
    @Autowired
    private HotelCardRepository hotelCardRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HotelBookingRepository hotelBookingRepository;
    @Autowired
    private UserBookingRepository userBookingRepository;
    @Autowired
    private  HotelRatingRepository hotelRatingRepository;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Autowired
    private HotelImageRepository hotelImageRepository;

    private static final String textOfRoom ="";

    public List<HotelResponse> showAllHotel(){

        List<Hotel> hotelList = this.hotelRepository.findAll();

        if (hotelList.isEmpty()){
            return null;
        }
        List<HotelResponse> hotelResponsesList  = new ArrayList<>();

        for (int i = 0; i <hotelList.size() ; i++) {
            HotelResponse hotel  = new HotelResponse();
            hotel.setId(hotelList.get(i).getId());
            hotel.setName(hotelList.get(i).getName());
            hotel.setAddress(hotelList.get(i).getAddress());
            hotel.setDescription(hotelList.get(i).getDescription());
            hotel.setRatingCount(hotelList.get(i).getRatingCount());

            // todo : give hotel image from hotels and put this image in correct hotel
            List<HotelImage> hotelImageList =  hotelList.get(i).getHotelImageList();

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (int j = 0; j <hotelImageList.size() ; j++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImageList.get(j).getId());
                imageDTO.setImageUrl("8080/user/hotel/hotelImage/"+hotelImageList.get(j).getName());
                imageDTOS.add(imageDTO);
            }
            hotel.setImageDTOList(imageDTOS);

            hotelResponsesList.add(hotel);

        }
        return hotelResponsesList;
    }

    public HotelResponse showHotelInfo(Long id){

        Optional<Hotel> hotelOpt = this.hotelRepository.findById(id);
        if (hotelOpt.isEmpty()){
            return null;
        }
        Hotel hotelInfo  = hotelOpt.get();
        HotelResponse hotelResponse  = new HotelResponse();
        hotelResponse.setId(hotelInfo.getId());
        hotelResponse.setName(hotelInfo.getName());
        hotelResponse.setAddress(hotelInfo.getAddress());
        hotelResponse.setDescription(hotelInfo.getDescription());
        hotelResponse.setRatingCount(hotelInfo.getRatingCount());
        hotelResponse.setAvgRating(hotelInfo.getAvgRating());
        List<ImageDTO> imageResponseList = new ArrayList<>();
        List<HotelImage> imageList  = hotelInfo.getHotelImageList();

        for (int i = 0; i < imageList.size() ; i++) {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(imageList.get(i).getId());
            imageDTO.setImageUrl("8080/user/hotel/"+imageList.get(i).getName());
            imageResponseList.add(imageDTO);
        }
        hotelResponse.setImageDTOList(imageResponseList);

        return hotelResponse;
    }

    public List<HotelResponse> showHotelDirect(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        double longitude = jwtService.extractLongitude(token);
        double latitude  = jwtService.extractLatitude(token);
//        List<Hotel> hotelList = this.hotelRepository.findAll();
        List<Hotel> hotelList = this.hotelRepository.findClosestHotels(longitude,latitude,20);
        if (hotelList.isEmpty()){
            return null;
        }
       return getHotelResponse(hotelList);
    }

    private static List<HotelResponse> getHotelResponse(List<Hotel> hotelList){

        List<HotelResponse> hotelResponseList = new ArrayList<>();
        for (int i =0 ; i<hotelList.size();i++){
            HotelResponse hotelResponse  = new HotelResponse();
            hotelResponse.setId(hotelList.get(i).getId());
            hotelResponse.setAddress(hotelList.get(i).getAddress());
            hotelResponse.setName(hotelList.get(i).getName());
            hotelResponse.setDescription(hotelList.get(i).getDescription());
            hotelResponse.setAvgRating(hotelList.get(i).getAvgRating());
            hotelResponse.setRatingCount(hotelList.get(i).getRatingCount());
            List<ImageDTO> imageDTOS = new ArrayList<>();
            List<HotelImage> hotelImageList =  hotelList.get(i).getHotelImageList();

            for (HotelImage hotelImage : hotelImageList) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImage.getId());
                imageDTO.setImageUrl("8080/user/hotel/hotelImage/" + hotelImage.getName());
                imageDTOS.add(imageDTO);
            }

            hotelResponse.setImageDTOList(imageDTOS);
            hotelResponseList.add(hotelResponse);
        }
        return hotelResponseList;
    }

    public byte[] viewImage(String imageName) throws Exception {
        Optional<HotelImage> image = this.hotelImageRepository.findByName( imageName);
        if (image.isPresent()){
            String path = image.get().getPath();
            File file = new File(path);
            if (!file.exists()) throw new FileNotFoundException("Image file not found");
            return Files.readAllBytes(file.toPath());
        }
        return null;
    }

    private void updateUserCard(UserCard userCard,double amountToBePaid){
        userCard.setTotalBalance(userCard.getTotalBalance()-amountToBePaid);
        this.userCardRepository.save(userCard);
    }
    private void updateHotelCard(HotelCard hotelCard,double amountToBePaid){
        hotelCard.setTotalBalance(hotelCard.getTotalBalance()+amountToBePaid);
        this.hotelCardRepository.save(hotelCard);
    }


    public List<UserBookingResponse> viewActiveReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        LocalDate localDate = LocalDate.now();
        List<UserBooking> userBookings = this.userBookingRepository.findByUserId(userId);
        List<UserBookingResponse> bookingResponses  = new ArrayList<>();
        for (UserBooking userBooking : userBookings) {
            if (localDate.isAfter(userBooking.getBookingEndDate()) ||
                    localDate.isEqual(userBooking.getBookingEndDate())&&
            userBooking.getStatus()==1) {
                UserBookingResponse userBookingResponse = new UserBookingResponse();
                userBookingResponse.setId(userBooking.getId());
                userBookingResponse.setBookingType(userBooking.getBookingType());
                userBookingResponse.setBookingStartDate(userBooking.getBookingStartDate());
                userBookingResponse.setBookingEndDate(userBooking.getBookingEndDate());
                userBookingResponse.setListOfReservedRoomNumbers(userBooking.getListOfReservedRoomNumbers());
                bookingResponses.add(userBookingResponse);
            }
        }
return bookingResponses;

    }

    public List<UserBookingResponse> viewCanceledReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        List<UserBooking> userBookings = this.userBookingRepository.findByUserId(userId);
        List<UserBookingResponse> bookingResponses  = new ArrayList<>();
        for (UserBooking userBooking : userBookings) {
            if (userBooking.getStatus()==0) {
                UserBookingResponse userBookingResponse = new UserBookingResponse();
                userBookingResponse.setId(userBooking.getId());
                userBookingResponse.setBookingType(userBooking.getBookingType());
                userBookingResponse.setBookingStartDate(userBooking.getBookingStartDate());
                userBookingResponse.setBookingEndDate(userBooking.getBookingEndDate());
                userBookingResponse.setListOfReservedRoomNumbers(userBooking.getListOfReservedRoomNumbers());
                bookingResponses.add(userBookingResponse);
            }
        }
        return bookingResponses;

    }

    public List<UserBookingResponse> viewIncorrectReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        List<UserBooking> userBookings = this.userBookingRepository.findByUserId(userId);
        List<UserBookingResponse> bookingResponses  = new ArrayList<>();
        for (UserBooking userBooking : userBookings) {
            if (userBooking.getStatus()==-1) {
                UserBookingResponse userBookingResponse = new UserBookingResponse();
                userBookingResponse.setId(userBooking.getId());
                userBookingResponse.setBookingType(userBooking.getBookingType());
                userBookingResponse.setBookingStartDate(userBooking.getBookingStartDate());
                userBookingResponse.setBookingEndDate(userBooking.getBookingEndDate());
                userBookingResponse.setListOfReservedRoomNumbers(userBooking.getListOfReservedRoomNumbers());
                bookingResponses.add(userBookingResponse);
            }
        }
        return bookingResponses;

    }

    public List<HotelResponse> showAllHotelByLocation(String address){

        List<Hotel> hotelList = this.hotelRepository.findByAddress(address);

        if (hotelList.isEmpty()){
            return null;
        }
        List<HotelResponse> hotelResponsesList  = new ArrayList<>();

        for (int i = 0; i <hotelList.size() ; i++) {
            HotelResponse hotel  = new HotelResponse();
            hotel.setId(hotelList.get(i).getId());
            hotel.setName(hotelList.get(i).getName());
            hotel.setAddress(hotelList.get(i).getAddress());
            hotel.setDescription(hotelList.get(i).getDescription());
            hotel.setRatingCount(hotelList.get(i).getRatingCount());

            // todo : give hotel image from hotels and put this image in correct hotel
            List<HotelImage> hotelImageList =  hotelList.get(i).getHotelImageList();

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (int j = 0; j <hotelImageList.size() ; j++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImageList.get(j).getId());
                imageDTO.setImageUrl("8080/user/hotel/"+hotelImageList.get(j).getName());
                imageDTOS.add(imageDTO);
            }
            hotel.setImageDTOList(imageDTOS);

            hotelResponsesList.add(hotel);

        }
        return hotelResponsesList;
    }

    public double viewTotalBalance(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<UserCard> userCard = this.userCardRepository.findByUserId(userId);
        return userCard.map(UserCard::getTotalBalance).orElse(-1D);

    }


    public List<HotelResponse> filterHotelByRating(){
        List<Hotel> hotelList = this.hotelRepository.filterHotelByRating();

        if (hotelList.isEmpty())
            return null;
        List<HotelResponse> hotelResponseList = new ArrayList<>();
        for (int i = 0; i <hotelList.size() ; i++) {

            Hotel hotel = hotelList.get(i);
            HotelResponse hotelResponse  = new HotelResponse();
            hotelResponse.setId(hotel.getId());
            hotelResponse.setName(hotel.getName());
            hotelResponse.setDescription(hotel.getDescription());
            hotelResponse.setAvgRating(hotel.getAvgRating());
            hotelResponse.setRatingCount(hotel.getRatingCount());
            hotelResponse.setAddress(hotel.getAddress());

            // todo : this array to put hotel image
            List<ImageDTO> imageDTOList = new ArrayList<>();
            for (int j = 0; j <hotel.getHotelImageList().size() ; j++) {

                HotelImage hotelImage = hotel.getHotelImageList().get(j);

                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImage.getId());
                imageDTO.setImageUrl("8080/user/hotel/hotelImage/"+hotelImage.getName());
                imageDTOList.add(imageDTO);
            }

            hotelResponse.setImageDTOList(imageDTOList);
            hotelResponseList.add(hotelResponse);

        }

        return hotelResponseList;
    }



    @Transactional
    public Map<String ,String> hotelEvaluation(Long hotelId,int rating,String comment){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> user = this.userRepository.findById(userId);
        if (user.isEmpty())
            return Map.of("message",
                    "Sorry , your account is not found in our app");

        Optional<Hotel> hotel = this.hotelRepository.findById(hotelId);
        if (hotel.isEmpty())
            return Map.of("message",
                    "Sorry , this hotel is not found in our system");

        System.out.println(userId);
        System.out.println(hotelId);

        if (!isReservation(userId,hotelId)){
            return Map.of("message",
                    "Sorry , you can't rate this hotel now because you may not have booked or the review time is not right");
        }

        Optional<HotelRating> hotelRating  = this.hotelRatingRepository.findByUserIdAndHotelId(userId,hotelId);

        if (hotelRating.isEmpty()){
        HotelRating newHotelRating = new HotelRating();
        newHotelRating.setUser(user.get());
        newHotelRating.setRating(rating);
        newHotelRating.setComment(comment);
        newHotelRating.setHotel(hotel.get());
        this.hotelRatingRepository.save(newHotelRating);
        int sumRating  = this.hotelRatingRepository.sumForAllRating(hotelId);
         boolean isUpdated = updateHotelRating(hotelId, sumRating,true);
        if (isUpdated)
         return Map.of("message",
                "Successfully Rating");

       return Map.of("message",
                    "Not Successfully Rating");

        }else {
            HotelRating  hotelRating1 = hotelRating.get();
            hotelRating1.setRating(rating);
            hotelRating1.setComment(comment);
            this.hotelRatingRepository.save(hotelRating1);
            int sumRating = this.hotelRatingRepository.sumForAllRating(hotelId);
            boolean isUpdated = updateHotelRating(hotel.get().getId(),sumRating,false);
            if (isUpdated)
            return Map.of("message",
                        "Successfully Rating");

            return Map.of("message",
                    "Not Successfully Rating");
        }


    }

    private  boolean updateHotelRating(Long hotelId,double sumRating,boolean isNew){
        Optional<Hotel> hotel  =  this.hotelRepository.findById(hotelId);
        if (hotel.isEmpty())
            return false;
        Hotel updatedHotel = hotel.get();
        if (isNew) {
            updatedHotel.setRatingCount(updatedHotel.getRatingCount() + 1);
        }
        if (sumRating==0){
            updatedHotel.setAvgRating(sumRating);
        }else {
            int ratingCount = updatedHotel.getRatingCount();
            if (ratingCount==0)
                updatedHotel.setAvgRating(0);
            else {
                updatedHotel.setAvgRating(sumRating /ratingCount);
            }
        }
        this.hotelRepository.save(updatedHotel);
        return true;

    }

    private boolean isReservation(Long userId , Long hotelId){

        List<HotelBooking> hotelBookingList   = this.
                hotelBookingRepository.findByUserIdAndHotelId(userId, hotelId);

       if (hotelBookingList.isEmpty()){
           return false;
       }else {

           for (HotelBooking hotelBooking : hotelBookingList) {

               if (hotelBooking.isStatus()&&
                   !LocalDate.now().isBefore(hotelBooking.getLaunchDate())){
                   return true;
               }
           }
           return false;

       }



    }

    /**
     * Booking List Of Room
     * */

    public void bookingRooms()
    {

    }


    // todo filter room that not bocked
    public Set<RoomResponse> filterAvailableRoomsInHotel(Long hotelId, LocalDate startDate, LocalDate endDate){

        if (!UHotelService.checkDate(startDate,endDate)){
           return null;
       }


        Optional<Hotel> hotel = this.hotelRepository.findById(hotelId);
        if (hotel.isEmpty()){
            return null;
        }
        List<RoomResponse> roomResponseList = convertRoomToDTO(this.roomRepository.findByHotelId(hotelId));

        List<HotelBooking> hotelBookingList = this.hotelBookingRepository.filterByDate(hotelId, startDate, endDate,true);

        Set<RoomResponse> notBockedRoom  = new HashSet<>();
        if (!hotelBookingList.isEmpty()) {
            Set<Room> bockedRoom = new HashSet<>(); // list of room

            for (HotelBooking hotelBooking : hotelBookingList) {
                bockedRoom.addAll(hotelBooking.getRooms());
            }

            Set<RoomResponse> roomResponseSet = convertRoomToDTO(bockedRoom);// set of room response


            assert roomResponseList != null;
            for (RoomResponse roomResponse : roomResponseList) {
                assert roomResponseSet != null;
                if (!roomResponseSet.contains(roomResponse)) {
                    notBockedRoom.add(roomResponse);
                }
            }

        }else {

            assert roomResponseList != null;
            notBockedRoom.addAll(roomResponseList);
        }
        return notBockedRoom;
    }

    public Map<Long,Set<RoomResponse>> filterAvailableRoomsInAllHotel(
            String address,LocalDate startDate,LocalDate endDate ){
        if (!UHotelService.checkDate(startDate,endDate)){
            return null;
        }

        List<Hotel> hotelList = this.hotelRepository.findByAddress(address);

        if (hotelList.isEmpty()){
            return null;
        }

        Map<Long , Set<RoomResponse>> availableRooms = new HashMap<>();
        for (Hotel hotel : hotelList){
            availableRooms.put(

                    hotel.getId(),
                    filterAvailableRoomsInHotel(
                            hotel.getId(),
                            startDate,
                            endDate
                    )
            );
        }


        return availableRooms;
            }

    private List<RoomResponse> convertRoomToDTO(List<Room> rooms) {

        if (rooms.isEmpty())
            return null;

        List<RoomResponse> listOfRoom = new ArrayList<>();


        for (Room room : rooms) {
            List<ImageDTO> roomImages = new ArrayList<>();
            RoomResponse roomResponse = getRoomResponse(room);
            List<RoomImage> roomImageList = this.roomImageRepository.findByRoomId(room.getId());
            for (RoomImage roomImage : roomImageList) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImage.getId());
                imageDTO.setImageUrl("8080/user/hotel/" + roomImage.getName());
                roomImages.add(imageDTO);
            }
            roomResponse.setImageDTOList(roomImages);
            listOfRoom.add(roomResponse);

        }
        return listOfRoom;
    }

    private static boolean checkDate(LocalDate startDate,LocalDate endDate){
        return !startDate.isBefore(LocalDate.now()) &&
                !startDate.isAfter(endDate) &&
                !startDate.isBefore(LocalDate.now());
    }
    private  Set<RoomResponse> convertRoomToDTO(Set<Room> rooms){

        if (rooms.isEmpty())
            return null;

        Set<RoomResponse> listOfRoom = new HashSet<>();


        for (Room room : rooms) {
            List<ImageDTO> roomImages = new ArrayList<>();
            RoomResponse roomResponse = getRoomResponse(room);
            List<RoomImage> roomImageList = this.roomImageRepository.findByRoomId(room.getId());
            for (RoomImage roomImage : roomImageList ){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(roomImage.getId());
                imageDTO.setImageUrl("8080/user/hotel/"+roomImage.getName());
                roomImages.add(imageDTO);

            }
            roomResponse.setImageDTOList(roomImages);

            listOfRoom.add(roomResponse);

        }

        return listOfRoom;

    }
    private static RoomResponse getRoomResponse(Room room) {
        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setId(room.getId());
        roomResponse.setHotelName(room.getHotel().getName());
        roomResponse.setRoom_count(room.getRoomCount());
        roomResponse.setRoom_type(room.getRoomType());
        roomResponse.setStatus(room.isStatus());
        roomResponse.setOwnerName(room.getHotel().getUser().getFullName());
        roomResponse.setBasePrice(room.getBasePrice());
        roomResponse.setCurrentPrice(room.getCurrentPrice());
        return roomResponse;
    }

}
