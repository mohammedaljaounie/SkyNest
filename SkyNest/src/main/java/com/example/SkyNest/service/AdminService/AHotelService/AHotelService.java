package com.example.SkyNest.service.AdminService.AHotelService;

import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.dto.ImageDTO;
import com.example.SkyNest.model.entity.hotel.Hotel;
import com.example.SkyNest.model.entity.hotel.HotelBooking;
import com.example.SkyNest.model.entity.hotel.HotelCard;
import com.example.SkyNest.model.entity.hotel.HotelImage;
import com.example.SkyNest.model.repository.hotel.HotelCardRepository;
import com.example.SkyNest.model.repository.hotel.HotelImageRepository;
import com.example.SkyNest.model.repository.hotel.HotelRepository;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
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

    @Value("${image.upload.dir}")
    private String uploadDir;


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

    public Map<String,String> uploadImage(Long hotelId,MultipartFile image) throws IOException {
        Optional<Hotel> hotelOpt = this.hotelRepository.findById(hotelId);
        if (hotelOpt.isEmpty()){
            return Map.of("message","This hotel is not found in our application");
        }
        String uniqueImageName = UUID.randomUUID()+"_"+image.getOriginalFilename();
        String imagePath = uploadDir+image.getOriginalFilename();

        HotelImage hotelImage = new HotelImage();
        hotelImage.setName(uniqueImageName);
        hotelImage.setPath(imagePath);
        hotelImage.setType(image.getContentType());
        hotelImage.setHotel(hotelOpt.get());
        this.hotelImageRepository.save(hotelImage);
        image.transferTo(new File(imagePath).toPath());
        return Map.of("message","Successfully Upload");

    }

    public byte[] viewImage(String imageName) throws Exception {
        Optional<HotelImage> image = this.hotelImageRepository.findByName(imageName);
        if (image.isPresent()) {
            String path = image.get().getPath();
            File file = new File(path);
            if (!file.exists()) throw new FileNotFoundException("Image file not found");
            return Files.readAllBytes(file.toPath());
        }
        return null;
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












}
