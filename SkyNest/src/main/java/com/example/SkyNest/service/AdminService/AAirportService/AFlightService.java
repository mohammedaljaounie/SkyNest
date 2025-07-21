package com.example.SkyNest.service.AdminService.AAirportService;

import com.example.SkyNest.dto.airportdto.FlightInfoUpdate;
import com.example.SkyNest.dto.airportdto.FlightRequest;
import com.example.SkyNest.dto.airportdto.FlightResponse;
import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.firebase.service.FirebaseService;
import com.example.SkyNest.model.entity.flight.*;
import com.example.SkyNest.model.entity.userDetails.Notification;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.entity.userDetails.UserCard;
import com.example.SkyNest.model.repository.flight.*;
import com.example.SkyNest.model.repository.userDetails.NotificationRepo;
import com.example.SkyNest.model.repository.userDetails.UserCardRepository;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import com.example.SkyNest.service.authService.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AFlightService {

    @Value("${image.upload.flight}")
    private  String flightImagePath;


    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final FlightRepo flightRepo;
    private final AirportRepo airportRepo;
    private final UserRepository userRepository;
    private final FlightImageRepo flightImageRepo;
    private final FirebaseService firebaseService;
    private final NotificationRepo notificationRepo ;
    private final FlightBookingRepo flightBookingRepo;
    private final UserCardRepository userCardRepository;
    private final AirportCardRepo airportCardRepo;
    public AFlightService(
          HttpServletRequest httpServletRequest , JwtService jwtService,
            FlightRepo flightRepo, AirportRepo airportRepo,
            UserRepository userRepository,
            FlightImageRepo flightImageRepo, FirebaseService firebaseService, NotificationRepo notificationRepo
    ,FlightBookingRepo flightBookingRepo,UserCardRepository userCardRepository,AirportCardRepo airportCardRepo){

        this.request = httpServletRequest;
        this.jwtService = jwtService;
        this.flightRepo = flightRepo;
        this.airportRepo = airportRepo;
        this.userRepository = userRepository;
        this.flightImageRepo = flightImageRepo;
        this.firebaseService = firebaseService;
        this.notificationRepo = notificationRepo;
        this.flightBookingRepo = flightBookingRepo;
        this.userCardRepository = userCardRepository;
        this.airportCardRepo = airportCardRepo;
    }

    public Map<String,String> saveImageToFlight(Long flightId, MultipartFile image) throws Exception{
        if (image==null){
            return Map.of("message","Wrong, this image is empty");
        }
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId,"admin");
        if (user.isEmpty()){
            return Map.of("message","Wrong , may you don't have account in our app or you isn't admin");
        }
        Optional<Flight> flight = this.flightRepo.findById(flightId);
        if (flight.isEmpty()){
            return Map.of("message","Wrong , this flight is not found");
        }

        if (!flight.get().getAirport().getUser().getId().equals(userId)){
            return Map.of("message","wrong , you can't access on this flight");
        }

        String contentType = image.getContentType();
        if (!(contentType.equals("image/jpeg")||contentType.equals("image/png"))){
            throw new IOException(" can you upload only JPG و PNG");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String imageName = UUID.randomUUID()+extension;

        Path folderPath = Paths.get(flightImagePath);
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(imageName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        FlightImage flightImage = new FlightImage();
        flightImage.setName(imageName);
        flightImage.setPath(folderPath +imageName);
        flightImage.setType(image.getContentType());
        flightImage.setFlight(flight.get());
        this.flightImageRepo.save(flightImage);
        return Map.of("message","Successfully Uploaded");
    }

    public Resource loadImage(String fileName) throws IOException {

         Path   filePath = Paths.get(flightImagePath).resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {

          Path  filePath = Paths.get(flightImagePath).resolve(fileName);

        return Files.probeContentType(filePath);
    }




    @Transactional
     public Map<String,String> createFlight(FlightRequest flightRequest){

        if (flightRequest.getStartingPointDate().isAfter(flightRequest.getDestinationDate())
        ||flightRequest.getStartingPointDate().equals(flightRequest.getDestinationDate())){
            return Map.of("message","Wrong in date");
        }

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
         Optional<User> user = this.userRepository.findById(userId);

         if (user.isEmpty()){
             return Map.of("message","Sorry , you don't have any account in our system");
         }

         Optional<Airport> airport = this.airportRepo.findById(flightRequest.getAirportId());
         if (airport.isEmpty()){
             return Map.of("message","Sorry , This airport is not found in our system");
         }
         if (!userId.equals(airport.get().getUser().getId())){
             return Map.of("message","Wrong , you don't have this airport");
         }
         Flight flight = new Flight();
         flight.setStartingPoint(flightRequest.getStartingPoint());
         flight.setDestination(flightRequest.getDestination());
         flight.setStartingPointDate(flightRequest.getStartingPointDate());
         flight.setDestinationDate(flightRequest.getDestinationDate());
         flight.setNumberOfChairs(flightRequest.getNumberOfChairs());
         flight.setNumberOfEmptyChairs(flightRequest.getNumberOfChairs());
         flight.setStatus(StatusEnumForFlight.Incomplete);
         flight.setTripType(flightRequest.getTripType());
         flight.setAirport(airport.get());
         flight.setEnable(true);
         flight.setBasePrice(flightRequest.getBasePrice());
         flight.setCurrentPrice(flightRequest.getBasePrice());
         this.flightRepo.save(flight);
         return Map.of("message","Successfully added flight") ;
     }

    @Transactional
    public Map<String,String> updateFlightTime(FlightInfoUpdate infoUpdate)throws Exception{
        if (infoUpdate.getStartingPointDate().isAfter(infoUpdate.getDestinationDate())||
        infoUpdate.getDestinationDate().equals(infoUpdate.getStartingPointDate())
        ||infoUpdate.getStartingPointDate().isBefore(LocalDateTime.now())){
            return Map.of("message","Wrong in date");
        }


        Optional<Flight> flight = this.flightRepo.findById(infoUpdate.getFlightId());
        if (flight.isEmpty()){
            return Map.of("message","this flight is not found");
        }
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId,"admin");
        if (user.isEmpty()){
            return Map.of("message","");
        }

        if (!flight.get().getAirport().getUser().getId().equals(userId)){

            return Map.of("message","Wrong , you can't access to this flight");
        }
        Flight newFlight = flight.get();
        if (infoUpdate.getStartingPointDate()!=null&& infoUpdate.getDestinationDate()!=null){

            newFlight.setStartingPointDate(infoUpdate.getStartingPointDate());
            newFlight.setDestinationDate(infoUpdate.getDestinationDate());
        }
        if (infoUpdate.getDestination()!=null){
            newFlight.setDestination(infoUpdate.getDestination());
        }
        if (infoUpdate.getPrice()>0) {

            if (infoUpdate.getPrice() >= newFlight.getCurrentPrice()) {
                newFlight.setCurrentPrice(infoUpdate.getPrice());
            } else {
                newFlight.setCurrentPrice(infoUpdate.getPrice());
                // sentNotification
                List<User> users = this.userRepository.findAllByRoleName("user");
                for (User userInfo : users) {
                    String body = "Hello " + userInfo.getFullName() +
                            " \nExplore the offers with" + flight.get().getAirport().getName() + "Airport and book your ticket for a unique travel experience.\n" +
                            " Hurry to book the first seats.";
                    if (userInfo.getFcmToken() != null) {
                        this.firebaseService.sendNotification("SkyNest Application", body, userInfo.getFcmToken());
                        this.notificationRepo.save(new Notification("SkyNest Application\n" + body, userInfo));
                    }

                }

            }

        }

      return Map.of("message","successfully updated");
    }


    @Transactional
    public Map<String,String> updatePrice(Long flightId,double price) throws Exception {
        if (price <= 0) {
            return Map.of("message", "wrong , this price is not allow");
        }
        Optional<Flight> flight = this.flightRepo.findById(flightId);
        if (flight.isEmpty()) {
            return Map.of("message", "this flight is not found");
        }
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId, "admin");
        if (user.isEmpty()) {
            return Map.of("message", "");
        }

        if (!flight.get().getAirport().getUser().getId().equals(userId)) {

            return Map.of("message", "Wrong , you can't access to this flight");
        }
        Flight newFlight = flight.get();
        if (price >= newFlight.getCurrentPrice()) {
            newFlight.setCurrentPrice(price);
        } else {
            newFlight.setCurrentPrice(price);
            // sentNotification
            List<User> users = this.userRepository.findAllByRoleName("user");
            for (User userInfo : users) {
                String body = "Hello " + userInfo.getFullName() +
                        " \nExplore the offers with" + flight.get().getAirport().getName() + "Airport and book your ticket for a unique travel experience.\n" +
                        " Hurry to book the first seats.";
                if (userInfo.getFcmToken() != null) {
                    this.firebaseService.sendNotification("SkyNest Application", body, userInfo.getFcmToken());
                    this.notificationRepo.save(new Notification("SkyNest Application\n" + body, userInfo));
                }

            }
        }

        return Map.of("message","successfully updated price");
    }


     public List<FlightResponse> getAllYourFlight(Long airportId){
        Optional<Airport> airport = this.airportRepo.findById(airportId);
        if (airport.isEmpty()){
            return null;
        }
        List<Flight> flightList = this.flightRepo.findAllByAirportIdAndEnable(airportId,true);
        List<FlightResponse> flightResponsesList = new ArrayList<>();
        for (Flight flight : flightList) {
            flightResponsesList.add(getFlightResponse(flight,false));
        }

        return flightResponsesList;
     }


     public FlightResponse flightDetails(Long flightId){
        Optional<Flight> flightOptional = this.flightRepo.findById(flightId);
         return flightOptional.map(flight -> AFlightService.getFlightResponse(flight, false)).orElse(null);

     }

     public static FlightResponse getFlightResponse(Flight flight,boolean isAdmin){
         List<ImageDTO> flightImage = new ArrayList<>();
         FlightResponse flightResponse = new FlightResponse();
         flightResponse.setId(flight.getId());
         flightResponse.setNumberOfChairs(flight.getNumberOfChairs());
         flightResponse.setNumberOfEmptyChairs(flight.getNumberOfEmptyChairs());
         flightResponse.setStartingPoint(flight.getStartingPoint());
         flightResponse.setDestination(flight.getDestination());
         flightResponse.setStartingPointDate(flight.getStartingPointDate());
         flightResponse.setDestinationDate(flight.getDestinationDate());
         flightResponse.setBasePrice(flight.getBasePrice());
         flightResponse.setCurrentPrice(flight.getCurrentPrice());
         flightResponse.setAirportName(flight.getAirport().getName());
         if (StatusEnumForFlight.Complete.equals(flight.getStatus())){
             flightResponse.setStatus("Complete");
         }else{
             flightResponse.setStatus("InComplete");
         }
         if (TripTypeAndReservation.Deluxe.equals(flight.getTripType())){
             flightResponse.setTripType("Deluxe");
         }else{
             flightResponse.setTripType("Regular");
         }
         if (flight.isEnable()){
             flightResponse.setFlightStatus("Active");
         }else{
             flightResponse.setFlightStatus("Canceled");
         }
         for (FlightImage image : flight.getFlightImages()) {
             ImageDTO imageDTO = new ImageDTO();
             imageDTO.setId(image.getId());
             if (isAdmin) {
                 imageDTO.setImageUrl("http://localhost:8080/admin/flight/" + image.getName());
             }else {
                 imageDTO.setImageUrl("/user/flight/" + image.getName());

             }
             flightImage.add(imageDTO);
         }
         flightResponse.setFlightImage(flightImage);

         return flightResponse;
     }

    @Transactional
    public Map<String,String> deleteFlight(Long flightId) throws Exception {

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId,"admin");
         if (user.isEmpty()){
             return Map.of("message","Wrong , this account is not found in our system");
         }

         Optional<Flight> flightOptional = this.flightRepo.findById(flightId);
         if (flightOptional.isEmpty()){
             return Map.of("message","This flight is not found in our system");
         }
         Flight flight = flightOptional.get();
         if (!flight.isEnable()){

             return Map.of("message","Wrong , this flight is deleted");
         }
         if (!flight.getDestinationDate().isAfter(LocalDateTime.now())){

             return Map.of("message","This flight has already arrived");
         }
         List<FlightBooking> flightBookingList = this.flightBookingRepo.findByFlightIdAndStatus(flightId, StatusEnumForBooking.Activated);

        for (FlightBooking flightBooking : flightBookingList) {

            updateUserCard(flightBooking.getUser().getId(), flightBooking.getCurrentPrice());
            updateAirportCard(flightBooking.getFlight().getAirport().getId(), flightBooking.getCurrentPrice());
            flightBooking.setStatus(StatusEnumForBooking.Canceled);
            this.flightBookingRepo.save(flightBooking);

            String body = "Hello " + flightBooking.getUser().getFullName() + ".\n Your flight has been canceled at " + flightBooking.getFlight().getAirport().getName() + " Airport and your flight amount has been refunded due to issues that jeopardize the flight.";

            this.firebaseService.sendNotification("SkyNest", body, flightBooking.getUser().getFcmToken());

            this.notificationRepo.save(new Notification("SkyNest\n" + body, flightBooking.getUser()));
        }
        flight.setStatus(StatusEnumForFlight.Incomplete);
        flight.setEnable(false);
        this.flightRepo.save(flight);

        return Map.of("message","Successfully Deleted Flight");

    }

    public void updateUserCard(Long userId,double totalAmount){
        Optional<UserCard> userCard = this.userCardRepository.findByUserId(userId);
        if (userCard.isEmpty()){
            return;
        }
        UserCard updateUserCard  = userCard.get();
        updateUserCard.setTotalBalance(updateUserCard.getTotalBalance()+totalAmount);
        this.userCardRepository.save(updateUserCard);
    }


    public void updateAirportCard(Long airportId,double totalAmount){
        Optional<AirportCard> airportCard = this.airportCardRepo.findByAirportId(airportId);
        if (airportCard.isEmpty()){
            return;
        }
        AirportCard updateAirportCard  = airportCard.get();
        updateAirportCard.setTotalBalance(updateAirportCard.getTotalBalance()-totalAmount);
        this.airportCardRepo.save(updateAirportCard);

    }



}