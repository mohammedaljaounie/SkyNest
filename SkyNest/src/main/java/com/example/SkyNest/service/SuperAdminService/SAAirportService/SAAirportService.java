package com.example.SkyNest.service.SuperAdminService.SAAirportService;

import com.example.SkyNest.dto.airportdto.AirportRequest;
import com.example.SkyNest.dto.airportdto.AirportResponse;
import com.example.SkyNest.dto.airportdto.AirportUpdateRequest;
import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.model.entity.flight.Airport;
import com.example.SkyNest.model.entity.flight.AirportCard;
import com.example.SkyNest.model.entity.flight.AirportImage;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.flight.*;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.StatusRole;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SAAirportService {

    @Value("${image.upload.flight}")
    private  String flightImagePath;

    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AirportRepo airportRepo ;
    private final AirportCardRepo airportCardRepo;
    private final AirportImageRepo airportImageRepo;
    private final FlightRepo flightRepo;
    private AirportRatingRepo airportRatingRepo;
    private FlightBookingRepo flightBookingRepo;

    public SAAirportService( HttpServletRequest request,
                            JwtService jwtService,
                            UserRepository userRepository,
                            AirportRepo airportRepo, AirportCardRepo airportCardRepo
    ,AirportImageRepo airportImageRepo,FlightRepo flightRepo,AirportRatingRepo airportRatingRepo
                             ,FlightBookingRepo flightBookingRepo
                             ){
        this.request = request;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.airportRepo =airportRepo;
        this.airportCardRepo = airportCardRepo;
        this.airportImageRepo  = airportImageRepo;
        this.flightRepo = flightRepo;
        this.airportRatingRepo = airportRatingRepo;
        this.flightBookingRepo = flightBookingRepo;
    }

    // create airport
    public Map<String,String> createAirport(AirportRequest airportRequest){
        Optional<User> user = this.userRepository.findByEmailAndRoleName(airportRequest.getEmail(),"admin");
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
        return airport.map(value -> SAAirportService.getAirportResponse(value, StatusRole.USER)).orElse(null);
    }

    // show all airport
    public  List<AirportResponse> airportsInfo(){

        List<Airport> airportList = this.airportRepo.findAll();

        List<AirportResponse> airportResponseList = new ArrayList<>();

        for (Airport airport : airportList){
            airportResponseList.add(SAAirportService.getAirportResponse(airport,StatusRole.USER));
        }
        return airportResponseList;
    }

    public static AirportResponse getAirportResponse(Airport airport, StatusRole role){

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
        List<ImageDTO> imageDTOList = new ArrayList<>();
        for (AirportImage airportImage : airport.getAirportImages()){
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(airportImage.getId());
            if (role.equals(StatusRole.SUPER_ADMIN)){
                imageDTO.setImageUrl("http://localhost:8080/super_admin/airport/"+airportImage.getName());

            } else if (role.equals(StatusRole.ADMIN)) {
                imageDTO.setImageUrl("http://localhost:8080/admin/airport/"+airportImage.getName());

            }else{
                imageDTO.setImageUrl("/user/airport/"+airportImage.getName());

            }
            imageDTOList.add(imageDTO);
        }
            airportResponse.setAirportImages(imageDTOList);
        return airportResponse;
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


    public Map<String,String> deleteAirport(Long airportId){
        Optional<Airport> airport = this.airportRepo.findById(airportId);
        System.out.println("df");
        if (airport.isEmpty()){
            return Map.of("message","Wrong , this airport is not found in our system");
        }

        this.airportImageRepo.deleteAllByAirportId(airportId);
        try {
            this.airportCardRepo.deleteByAirportId(airportId);
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of("message", "Error deleting airportCard: " + e.getMessage());
        }

        this.airportRatingRepo.deleteAllByAirportId(airportId);
        this.flightBookingRepo.deleteAllByAirportId(airportId);
        this.flightRepo.deleteAllByAirportId(airportId);
        this.airportRepo.delete(airport.get());
        return Map.of("message","successfully deleted");
    }



}
