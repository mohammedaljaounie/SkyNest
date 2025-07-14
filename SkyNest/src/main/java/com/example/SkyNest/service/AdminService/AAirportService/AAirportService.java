package com.example.SkyNest.service.AdminService.AAirportService;

import com.example.SkyNest.dto.airportdto.FlightRequest;
import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.model.entity.flight.Flight;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.flight.AirportRepo;
import com.example.SkyNest.model.repository.flight.FlightRepo;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.StatusEnumForFlight;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.persistence.ManyToOne;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.Map;
import java.util.Optional;

@Service
public class AAirportService {

    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final FlightRepo flightRepo;
    private final AirportRepo airportRepo;
    private final UserRepository userRepository;
    public AAirportService(
            HttpServletRequest httpServletRequest , JwtService jwtService,
            FlightRepo flightRepo,AirportRepo airportRepo,
            UserRepository userRepository){

        this.request = httpServletRequest;
        this.jwtService = jwtService;
        this.flightRepo = flightRepo;
        this.airportRepo = airportRepo;
        this.userRepository = userRepository;

    }

    // create flight
     public Map<String,String> createFlight(FlightRequest flightRequest){
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
         flight.setStatus(StatusEnumForFlight.Incomplete);
         flight.setTripType(flightRequest.getTripType());
         flight.setAirport(airport.get());
         this.flightRepo.save(flight);
         return Map.of("message","Successfully added flight") ;
     }



    // update flight



    // show flight


    // delete flight



}