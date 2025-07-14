package com.example.SkyNest.service.UserService.UHotelService;

import com.example.SkyNest.dto.hoteldto.*;
import com.example.SkyNest.model.entity.hotel.*;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.entity.userDetails.UserCard;
import com.example.SkyNest.model.repository.hotel.*;
import com.example.SkyNest.model.repository.userDetails.UserCardRepository;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.DateStatus;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
    private  HotelRatingRepository hotelRatingRepository;
    @Autowired
    private RoomImageRepository roomImageRepository;

    @Autowired
    private HotelImageRepository hotelImageRepository;
    @Value("${image.upload.dir}")
    private String uploadDir;

    @Value("${image.upload.place.near.hotel}")
    private String uploadImagePlace;



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
            hotel.setAvgRating(hotelList.get(i).getAvgRating());
            //  todo : give hotel image from hotels and put this image in correct hotel
            List<HotelImage> hotelImageList =  hotelList.get(i).getHotelImageList();

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (int j = 0; j <hotelImageList.size() ; j++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImageList.get(j).getId());
                imageDTO.setImageUrl("/user/hotel/hotelImage/"+hotelImageList.get(j).getName());
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
            imageDTO.setImageUrl("/user/hotel/hotelImage/"+imageList.get(i).getName());
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
        System.out.println(longitude);
        System.out.println(latitude);
//        List<Hotel> hotelList = this.hotelRepository.findAll();
        List<Hotel> hotelList = this.hotelRepository.findNearestHotels(latitude,longitude,20);
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
                imageDTO.setImageUrl("/user/hotel/hotelImage/" + hotelImage.getName());
                imageDTOS.add(imageDTO);
            }

            hotelResponse.setImageDTOList(imageDTOS);
            hotelResponseList.add(hotelResponse);
        }
        return hotelResponseList;
    }

    public Resource loadImage(String fileName,boolean isHotel) throws IOException {

        Path filePath = null;
        if (isHotel){
             filePath = Paths.get(uploadDir).resolve(fileName);
        }else{
            filePath = Paths.get(uploadImagePlace).resolve(fileName);

        }

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName,boolean isHotel) throws IOException {
        Path filePath = null;
        if (isHotel){
            filePath = Paths.get(uploadDir).resolve(fileName);
        }else{
            filePath = Paths.get(uploadImagePlace).resolve(fileName);

        }
        return Files.probeContentType(filePath);
    }



    //حجوزات ساريه
    public List<UserBookingResponse> viewActiveReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        List<HotelBooking> hotelBookingList = this.hotelBookingRepository.findByStatusAndUserId(StatusEnumForBooking.Activated,userId);

        return  getUserBookingResponse(hotelBookingList, StatusEnumForBooking.Activated);
    }

    // حجوزات ملغيه
    public List<UserBookingResponse> viewCanceledReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        List<HotelBooking> hotelBookingList    = this.hotelBookingRepository.findByStatusAndUserId(StatusEnumForBooking.Canceled,userId);

        return getUserBookingResponse(hotelBookingList, StatusEnumForBooking.Canceled);
    }

    // حجوزات مرفوضه
    public List<UserBookingResponse> viewIncorrectReservation(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        List<HotelBooking> hotelBookingList = this.hotelBookingRepository.findByStatusAndUserId(StatusEnumForBooking.Unacceptable,userId);

   return getUserBookingResponse(hotelBookingList, StatusEnumForBooking.Unacceptable);

    }


    private List<UserBookingResponse> getUserBookingResponse(
            List<HotelBooking> hotelBookingList, StatusEnumForBooking statusEnum){

        List<UserBookingResponse> userBookingResponseArrayList = new ArrayList<>();
        for (HotelBooking hotelBooking : hotelBookingList){
            UserBookingResponse userBookingResponse = new UserBookingResponse();
            userBookingResponse.setBookingId(hotelBooking.getId());
            userBookingResponse.setBookingStartDate(hotelBooking.getLaunchDate());
            userBookingResponse.setBookingEndDate(hotelBooking.getDepartureDate());
            userBookingResponse.setBookingType("Hotel reservation");
            userBookingResponse.setHotelName(hotelBooking.getHotel().getName());
            userBookingResponse.setNumberOfNights(UHotelService.calculateDaysBetweenTowDate(hotelBooking.getLaunchDate(),hotelBooking.getDepartureDate()));
            userBookingResponse.setAddress(hotelBooking.getHotel().getAddress());
            userBookingResponse.setTotalCost(hotelBooking.getTotalAmount());
           if (statusEnum== StatusEnumForBooking.Activated)
            userBookingResponse.setStatusBooking("Activated");
           else if (statusEnum== StatusEnumForBooking.Canceled)
                userBookingResponse.setStatusBooking("Canceled");
           else
               userBookingResponse.setStatusBooking("Unacceptable");

            userBookingResponseArrayList.add(userBookingResponse);
        }

        return userBookingResponseArrayList;
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
            hotel.setAvgRating(hotelList.get(i).getAvgRating());
            // todo : give hotel image from hotels and put this image in correct hotel
            List<HotelImage> hotelImageList =  hotelList.get(i).getHotelImageList();

            List<ImageDTO> imageDTOS = new ArrayList<>();
            for (int j = 0; j <hotelImageList.size() ; j++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(hotelImageList.get(j).getId());
                imageDTO.setImageUrl("/user/hotel/hotelImage"+hotelImageList.get(j).getName());
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
                imageDTO.setImageUrl("/user/hotel/hotelImage/"+hotelImage.getName());
                imageDTOList.add(imageDTO);
            }

            hotelResponse.setImageDTOList(imageDTOList);
            hotelResponseList.add(hotelResponse);

        }

        return hotelResponseList;
    }



    @Transactional
    public Map<String ,String> hotelEvaluation(Long hotelId,int rating,String comment){
         if (rating>5||rating<1){
             return Map.of("message",
                     "this rating is not allow , it is high");
         }

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
                hotelBookingRepository.findByUserIdAndHotelIdAndStatus(userId, hotelId, StatusEnumForBooking.Activated);

       if (hotelBookingList.isEmpty()){
           return false;
       }else {

           for (HotelBooking hotelBooking : hotelBookingList) {

               if (!LocalDate.now().isBefore(hotelBooking.getLaunchDate())){
                   return true;
               }
           }
           return false;

       }



    }

    /**
     * Booking List Of Room
     * */

    @Transactional
    public ResponseEntity<?> bookingRooms(HotelBookingRequest bookingRequest)
    {
      if (!UHotelService.checkDate(bookingRequest.getLaunchDate(),bookingRequest.getDepartureDate())){
          // todo : stop because the date is wrong
          return ResponseEntity.status(400).body(Map.of("message",
                  "Wrong in date"));
      }

      String jwt = request.getHeader("Authorization");
      String token = jwt.substring(7);
      Long userId = jwtService.extractId(token);
      Optional<User> user = this.userRepository.findById(userId);
      if (user.isEmpty()){

          //todo : stop because this user is not register
           return ResponseEntity.status(403).body(Map.of("message",
                   "your not register in our application"));
      }
        System.out.println("User");
      Optional<UserCard> userCard = this.userCardRepository.findByUserId(user.get().getId());
      if (userCard.isEmpty()){

          //todo : stop because user not has card to pay
          return ResponseEntity.status(403).body(Map.of("message","you don't have card to pay"));
      }
        System.out.println("UserCard");

      Optional<Hotel> hotel = this.hotelRepository.findById(bookingRequest.getHotelId());
      if (hotel.isEmpty()){
          // todo : stop because  the hotel is not found
          return ResponseEntity.status(400).body(Map.of("message","This hotel is not found in our app, booking in other hotel"));
      }

      Optional<HotelCard> hotelCard = this.hotelCardRepository.findByHotelId(hotel.get().getId());
      if (hotelCard.isEmpty()){

          // todo : stop because the hotel is not has card
          return ResponseEntity.status(403).body(Map.of("message","i'm sorry , this hotel don't have card now,you can't booking in this hotel"));
      }

            //      الغرف يلي موجوده بالطلب
        List<Room> realRooms = roomRepository.findAllById(bookingRequest.getSetOfRooms());

        //  todo : check if all rooms in same hotel
                for (Room room : realRooms) {
                    if (room.getHotel()==null||!(room.getHotel().getId() .equals( hotel.get().getId()))) {

                        //todo : stop because this room is not found in same hotel
                        return ResponseEntity.status(400).body("some room is not found in same hotel");

                    }

                }

       Set<RoomResponse> notBockedRoom =filterAvailableRoomsInHotel(
               bookingRequest.getHotelId(),
               bookingRequest.getLaunchDate(),
               bookingRequest.getDepartureDate()
       );


        // check by size
        if (notBockedRoom.isEmpty()||notBockedRoom.size()<bookingRequest.getSetOfRooms().size()){
            //todo : stop because all room is bocked
            return ResponseEntity.status(400).body(Map.of("message","i'm sorry , all rooms are bocked "));
        }

// حساب عدد الغرف المتوفره لعملية الحجز (يعني بشوف الغرف يلي طلبها المستخدم اذا كلها متوفره ضمن الغرف الفاضيه او لا )
        int counter = 0;
           for (Room room :realRooms){

               for (RoomResponse roomResponse : notBockedRoom){
                   if (room.getId().equals(roomResponse.getId())){
                       counter++;
                       break;
                   }
               }
           }
           // التحقق اذا عدد الغرف المتوفره هو نفسه عدد الغرف المطلوبه
           if (counter!=bookingRequest.getSetOfRooms().size()){
               // todo : some rooms are bocked
               return ResponseEntity.status(400).body(Map.of("message","i'm sorry , all rooms are bocked "));
           }


        double totalCost = UHotelService.calculateTotalPrice(realRooms,bookingRequest.getLaunchDate(),bookingRequest.getDepartureDate());

        double theAmountToBePaid = totalCost* bookingRequest.getPaymentRatio()/100;


        if (!UHotelService.checkUserCard(userCard.get(),theAmountToBePaid)){

 // todo : update hotel booking


            this.hotelBookingRepository.save(
                    new HotelBooking(
                            bookingRequest.getNumberOfPerson(),
                            bookingRequest.getSetOfRooms().size(),
                            StatusEnumForBooking.Unacceptable,bookingRequest.getLaunchDate(),
                            bookingRequest.getDepartureDate(),
                            bookingRequest.getPaymentRatio(),totalCost,totalCost,0,
                            user.get(),
                            hotel.get(),
                            new HashSet<>(realRooms)
                    )
            );

            // todo : stop because user don't have Sufficient amount
            return ResponseEntity.status(400).body(Map.of("message","i'm sorry , you don't have mony "));
        }

        updateUserCard(userCard.get(), theAmountToBePaid,false);
        updateHotelCard(hotelCard.get(), theAmountToBePaid,true);

        // update room status if start date = today
        this.hotelBookingRepository.save(
                new HotelBooking(
                        bookingRequest.getNumberOfPerson(),
                        bookingRequest.getSetOfRooms().size(),
                        StatusEnumForBooking.Activated,bookingRequest.getLaunchDate(),
                        bookingRequest.getDepartureDate(),
                        bookingRequest.getPaymentRatio(),totalCost,totalCost,theAmountToBePaid,
                        user.get(),
                        hotel.get(),
                        new HashSet<>(realRooms)
                )
        );

        return ResponseEntity.ok(Map.of("message",
                "Successfully booking"));

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

        List<HotelBooking> hotelBookingList = this.hotelBookingRepository.filterByDate(hotelId, startDate, endDate, StatusEnumForBooking.Activated);

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

    public ResponseEntity<?> filterAvailableRoomsInAllHotel(
            String address,LocalDate startDate,LocalDate endDate ){
        if (!UHotelService.checkDate(startDate,endDate)){
            return ResponseEntity.status(400).body(Map.of("message","Wrong in date, correct your date"));
        }

        List<Hotel> hotelList = this.hotelRepository.findByAddress(address);

        if (hotelList.isEmpty()){
            return ResponseEntity.status(400).body(Map.of("message","This hotel is not found in our application"));
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


        return ResponseEntity.ok(availableRooms);
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
                imageDTO.setImageUrl("/user/hotel/hotelImage" + roomImage.getName());
                roomImages.add(imageDTO);
            }
            roomResponse.setImageDTOList(roomImages);
            listOfRoom.add(roomResponse);

        }
        return listOfRoom;
    }

    private static boolean checkDate(LocalDate startDate,LocalDate endDate){
        return !startDate.isBefore(LocalDate.now()) &&
                !startDate.isAfter(endDate);
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
                imageDTO.setImageUrl("/user/hotel/hotelImage"+roomImage.getName());
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

        roomResponse.setOwnerName(room.getHotel().getUser().getFullName());
        roomResponse.setBasePrice(room.getBasePrice());
        roomResponse.setCurrentPrice(room.getCurrentPrice());
        return roomResponse;
    }

    private static double calculateTotalPrice(List<Room> rooms,LocalDate startDate,LocalDate endDate){

        long totalDaysOfStay = UHotelService.calculateDaysBetweenTowDate(startDate, endDate);

        double totalCost = 0;
        for (Room room : rooms) {

            totalCost = totalCost +( room.getCurrentPrice()*totalDaysOfStay);
        }
        return totalCost;
    }

    private  static long  calculateDaysBetweenTowDate(LocalDate startDate,LocalDate endDate){

        return ChronoUnit.DAYS.between(startDate,endDate)+1;
    }

    private static boolean checkUserCard(UserCard  userCard,double $Mon){
        return userCard.getTotalBalance() >= $Mon;
    }

    private  void updateUserCard(UserCard userCard,double amountToBePaid,boolean isDecrease){
        if (isDecrease) {
            userCard.setTotalBalance(userCard.getTotalBalance() + amountToBePaid);
        }else {
            userCard.setTotalBalance(userCard.getTotalBalance() - amountToBePaid);
        }
        this.userCardRepository.save(userCard);
    }
    private  void updateHotelCard(HotelCard hotelCard,double amountToBePaid,boolean isDecrease){
        if (isDecrease) {
            hotelCard.setTotalBalance(hotelCard.getTotalBalance() + amountToBePaid);
        }
        else {
            hotelCard.setTotalBalance(hotelCard.getTotalBalance() - amountToBePaid);
        }
       this.hotelCardRepository.save(hotelCard);
    }


    @Transactional
    public Map<String,String> cancelBooking(Long bookingId){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            // message explain that this user is not found in our application
            return Map.of("message",
                    "sorry,you are  not found in our application");
        }
        Optional<UserCard>  userCard = this.userCardRepository.findByUserId(userId);
        if (userCard.isEmpty()){
            return Map.of("message",
                    "sorry, you don't have card to return your mony on it");
        }
        Optional<HotelBooking> hotelBooking = this.hotelBookingRepository.findById(bookingId);
        if (hotelBooking.isEmpty()){
            // message explain that this booking is not found
            return Map.of("message",
                    "sorry , this reservation is not found in system yet");
        }
        if (hotelBooking.get().getStatus()!= StatusEnumForBooking.Activated){
            return Map.of("message",
                    "sorry , this reservation isn't Activated");
        }

        if (!hotelBooking.get().getUser().getId().equals(userId)){
            // message explain , the person that send this request is not equal the person that have this booking
            return Map.of("message",
                    "sorry , this reservation is not found in your reservation");
        }

        Optional<HotelCard> hotelCard = this.hotelCardRepository.findByHotelId(hotelBooking.get().getHotel().getId());
        if (hotelCard.isEmpty()){
            return Map.of("message",
                    "sorry , this hotel isn't have card to get mony from it");
        }


        DateStatus dateStatus=verifyTheFine(hotelBooking.get().getLaunchDate());

        boolean isCancel = false;
        switch (dateStatus){
            case FREE : {
                System.out.println("FREE");
                HotelBooking updateHotelBooking = hotelBooking.get();
                updateHotelBooking.setStatus(StatusEnumForBooking.Canceled);
                updateUserCard(userCard.get(), updateHotelBooking.getAmountPaid(),true);
                updateHotelCard(hotelCard.get(), updateHotelBooking.getAmountPaid(),false);
                this.hotelBookingRepository.save(updateHotelBooking);
                isCancel=true;
            }
                break;
            case A_FINE:{
                System.out.println("A_FINE");
                HotelBooking updateHotelBooking = hotelBooking.get();
                double fine = updateHotelBooking.getCurrentTotalAmount()*5/100;
                updateHotelBooking.setStatus(StatusEnumForBooking.Canceled);
                updateUserCard(userCard.get(), updateHotelBooking.getAmountPaid()-fine,true);
                updateHotelCard(hotelCard.get(), updateHotelBooking.getAmountPaid()-fine,false);
                this.hotelBookingRepository.save(updateHotelBooking);
                isCancel=true;

            }
                break;
            case FULL_AMOUNT:{
                System.out.println("FULL_AMOUNT");
                HotelBooking updateHotelBooking = hotelBooking.get();
                updateHotelBooking.setStatus(StatusEnumForBooking.Canceled);
                this.hotelBookingRepository.save(updateHotelBooking);
                isCancel=true;

            }
                break;

        }
        if (isCancel){
            return Map.of("message",
                    "Successfully Canceled");
        }
        return Map.of("message",
                "Not Successfully Canceled");

    }

    private DateStatus verifyTheFine(LocalDate startDate) {
        long afterDate = ChronoUnit.DAYS.between(LocalDate.now(), startDate);
        if (afterDate >= 3 && LocalDate.now().isBefore(startDate)) {
            return DateStatus.FREE;
        } else if (afterDate < 3 && afterDate > 0 && LocalDate.now().isBefore(startDate)) {
            return DateStatus.A_FINE;
        } else {
            return DateStatus.FULL_AMOUNT;
        }
    }

}


