package com.example.SkyNest.service.UserService.UAirportService;

import com.example.SkyNest.dto.airportdto.FilterFlightInfo;
import com.example.SkyNest.dto.airportdto.FlightBookingResponse;
import com.example.SkyNest.dto.airportdto.FlightResponse;
import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.model.entity.flight.Flight;
import com.example.SkyNest.model.entity.flight.FlightBooking;
import com.example.SkyNest.model.repository.flight.AirportRepo;
import com.example.SkyNest.model.repository.flight.FlightBookingRepo;
import com.example.SkyNest.model.repository.flight.FlightRepo;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.myEnum.TripTypeAndReservation;
import com.example.SkyNest.service.AdminService.AAirportService.AFlightService;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class UFlightService {
    @Value("${image.upload.flight}")
   private String flightImagePath;

    private final FlightRepo flightRepo;
    private final AirportRepo airportRepo;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final FlightBookingRepo flightBookingRepo;

    public UFlightService(
            FlightRepo flightRepo,
            AirportRepo airportRepo,
            HttpServletRequest request,
            JwtService jwtService,
            FlightBookingRepo flightBookingRepo
    ){

        this.flightRepo = flightRepo;
        this.airportRepo = airportRepo;
        this.request = request;
        this.jwtService = jwtService;
        this.flightBookingRepo  = flightBookingRepo;
    }





    public Resource loadImage(String fileName) throws IOException {

        Path filePath = Paths.get(flightImagePath).resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {

        Path  filePath = Paths.get(flightImagePath).resolve(fileName);

        return Files.probeContentType(filePath);
    }


    public  List<FlightBookingResponse> viewActiveFlight(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        List<FlightBooking> flightBookingList = this.flightBookingRepo.findByUserIdAndStatus(userId, StatusEnumForBooking.Activated);
        List<FlightBookingResponse> flightBookingResponseList = new ArrayList<>();
        for (FlightBooking flightBooking : flightBookingList){
           flightBookingResponseList.add(UFlightService.getFlightBookingResponse(flightBooking));
        }
        return flightBookingResponseList;
    }

    public List<FlightBookingResponse> viewCanceledFlight(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        List<FlightBooking> flightBookingList = this.flightBookingRepo.findByUserIdAndStatus(userId, StatusEnumForBooking.Canceled);
        List<FlightBookingResponse> flightBookingResponseList = new ArrayList<>();
        for (FlightBooking flightBooking : flightBookingList){
            flightBookingResponseList.add(UFlightService.getFlightBookingResponse(flightBooking));
        }
        return flightBookingResponseList;
    }

    public List<FlightBookingResponse> viewIncorrectFlight(){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        List<FlightBooking> flightBookingList = this.flightBookingRepo.findByUserIdAndStatus(userId, StatusEnumForBooking.Unacceptable);
        List<FlightBookingResponse> flightBookingResponseList = new ArrayList<>();
        for (FlightBooking flightBooking : flightBookingList){
           flightBookingResponseList.add(UFlightService.getFlightBookingResponse(flightBooking));
        }
        return flightBookingResponseList;
    }

    public List<FlightResponse> searchFlightByStartPointAndDestinationInAllAirport(String startPoint,String destination){

        LocalDateTime threshold = LocalDateTime.now().plusHours(10);
        System.out.println("time "+threshold);
        List<Flight> flightList = this.flightRepo.findAvailableFlightsForStartAndEndPointInAll(startPoint, destination,StatusEnumForFlight.Incomplete,threshold,true);
        System.out.println("flight size : "+flightList.size());
        List<FlightResponse>flightResponseList = new ArrayList<>();
        for (Flight flight : flightList){
            flightResponseList.add(AFlightService.getFlightResponse(flight,false));
        }

         return flightResponseList;
    }

    public List<FlightResponse> searchFlightByStartPointAndDestinationInCertainAirport(Long airportId,String startPoint,String destination){
        LocalDateTime threshold = LocalDateTime.now().plusHours(10);
        List<Flight> flightList = this.flightRepo.findAvailableFlightsForStartAndEndPointInCertainAirport(airportId,startPoint, destination,StatusEnumForFlight.Incomplete,threshold,true);
        List<FlightResponse>flightResponseList = new ArrayList<>();
        for (Flight flight : flightList){
           flightResponseList.add(AFlightService.getFlightResponse(flight,false));
        }

        return flightResponseList;
    }

    public List<FlightResponse> searchAvailableFlightsForDateAndLocation(FilterFlightInfo info){

        if (info.getUserDate().isBefore(LocalDate.now())){
            return Collections.emptyList();
        }
        LocalDateTime startDate  = info.getUserDate().atStartOfDay();
        LocalDateTime endDate = info.getUserDate().atTime(LocalTime.MAX);
        LocalDateTime nowPlus10 = LocalDateTime.now().plusHours(10);
        List<Flight> flightList = new ArrayList<>();
        if (info.getUserDate().isEqual(LocalDate.now())){
           flightList = this.flightRepo.findAvailableFlightsForDateAndLocation(startDate, endDate,nowPlus10, info.getStartPoint(), info.getDestination(),StatusEnumForFlight.Incomplete,true);
        }
        else{

            flightList = this.flightRepo.findAvailableFlightsForFutureDateAndLocation(startDate,endDate,info.getStartPoint(),info.getDestination(),StatusEnumForFlight.Incomplete,true);
        }
        List<FlightResponse>flightResponseList = new ArrayList<>();
        for (Flight flight : flightList){
            flightResponseList.add(AFlightService.getFlightResponse(flight,false));
        }

        return flightResponseList;
    }



    private static FlightBookingResponse getFlightBookingResponse(FlightBooking flightBooking){

        FlightBookingResponse flightBookingResponse = new FlightBookingResponse();
        flightBookingResponse.setId(flightBooking.getId());
        flightBookingResponse.setStartingPoint(flightBooking.getStartingPoint());
        flightBookingResponse.setDestination(flightBooking.getDestination());
        flightBookingResponse.setStartingPointDate(flightBooking.getStartingPointDate());
        flightBookingResponse.setDestinationDate(flightBooking.getDestinationDate());
        if (StatusEnumForBooking.Activated.equals(flightBooking.getStatus())){
            flightBookingResponse.setStatus("Activated");
        } else if (StatusEnumForBooking.Canceled.equals(flightBooking.getStatus())) {
            flightBookingResponse.setStatus("Canceled");
        }else {
            flightBookingResponse.setStatus("Unacceptable");
        }
        flightBookingResponse.setNumberOfChairs(flightBooking.getNumberOfPerson());
        if (TripTypeAndReservation.Deluxe.equals(flightBooking.getTripType())){
            flightBookingResponse.setTripType("Deluxe");
        }else {
            flightBookingResponse.setTripType("Regular");
        }
        flightBookingResponse.setBasePrice(flightBooking.getBasePrice());
        flightBookingResponse.setCurrentPrice(flightBooking.getCurrentPrice());
        flightBookingResponse.setUserName(flightBooking.getUser().getFullName());
        flightBookingResponse.setAirportName(flightBooking.getFlight().getAirport().getName());

        return flightBookingResponse;
    }














}
