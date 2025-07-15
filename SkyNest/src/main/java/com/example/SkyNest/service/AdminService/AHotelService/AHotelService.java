package com.example.SkyNest.service.AdminService.AHotelService;

import com.example.SkyNest.dto.hoteldto.HotelResponse;
import com.example.SkyNest.dto.hoteldto.ImageDTO;
import com.example.SkyNest.dto.hoteldto.PlaceNearHotelResponse;
import com.example.SkyNest.dto.hoteldto.PlaceNearTheHotelRequest;
import com.example.SkyNest.model.entity.hotel.*;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.hotel.*;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.*;


@Service
public class AHotelService {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelImageRepository hotelImageRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HotelCardRepository  hotelCardRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceNearHotelRepo placeNearHotelRepo;
    @Autowired
    private PlaceNearHotelImageRepo placeNearHotelImageRepo;

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Value("${image.upload.place.near.hotel}")
    private String uploadImagePlace;


    public List<HotelResponse> showHotelInfo(){

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long id = jwtService.extractId(token);
        List<Hotel> hotelOpt = this.hotelRepository.findByUserId(id);
        if (hotelOpt.isEmpty()){
            return null;
        }
        List<HotelResponse> hotelResponseList = new ArrayList<>();
        for (Hotel hotel : hotelOpt) {
            HotelResponse hotelResponse = new HotelResponse();
            hotelResponse.setId(hotel.getId());
            hotelResponse.setName(hotel.getName());
            hotelResponse.setAddress(hotel.getAddress());
            hotelResponse.setDescription(hotel.getDescription());
            hotelResponse.setRatingCount(hotel.getRatingCount());
            hotelResponse.setAvgRating(hotel.getAvgRating());

            List<HotelImage> imageList = hotel.getHotelImageList();

            List<ImageDTO> imageResponseList = new ArrayList<>();
            for (int i = 0; i < imageList.size(); i++) {
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(imageList.get(i).getId());
                imageDTO.setImageUrl("http://localhost:8080/admin/hotel/" + imageList.get(i).getName());
                imageResponseList.add(imageDTO);
            }
            hotelResponse.setImageDTOList(imageResponseList);
            hotelResponseList.add(hotelResponse);
        }
        return hotelResponseList;

    }

//    public Map<String,String> uploadImage(Long hotelId,MultipartFile image) throws IOException {
//        Optional<Hotel> hotelOpt = this.hotelRepository.findById(hotelId);
//        if (hotelOpt.isEmpty()){
//            return Map.of("message","This hotel is not found in our application");
//        }
//        String uniqueImageName = UUID.randomUUID()+"_"+image.getOriginalFilename();
//        String imagePath = uploadDir+image.getOriginalFilename();
//
//        HotelImage hotelImage = new HotelImage();
//        hotelImage.setName(uniqueImageName);
//        hotelImage.setPath(imagePath);
//        hotelImage.setType(image.getContentType());
//        hotelImage.setHotel(hotelOpt.get());
//        this.hotelImageRepository.save(hotelImage);
//        image.transferTo(new File(imagePath).toPath());
//        return Map.of("message","Successfully Upload");
//
//    }

    public Map<String,String> saveImage(Long hotelId,MultipartFile file) throws IOException {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message","your not found in our system");
        }
        if (file.isEmpty()) {
            throw new IOException("this photo is empty");
        }

        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(hotelId,userId);
        if (hotelOptional.isEmpty()){
            return Map.of("message","you are not have hotel same like this details");
        }

        String contentType = file.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IOException(" can you upload only JPG و PNG");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String fileName = UUID.randomUUID() + extension;

        Path folderPath = Paths.get(uploadDir);
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(fileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        HotelImage meta = new HotelImage();
        meta.setName(fileName);
        meta.setPath(folderPath.toString()+fileName);
        meta.setType(file.getContentType());
        meta.setHotel(hotelOptional.get());
        this.hotelImageRepository.save(meta);
        return Map.of("message","successfully uploaded");
    }

    public Resource loadImage(String fileName,boolean isHotel) throws IOException {
        Path filePath =null ;
        if (isHotel) {
            filePath = Paths.get(uploadDir).resolve(fileName);
       }else {
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

    public Double getTotalBalanceForHotel(Long hotelId){
        String jwt  = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<Hotel> optionalHotel = this.hotelRepository.findByIdAndUserId(hotelId,userId);
        if (optionalHotel.isEmpty())
           return -1D;
        if (!Objects.equals(optionalHotel.get().getId(), hotelId))
           return -1D;

        Optional<HotelCard> hotelCard = this.hotelCardRepository.findByHotelId(hotelId);
        return hotelCard.map(HotelCard::getTotalBalance).orElse(-1D);

    }

    public  Map<String ,String> createPlaceNearHotel(PlaceNearTheHotelRequest nearTheHotelRequest) {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            // message to explain that this user is not found in our system
            return Map.of("message",
                    "Sorry , this account is not found in our system");
        }
        Optional<Hotel> hotelOptional = this.hotelRepository.findByIdAndUserId(nearTheHotelRequest.getHotelId(), userId);
        if (hotelOptional.isEmpty()){
            // message to explain that this user isn't have this hotel
            return Map.of("message",
                    "Sorry , you don't have hotel like this input info");
        }
        PlaceNearTheHotel placeNearTheHotel  = new PlaceNearTheHotel();
        placeNearTheHotel.setName(nearTheHotelRequest.getPlaceName());
        placeNearTheHotel.setDescription(nearTheHotelRequest.getDescription());
        placeNearTheHotel.setAddress(hotelOptional.get().getAddress());
        placeNearTheHotel.setHotel(hotelOptional.get());
        this.placeNearHotelRepo.save(placeNearTheHotel);

        return  Map.of("message",
                 "Successfully added this place to your hotel");
    }

    @Transactional
    public List<PlaceNearHotelResponse> showAllPlaceNearHotel(Long hotelId,boolean isAdmin){
        String  jwt  = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> userOptional  = this.userRepository.findById(userId);

        if (userOptional.isEmpty()){
            return null;
        }

        Optional<Hotel> hotelOptional = this.hotelRepository.findById(hotelId);
        if (hotelOptional.isEmpty()){
            return null;
        }

        List<PlaceNearTheHotel> places = hotelOptional.get().getPlaceNearTheHotelList();
        List<PlaceNearHotelResponse> placeNearTheHotelList = new ArrayList<>();

        for (PlaceNearTheHotel place : places){
            PlaceNearHotelResponse placeNearHotelResponse = new PlaceNearHotelResponse();

            placeNearHotelResponse.setPlaceId(place.getId());
            placeNearHotelResponse.setPlaceName(place.getName());
            placeNearHotelResponse.setPlaceDescription(place.getDescription());

            List<ImageDTO> imageDTOList = new ArrayList<>();
            for (PlaceNearTheHotelImage imagePlace : place.getPlaceNearTheHotelImageList()){
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setId(imagePlace.getId());
                if (isAdmin) {
                    imageDTO.setImageUrl("http://localhost:8080/admin/hotel/placeImage/" + imagePlace.getName());
                }
                else {
                    imageDTO.setImageUrl("/user/hotel/placeImage/" + imagePlace.getName());

                }
                imageDTOList.add(imageDTO);
            }
            placeNearHotelResponse.setImagePlaceList(imageDTOList);
            placeNearTheHotelList.add(placeNearHotelResponse);
        }

        return placeNearTheHotelList;
    }


    @Transactional
    public Map<String,String> uploadImageToPlace(Long placeId,MultipartFile image) throws IOException {
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);

        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message","your not found in our system");
        }

        if (image.isEmpty()){
            return Map.of("message","wrong , this image is empty");
        }
        Optional<PlaceNearTheHotel> placeNearTheHotelOptional = this.placeNearHotelRepo.findById(placeId);

        if (placeNearTheHotelOptional.isEmpty()){
            return Map.of("message","this place is not found around your hotel");
        }
        if (!placeNearTheHotelOptional.get().getHotel().getUser().getId().equals(userId)){

            return Map.of("message","you can access on this place because this hotel is not your hotel");
        }

        String contentType = image.getContentType();
        if (!("image/jpeg".equals(contentType) || "image/png".equals(contentType))) {
            throw new IOException(" can you upload only JPG و PNG");
        }

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String fileName = UUID.randomUUID() + extension;

        Path folderPath = Paths.get(uploadImagePlace);
        Files.createDirectories(folderPath);

        Path filePath = folderPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        PlaceNearTheHotelImage placeNearTheHotelImage = new PlaceNearTheHotelImage();
        placeNearTheHotelImage.setName(fileName);
        placeNearTheHotelImage.setPath(folderPath.toString()+fileName);
        placeNearTheHotelImage.setType(contentType);
        placeNearTheHotelImage.setPlaceNearTheHotel(placeNearTheHotelOptional.get());
        this.placeNearHotelImageRepo.save(placeNearTheHotelImage);

        return Map.of("message","Successfully Upload");

    }

    @Transactional
    public Map<String,String> updatePlaceInformation(Long placeId,String placeName,String placeDescription){
        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message",
                    "Sorry, this account isn't found in our system");
        }
        Optional<PlaceNearTheHotel> placeNearTheHotel = this.placeNearHotelRepo.findById(placeId);

       if (placeNearTheHotel.isEmpty()){
           return Map.of("message",
                   "Sorry , this place is not found in our system");
       }
       if (!placeNearTheHotel.get().getHotel().getUser().getId().equals(userId)){
           return Map.of("message",
                   "Sorry , can't update this place because you don't have  authorization on this place");
       }

       PlaceNearTheHotel updatePlaceNearTheHotel = placeNearTheHotel.get();
       if (placeName!=null){
           updatePlaceNearTheHotel.setName(placeName);
       }
       if (placeDescription!=null){
           updatePlaceNearTheHotel.setDescription(placeDescription);
       }
       this.placeNearHotelRepo.save(updatePlaceNearTheHotel);

    return Map.of("message",
            "Successfully updated place info");
    }


    public Map<String ,String > deletePlace(Long placeId){
        this.placeNearHotelImageRepo.deleteAllByPlaceId(placeId);
        this.placeNearHotelRepo.deleteById(placeId);

    return Map.of("message","Successfully Deleted");
    }




}
