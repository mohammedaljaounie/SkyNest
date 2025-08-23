package com.example.SkyNest.service.UserService.UAirportService;

import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.EvaluationRequestInfo;
import com.example.SkyNest.dto.airportdto.FlightBookingDetails;
import com.example.SkyNest.model.entity.flight.*;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.entity.userDetails.UserCard;
import com.example.SkyNest.model.repository.flight.*;
import com.example.SkyNest.model.repository.userDetails.UserCardRepository;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.myEnum.StatusRole;
import com.example.SkyNest.service.SuperAdminService.SAAirportService.SAAirportService;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UAirportService {
    @Value("${image.upload.airport}")
    private String airportImagePath;

    private final AirportRepo airportRepo;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final FlightBookingRepo flightBookingRepo;
    private final FlightRepo flightRepo;
    private final UserCardRepository userCardRepository;
    private final AirportCardRepo airportCardRepo;
    private final AirportRatingRepo airportRatingRepo;

    public UAirportService(
            AirportRepo airportRepo,
            HttpServletRequest request,
            JwtService jwtService,
            UserRepository userRepository,
            FlightBookingRepo flightBookingRepo,
            FlightRepo flightRepo,
            UserCardRepository userCardRepository,
            AirportCardRepo airportCardRepo,
            AirportRatingRepo airportRatingRepo
    ) {

        this.airportRepo = airportRepo;
        this.request = request;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.flightBookingRepo = flightBookingRepo;
        this.flightRepo = flightRepo;
        this.userCardRepository = userCardRepository;
        this.airportCardRepo = airportCardRepo;
        this.airportRatingRepo = airportRatingRepo;
    }


    public List<AirportResponse> getNearAirport() {

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findById(userId);
        if (user.isEmpty()) {
            return null;
        }

        double latitude = user.get().getLatitude();
        double longitude = user.get().getLongitude();
        List<Airport> airportList = this.airportRepo.findNearAirport(latitude, longitude, 100D, 20);
        List<AirportResponse> airportResponseList = new ArrayList<>();
        for (Airport airport : airportList) {
            airportResponseList.add(SAAirportService.getAirportResponse(airport, StatusRole.USER));
        }

        return airportResponseList;
    }

    public List<AirportResponse> getAirportOrderByRating() {
        List<Airport> airportList = this.airportRepo.getAirportOrderByRating();
        List<AirportResponse> airportResponseList = new ArrayList<>();

        for (Airport airport : airportList) {

            airportResponseList.add(SAAirportService.getAirportResponse(airport, StatusRole.USER));

        }

        return airportResponseList;
    }

    public List<AirportResponse> searchByName(String airportName) {
        List<Airport> airportList = this.airportRepo.findByName(airportName);

        List<AirportResponse> airportResponseList = new ArrayList<>();

        for (Airport airport : airportList){

            airportResponseList.add(SAAirportService.getAirportResponse(airport,StatusRole.USER));
        }

        return airportResponseList;
    }

    @Transactional
    public Map<String, String> flightBookingInAirport(FlightBookingDetails flightDetails) {

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId, "user");

        if (user.isEmpty()) {
            return Map.of("message", "Wrong this user is not found in our system");
        }

        Optional<UserCard> userCard = this.userCardRepository.findByUserId(userId);
        if (userCard.isEmpty()) {
            return Map.of("message", "We apologize, but you cannot complete the transaction because you do not have a bank account or other payment method to complete the reservation.");
        }


        Optional<Flight> flight = this.flightRepo.findById(flightDetails.getFlightId());
        if (flight.isEmpty()) {
            return Map.of("message", "Wrong , this flight is not found in this system");
        }
        Flight updateFlight = flight.get();

        Optional<AirportCard> airportCard = this.airportCardRepo.findByAirportId(updateFlight.getAirport().getId());

        if (airportCard.isEmpty()) {
            return Map.of("message", "We apologize, but you cannot complete the transaction. This airport does not have a bank account to which you can transfer funds. Payment and reservations must be made at the nearest office. Thank you.");
        }


        LocalDateTime nowPlus10 = LocalDateTime.now().plusHours(10);
        if (updateFlight.getStartingPointDate().isBefore(nowPlus10)) {
            return Map.of("message", "Sorry, you cannot book this flight because the flight schedule has expired. Book the following flights :-(");
        }

        int numberOfEmptyChairs = updateFlight.getNumberOfEmptyChairs();
        if (!(numberOfEmptyChairs >= flightDetails.getNumberOfPerson()) || updateFlight.getStatus() == StatusEnumForFlight.Complete) {
            return Map.of("message", "Dear user, we apologize for not being able to fulfill your reservation request because the number of available seats is not sufficient to meet your reservation information.");
        }

        double baseCost = numberOfEmptyChairs * flight.get().getCurrentPrice();
        int userLevel = user.get().getLevel()/5;
        double currentCost = 0;
        if (userLevel>0){
            currentCost = baseCost-baseCost*2*userLevel/100;
        }else {
            currentCost = baseCost;
        }
        double totalUserBalance = userCard.get().getTotalBalance();

        if (totalUserBalance<currentCost){
            saveBookingFlight(updateFlight.getAirport(),updateFlight, user.get(), baseCost, currentCost,flightDetails.getNumberOfPerson(),false);

            return Map.of("message","Error: You cannot complete the reservation because you do not have enough money. Charge your card and try again.");
        }
        updateUserCard(userCard.get(), baseCost,true);
        updateAirportCard(airportCard.get(), baseCost,true);
        updateFlight.setNumberOfEmptyChairs(updateFlight.getNumberOfEmptyChairs()-flightDetails.getNumberOfPerson());
        if (updateFlight.getNumberOfEmptyChairs() == 0) {
            updateFlight.setStatus(StatusEnumForFlight.Complete);
        }
        this.flightRepo.save(updateFlight);
        saveBookingFlight(updateFlight.getAirport(),updateFlight, user.get(), baseCost, currentCost,flightDetails.getNumberOfPerson(),true);

        return Map.of("message", "Your flight reservation has been completed successfully We wish you a pleasant journey.");
    }

    private void updateUserCard(UserCard userCard, double totalCost,boolean isBooking) {
        if (isBooking){
            userCard.setTotalBalance(userCard.getTotalBalance() - totalCost);
        }else{
            userCard.setTotalBalance(userCard.getTotalBalance() + totalCost);
        }

        this.userCardRepository.save(userCard);
    }

    private void updateAirportCard(AirportCard airportCard, double totalCost,boolean isBooking) {
        if (isBooking){
            airportCard.setTotalBalance(airportCard.getTotalBalance() + totalCost);

        }else {
            airportCard.setTotalBalance(airportCard.getTotalBalance() - totalCost);

        }
        this.airportCardRepo.save(airportCard);
    }

    private void saveBookingFlight(Airport airport,Flight flight, User user, double totalCost,double currentCost, int numberOfPersons,boolean isAcceptable) {

        FlightBooking flightBooking = new FlightBooking();
        flightBooking.setStartingPoint(flight.getStartingPoint());
        flightBooking.setDestination(flight.getDestination());
        flightBooking.setStartingPointDate(flight.getStartingPointDate());
        flightBooking.setDestinationDate(flight.getDestinationDate());
        flightBooking.setNumberOfPerson(numberOfPersons);
        flightBooking.setBasePrice(totalCost);
        flightBooking.setCurrentPrice(currentCost);
        flightBooking.setTripType(flight.getTripType());
        if (isAcceptable){
            flightBooking.setStatus(StatusEnumForBooking.Activated);
        }else {
            flightBooking.setStatus(StatusEnumForBooking.Unacceptable);
        }

        flightBooking.setUser(user);
        flightBooking.setFlight(flight);
        flightBooking.setAirport(airport);
        this.flightBookingRepo.save(flightBooking);
    }


    @Transactional
    public Map<String, String> cancelFlightBooking(Long flightBookingId) {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> user = this.userRepository.findByIdAndRoleName(userId, "user");
        if (user.isEmpty()) {
            return Map.of("message", "Wrong , this account is not found in our system");
        }

        Optional<FlightBooking> flightBooking = this.flightBookingRepo.findByIdAndUserIdAndStatus(flightBookingId, userId, StatusEnumForBooking.Activated);

        if (flightBooking.isEmpty()) {

            return Map.of("message", "Wrong , you don't have flight booking like this details");
        }

        LocalDateTime nowPlus7 = LocalDateTime.now().plusDays(7);
        if (flightBooking.get().getStartingPointDate().isBefore(nowPlus7)) {
            return Map.of("message", "Sorry, you cannot cancel your flight reservation now. Cancellations must be made within 7 days of your flight.");
        }

        double tax = flightBooking.get().getCurrentPrice()*25/100;
        Optional<UserCard> userCard = this.userCardRepository.findByUserId(flightBooking.get().getUser().getId());
        if (userCard.isEmpty()){
            return Map.of("message","Sorry, we do not have a bank account to which the funds for cancelling the reservation can be transferred.");
        }

        Optional<AirportCard> airportCard = this.airportCardRepo.findByAirportId(flightBooking.get().getFlight().getAirport().getId());
        if (airportCard.isEmpty()) {
            return Map.of("message", "Sorry, the hotel does not have a bank account from which cancellation fees can be transferred.\n");
        }

        updateUserCard(userCard.get(),tax,false);
        updateAirportCard(airportCard.get(),tax,false);

       FlightBooking booking = flightBooking.get();
       booking.setStatus(StatusEnumForBooking.Canceled);
        this.flightBookingRepo.save(booking);

        return Map.of("message","The flight cancellation process was successfully completed and the 75% ticket cancellation tax was deducted.");
    }

    @Transactional
    public Map<String,String> airportEvaluation(EvaluationRequestInfo evaluationInfo){
        if (evaluationInfo.getStars()<=0||evaluationInfo.getStars()>5){
            return Map.of("message","The percentage of incorrect evaluation should be in the range of 1 to 5.");
        }

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> user = this.userRepository.findByIdAndRoleName(userId,"user");
        if (user.isEmpty()){
            return Map.of("message","Wrong , this account is not found in our system");
        }

        Optional<Airport> airport = this.airportRepo.findById(evaluationInfo.getAirportId());
        if (airport.isEmpty()){
            return Map.of("message","Wrong , this airport is not found in our system");
        }
        if (!isReservation(airport.get().getId(),userId)){
            return Map.of("message","Sorry, you can't rate the airport at this time. You can do so after experiencing a flight there.");
        }

        Optional<AirportRating> airportRatingOptional = this.airportRatingRepo.findByUserId(userId);
        if (airportRatingOptional.isEmpty()){
            AirportRating  airportRating = new AirportRating();
            airportRating.setRating(evaluationInfo.getStars());
            airportRating.setComment(evaluationInfo.getComment());
            airportRating.setUser(user.get());
            airportRating.setAirport(airport.get());
            this.airportRatingRepo.save(airportRating);
             int rating = this.airportRatingRepo.sumRating(evaluationInfo.getAirportId());
             updateAirportRatingInfo(airport.get(),rating,true);
        }else {

            AirportRating airportRating = airportRatingOptional.get();
            airportRating.setRating(evaluationInfo.getStars());
            if (evaluationInfo.getComment()!=null){
                airportRating.setComment(evaluationInfo.getComment());
            }
            this.airportRatingRepo.save(airportRating);
            int rating = this.airportRatingRepo.sumRating(evaluationInfo.getAirportId());
            updateAirportRatingInfo(airport.get(),rating,false);

        }

        return Map.of("message","Your rating has been successfully completed.\nThank you for your feedback on this airport.");
    }

    private boolean isReservation(Long airportId, Long userId) {

        List<FlightBooking> flightBookingList = this.flightBookingRepo.findByAirportIdAndUserIdAndStatus(airportId,userId,StatusEnumForBooking.Activated);

        if (flightBookingList.isEmpty()){
            return false;
        }

        for (FlightBooking flightBooking : flightBookingList){

            if (!flightBooking.getStartingPointDate().isAfter(LocalDateTime.now())){
                return true;
            }
        }

        return false;
    }

    private void updateAirportRatingInfo(Airport airport,int rating,boolean isNew){

        int ratingCount = airport.getRatingCount();

        if (isNew){
            airport.setRatingCount(ratingCount+1);
            airport.setAvgRating((double) rating /(ratingCount+1));
        }else {
            airport.setAvgRating((double) rating /(ratingCount));
        }
        this.airportRepo.save(airport);

    }

    public Resource loadImage(String fileName) throws IOException {

        Path filePath = Paths.get(airportImagePath).resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {

        Path  filePath = Paths.get(airportImagePath).resolve(fileName);

        return Files.probeContentType(filePath);
    }

}
