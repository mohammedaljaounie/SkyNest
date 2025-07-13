package com.example.SkyNest.service.SuperAdminService.SAAirportService;

import com.example.SkyNest.dto.airportdto.AirportRequest;
import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.AirportUpdateRequest;
import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.model.entity.flight.AirportCard;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.flight.AirportCardRepo;
import com.example.SkyNest.model.repository.flight.AirportRepo;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SAAirportService {

    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AirportRepo airportRepo ;
    private final AirportCardRepo airportCardRepo;

    public SAAirportService(HttpServletRequest request,
                            JwtService jwtService,
                            UserRepository userRepository,
    AirportRepo airportRepo,AirportCardRepo airportCardRepo){
        this.request = request;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.airportRepo =airportRepo;
        this.airportCardRepo = airportCardRepo;
    }

    // create airport
    public Map<String,String> createAirport(AirportRequest airportRequest){
        String jwt  = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId  = jwtService.extractId(token);
        if (!userId.equals(airportRequest.getAdminId())){
            return Map.of("message","Sorry , Not Match between user that send request and admin user");
        }
        Optional<User> user = this.userRepository.findByIdAndRoleName(airportRequest.getAdminId(),"admin");
        if (user.isEmpty()){
            return Map.of("message","Sorry , you don't have account in our system");
        }
          Airport airport =
          this.airportRepo.save(new Airport(airportRequest.getName(),
           airportRequest.getDescription(),0,0,
           airportRequest.getLatitude(),airportRequest.getLongitude(),
          airportRequest.getLocation(),user.get()));

        this.airportCardRepo.save(new AirportCard(0,airport));
        return Map.of("message","Successfully Created");
    }


    // update airport
    public Map<String,String> updateAirportInfo(AirportUpdateRequest request){


        if (request.getAirportId()==null) {
        return Map.of("message","Wrong , this id for airport is empty,we can't make any process");
        }

            Optional<Airport> airport = this.airportRepo.findById(request.getAirportId());
        if (airport.isEmpty()){
            return Map.of("message","Sorry , this airport is not found in our system");
        }
        Airport updateAirport  = airport.get();
        if (request.getName()!=null){
            updateAirport.setName(request.getName());
        }
        if (request.getDescription()!=null){
            updateAirport.setDescription(request.getDescription());
        }
        this.airportRepo.save(updateAirport);
        return Map.of("message","Successfully Updated");
    }


    // show certain airport

    public AirportResponse airportInfo(Long airportId) {

        Optional<Airport> airport = this.airportRepo.findById(airportId);
        return airport.map(SAAirportService::getAirportResponse).orElse(null);

    }

    // show all airport
    public List<AirportResponse> airportsInfo(){

        List<Airport> airportList = this.airportRepo.findAll();

        List<AirportResponse> airportResponseList = new ArrayList<>();

        for (Airport airport : airportList){
            airportResponseList.add(SAAirportService.getAirportResponse(airport));
        }
        return airportResponseList;
    }



    private static AirportResponse getAirportResponse(Airport airport){

        AirportResponse airportResponse = new AirportResponse();
        airportResponse.setAirportId(airport.getId());
        airportResponse.setName(airport.getName());
        airportResponse.setDescription(airport.getDescription());
        airportResponse.setLatitude(airport.getLatitude());
        airportResponse.setLongitude(airport.getLongitude());
        airportResponse.setLocation(airport.getLocation());
        airportResponse.setAvgRating(airport.getAvgRating());
        airportResponse.setRatingCount(airport.getRatingCount());
        airportResponse.setOwnerName(airport.getUser().getFullName());

        return airportResponse;
    }


    // delete airport

}
