package com.example.SkyNest.service.UserService.UHotelService;

import com.example.SkyNest.dto.*;
import com.example.SkyNest.model.entity.*;
import com.example.SkyNest.model.repository.*;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private HotelImageRepository hotelImageRepository;

    private static  String textOfRoom ="";

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
                imageDTO.setImageUrl("http://localhost:8080/user/hotel/hotelImage/"+hotelImageList.get(j).getName());
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
            imageDTO.setImageUrl("http://localhost:8080/user/hotel/"+imageList.get(i).getName());
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
                imageDTO.setImageUrl("http://localhost:8080/user/hotel/hotelImage/" + hotelImage.getName());
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

    // todo booking room direct
    public Map<String,String> roomBookingDirect(Long hotelId, Long roomId, HotelRoomRequest hotelRoomRequest){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isEmpty()){
            return Map.of(
                    "message",
                    "I'm Sorry , you can't booking any thing because you not auth"
            );
        }
        Optional<UserCard> userCard  = this.userCardRepository.findByUserId(userId);
        if (userCard.isEmpty()){
            return Map.of(
                    "message",
                    "I'm Sorry , you don't have card to payment booking"
            );
        }
        Optional<Hotel> hotel = this.hotelRepository.findById(hotelId);
        if (hotel.isEmpty()){
            return Map.of(
                    "message",
                    "I'm Sorry , This hotel is not found in our application"
            );
        }

        Optional<Room> room  = this.roomRepository.findByIdAndHotelId(roomId,hotelId);

        if (room.isEmpty()){
            return Map.of(
                    "message",
                    "I'm Sorry , This room is not found in this hotel"
            );
        }
        boolean status = room.get().isStatus();
        if (status){
            return Map.of(
                    "message",
                    "I'm Sorry , This room is booking"
            );
        }
        double roomPrice = room.get().getBasePrice();
        double amountToBePaid  = roomPrice*hotelRoomRequest.getPaymentRatio()/100;
        double totalUserBalance = userCard.get().getTotalBalance();
        if (totalUserBalance<amountToBePaid){
            savePublicBooking( hotelRoomRequest.getLaunchDate(), hotelRoomRequest.getDepartureDate(),user.get(),-1,null,0,null);
        }
        int roomNumber = room.get().getRoomCount();
        String reservedRoomNumber = ""+roomNumber;
        List<Long> list = new ArrayList<>();
        list.add(room.get().getId());
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setUser(user.get());
        hotelBooking.setHotel(hotel.get());
        hotelBooking.setNumberOfPerson(hotelRoomRequest.getNumberOfPerson());
        hotelBooking.setNumberOfRoom(1);
        hotelBooking.setPaymentRatio(hotelRoomRequest.getPaymentRatio());
        hotelBooking.setRoomType(room.get().getRoomType());
        hotelBooking.setStatus(true);
        hotelBooking.setLaunchDate(hotelRoomRequest.getLaunchDate());
        hotelBooking.setDepartureDate(hotelRoomRequest.getDepartureDate());
        hotelBooking.setListOfReservedRoomNumbers(list);
        hotelBooking.setTotalAmount(roomPrice);
        hotelBooking.setAmountPaid(amountToBePaid);
        HotelBooking hotelBooking1 = this.hotelBookingRepository.save(hotelBooking);
        savePublicBooking(hotelRoomRequest.getLaunchDate(),hotelRoomRequest.getDepartureDate(),
        user.get(),1,reservedRoomNumber,roomPrice,hotelBooking1);
        return Map.of("message",
                "your reservation has been successfully completed.\n" +
                        "wr hope you have a comfortable stay",
                "Booking start",hotelRoomRequest.getLaunchDate().toString(),
                "End of reservation",hotelRoomRequest.getDepartureDate().toString());
    }

    // todo booking in hotel
    public Map<String,String> roomBooking(Long hotelId, HotelBookingRequest hotelBookingRequest){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isEmpty()){
            return Map.of("message",
                    "I'm Sorry , you can't booking any thing because is not authorization in our application");
        }
        Optional<Hotel> hotel = this.hotelRepository.findById(hotelId);
        if (hotel.isEmpty()){
            return Map.of("message",
                    "this hotel is not found in this application");
        }
        Optional<HotelCard> hotelCard = this.hotelCardRepository.findByHotelId(hotelId);
        if (hotelCard.isEmpty()){
            return Map.of("message",
                    "this hotel don't have card to payment");
        }
        if (checkDate(hotelBookingRequest.getLaunchDate(),hotelBookingRequest.getDepartureDate())){
            return Map.of("message",
                    "I'm Sorry , you can't book because the time you want has passed");
        }
         List<Room> allRoomInTheHotel =checkRoom(false, hotelBookingRequest.getRoomType(), hotelId);
        if (allRoomInTheHotel.size()<hotelBookingRequest.getNumberOfRoom()){
            return Map.of("message",
                    "I'm Sorry , we don't have your request now");
        }
        double totalBookingCost = calculateTotalCost(allRoomInTheHotel,hotelBookingRequest.getNumberOfRoom());
        double amountToBePaid  = totalBookingCost*hotelBookingRequest.getPaymentRatio()/100;
        Optional<UserCard> userCard = this.userCardRepository.findByUserId(userId);
        if (userCard.isEmpty()){
            return Map.of("message","I'm Sorry , you don't have any card to payment");
        }
        double totalUserBalance  = userCard.get().getTotalBalance();
        if (totalUserBalance<amountToBePaid){
            savePublicBooking(
                    hotelBookingRequest.getLaunchDate(),
                    hotelBookingRequest.getDepartureDate(),
                    user.get(),-1,null
                    ,totalBookingCost,null);
            return Map.of("message",
                    "I'm sorry , you don't have enough money to pay");
        }
        updateUserCard(userCard.get(), amountToBePaid);
        updateHotelCard(hotelCard.get(), amountToBePaid);
        List<Long> listOfReservedRoomNumbers = updateRoomStatus(allRoomInTheHotel,hotelBookingRequest.getNumberOfRoom());
        HotelBooking hotelBooking =saveBooking(hotelBookingRequest,user.get(),hotel.get(),listOfReservedRoomNumbers,totalBookingCost,amountToBePaid);
        savePublicBooking(
                hotelBookingRequest.getLaunchDate(),
                hotelBookingRequest.getDepartureDate(),
                user.get(),1
        ,textOfRoom,totalBookingCost,hotelBooking);
        return Map.of("message",
                "your reservation has been successfully completed.\n" +
                        "wr hope you have a comfortable stay",
        "Booking start",hotelBookingRequest.getLaunchDate().toString(),
            "End of reservation",hotelBookingRequest.getDepartureDate().toString());
    }
    private static boolean checkDate(LocalDate LaunchDate,LocalDate DepartureDate){
        LocalDate localDate = LocalDate.now();
        return localDate.isAfter(LaunchDate)
                ||localDate.isAfter(DepartureDate)
                ||LaunchDate.isAfter(DepartureDate);
    }
    private  List<Room> checkRoom(boolean status,String roomType,Long hotelId){
        return roomRepository.findByStatusAndRoomTypeAndHotelId(status, roomType, hotelId);
    }
    private static double calculateTotalCost(List<Room> rooms ,int numberOfRoom){
        double totalCost =0;
        for (int i = 0; i < numberOfRoom ; i++) {

            totalCost = totalCost+rooms.get(i).getCurrentPrice();
        }
        return totalCost;
    }
    private  List<Long> updateRoomStatus(List<Room> rooms ,int numberOfRoom){
        textOfRoom ="";
       List<Long> listOfRoomBooking  = new ArrayList<>();
        for (int i = 0; i <numberOfRoom ; i++) {
            Room room = rooms.get(i);
            room.setStatus(true);
            listOfRoomBooking.add(room.getId());
            textOfRoom = textOfRoom+rooms.get(i).getRoomCount();
            if (i<numberOfRoom-1)
                textOfRoom = textOfRoom+"-";
            this.roomRepository.save(room);
        }
        return listOfRoomBooking;
    }
    private void updateUserCard(UserCard userCard,double amountToBePaid){
        userCard.setTotalBalance(userCard.getTotalBalance()-amountToBePaid);
        this.userCardRepository.save(userCard);
    }
    private void updateHotelCard(HotelCard hotelCard,double amountToBePaid){
        hotelCard.setTotalBalance(hotelCard.getTotalBalance()+amountToBePaid);
        this.hotelCardRepository.save(hotelCard);
    }
    private HotelBooking saveBooking(HotelBookingRequest hotelBookingRequest,User user,Hotel hotel,List<Long> listOfReservedRoomNumbers
           ,double totalAmount,double amountPaid){
        HotelBooking hotelBooking = new HotelBooking();
        hotelBooking.setUser(user);
        hotelBooking.setHotel(hotel);
        hotelBooking.setNumberOfPerson(hotelBookingRequest.getNumberOfPerson());
        hotelBooking.setNumberOfRoom(hotelBookingRequest.getNumberOfRoom());
        hotelBooking.setPaymentRatio(hotelBookingRequest.getPaymentRatio());
        hotelBooking.setRoomType(hotelBookingRequest.getRoomType());
        hotelBooking.setStatus(true);
        hotelBooking.setLaunchDate(hotelBookingRequest.getLaunchDate());
        hotelBooking.setDepartureDate(hotelBookingRequest.getDepartureDate());
        hotelBooking.setListOfReservedRoomNumbers(listOfReservedRoomNumbers);
        hotelBooking.setTotalAmount(totalAmount);
        hotelBooking.setAmountPaid(amountPaid);
       return this.hotelBookingRepository.save(hotelBooking);
    }
    private void savePublicBooking(LocalDate LaunchDate, LocalDate DepartureDate, User user,int status,String listOfReservedRoomNumbers,double totalAmount,
                                   HotelBooking hotelBooking){
        UserBooking userBooking = new UserBooking();
        userBooking.setBookingType("Hotel Reservation");
        userBooking.setBookingStartDate(LaunchDate);
        userBooking.setBookingEndDate(DepartureDate);
        userBooking.setStatus(status);
        userBooking.setUser(user);
        userBooking.setListOfReservedRoomNumbers(listOfReservedRoomNumbers);
        userBooking.setTotalAmount(totalAmount);
        userBooking.setHotelBooking(hotelBooking);
        this.userBookingRepository.save(userBooking);
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
                imageDTO.setImageUrl("http://localhost:8080/user/hotel/"+hotelImageList.get(j).getName());
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


    public Map<String,String> bookingCansel(Long userBookingId){

        String jwt = request.getHeader("Authorization");
        String token =jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty())
            return Map.of(
                    "message",
                    "you can't do any thing , because you isn't aut"
            );

        Optional<UserBooking> userBooking = this.userBookingRepository.findByIdAndUserId(userBookingId,userId);

        if (userBooking.isEmpty())
            return Map.of("message"
            ,"this booking is not found");

        if ((userBooking.get().getStatus()==-1))
          return Map.of("message",
                  "This reservation is not acceptable in principle, meaning that the cancellation process cannot be completed. ");
        if(userBooking.get().getBookingStartDate().isBefore(LocalDate.now())){
            return Map.of("message",
                    "you can't cansel this booking because you led in date");
        }

        Optional<HotelBooking> hotelBooking = this.hotelBookingRepository.findById(userBooking.get().getHotelBooking().getId());

        if (hotelBooking.isEmpty())
            return Map.of("message",
                    "This hotel is not found in our application now");

            List<Room> rooms = this.roomRepository.findAllById(hotelBooking.get().getListOfReservedRoomNumbers());

        for (Room room : rooms) {
            room.setStatus(false);
            System.out.println(room.getId());
            this.roomRepository.save(room);
        }

        // todo : update userBooking && hotelBooking
        UserBooking userBookingUpdate = userBooking.get();
        userBookingUpdate.setStatus(0);
        this.userBookingRepository.save(userBookingUpdate);

        HotelBooking hotelBookingUpdate = hotelBooking.get();
        hotelBookingUpdate.setStatus(false);
        this.hotelBookingRepository.save(hotelBookingUpdate);



        // todo : update userCard && hotelCard
        double cancellationTax = (hotelBooking.get().getTotalAmount())*0.05;
        double amountPaid = hotelBooking.get().getAmountPaid();
        updateModifyAmounts(hotelBooking.get().getHotel().getId(),
                userId,(amountPaid-cancellationTax));
        return Map.of(

                "message",
                "Successfully Canceled"
        );

    }


    private void updateModifyAmounts(Long hotelId,Long userId,double cancellationTax){

        Optional<HotelCard> hotelCardOptional = this.hotelCardRepository.findByHotelId(hotelId);
        if (hotelCardOptional.isEmpty()) {
        }
        else {

            Optional<UserCard> userCardOptional  = this.userCardRepository.findByUserId(userId);
            if (userCardOptional.isEmpty())
                return;
            HotelCard hotelCard = hotelCardOptional.get();
            hotelCard.setTotalBalance((hotelCard.getTotalBalance()-cancellationTax));
            this.hotelCardRepository.save(hotelCard);


            UserCard userCard = userCardOptional.get();
            userCard.setTotalBalance(userCard.getTotalBalance()+cancellationTax);
            this.userCardRepository.save(userCard);
        }

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
                imageDTO.setImageUrl("http://localhost:8080/user/hotel/hotelImage/"+hotelImage.getName());
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
}
