package com.example.SkyNest.service.AdminService.AAirportService;


import com.example.SkyNest.model.entity.flight.*;
import com.example.SkyNest.model.entity.userDetails.User;

import com.example.SkyNest.model.repository.flight.AirportCardRepo;
import com.example.SkyNest.model.repository.flight.AirportImageRepo;
import com.example.SkyNest.model.repository.flight.AirportRepo;
import com.example.SkyNest.model.repository.flight.FlightBookingRepo;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.myEnum.StatusEnumForBooking;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AAirportService {

    @Value("${image.upload.airport}")
    private String airportImagePath;
    private final HttpServletRequest request;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final AirportCardRepo airportCardRepo;
    private final AirportRepo airportRepo;
    private final AirportImageRepo airportImageRepo;
    private final FlightBookingRepo flightBookingRepo;

    public AAirportService(
            HttpServletRequest request,
    JwtService jwtService,
    UserRepository userRepository,
    AirportCardRepo airportCardRepo,AirportRepo airportRepo,
    AirportImageRepo airportImageRepo,FlightBookingRepo flightBookingRepo
    ){

        this.userRepository = userRepository;
        this.request = request;
        this.jwtService = jwtService;
        this.airportRepo = airportRepo;
        this.airportCardRepo = airportCardRepo;
        this.airportImageRepo = airportImageRepo;
        this.flightBookingRepo = flightBookingRepo;
    }


    public Map<String,String> saveImageToAirport(Long airportId, MultipartFile image) throws Exception{
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
        Optional<Airport> airport = this.airportRepo.findById(airportId);
        if (airport.isEmpty()){
            return Map.of("message","Wrong , this airport is not found");
        }

        if (!airport.get().getUser().getId().equals(userId)){
            return Map.of("message","wrong , you can't access on this airport");
        }

        String contentType = image.getContentType();
        if (!(contentType.equals("image/jpeg")||contentType.equals("image/png"))){
            throw new IOException(" can you upload only JPG و PNG");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String imageName = UUID.randomUUID()+extension;

        Path folderPath = Paths.get(airportImagePath);
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(imageName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        AirportImage airportImage = new AirportImage();
        airportImage.setName(imageName);
        airportImage.setPath(folderPath +imageName);
        airportImage.setType(image.getContentType());
        airportImage.setAirport(airport.get());
        this.airportImageRepo.save(airportImage);
        return Map.of("message","Successfully Uploaded");
    }

    public Resource loadImage(String fileName) throws IOException {

        Path   filePath = Paths.get(airportImagePath).resolve(fileName);

        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("this image is not found"+fileName);
        }

        return new UrlResource(filePath.toUri());
    }

    public String getImageContentType(String fileName) throws IOException {

        Path  filePath = Paths.get(airportImagePath).resolve(fileName);

        return Files.probeContentType(filePath);
    }


    public double getTotalBalanced(Long airportId) {

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> user = this.userRepository.findByIdAndRoleName(userId, "admin");
        if (user.isEmpty()) {
            return -1D;
        }

        Optional<AirportCard> airportCard = this.airportCardRepo.findByAirportId(airportId);

        return airportCard.map(AirportCard::getTotalBalance).orElse(-1D);
    }


    public Long getAirportBookingsForAirport(Long airportId, int month, int year) {
        return flightBookingRepo.countBookingsForAirportInMonth(airportId, month, year, StatusEnumForBooking.Activated);
    }


}
