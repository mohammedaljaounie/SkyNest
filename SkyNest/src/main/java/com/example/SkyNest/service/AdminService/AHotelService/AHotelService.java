package com.example.SkyNest.service.AdminService.AHotelService;

import ch.qos.logback.core.util.StringUtil;
import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.dto.ImageDTO;
import com.example.SkyNest.model.entity.Hotel;
import com.example.SkyNest.model.entity.HotelCard;
import com.example.SkyNest.model.entity.HotelImage;
import com.example.SkyNest.model.repository.HotelCardRepository;
import com.example.SkyNest.model.repository.HotelImageRepository;
import com.example.SkyNest.model.repository.HotelRepository;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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

    @Value("${image.upload.dir}")
    private String uploadDir;


    public HotelResponse showHotelInfo(){

        String jwt = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long id = jwtService.extractId(token);
        Optional<Hotel> hotelOpt = this.hotelRepository.findByUserId(id);
        if (hotelOpt.isEmpty()){
            return null;
        }

        Hotel hotelInfo  = hotelOpt.get();
        HotelResponse hotelResponse  = new HotelResponse();
        hotelResponse.setId(hotelInfo.getId());
        hotelResponse.setName(hotelInfo.getName());
        hotelResponse.setLocation(hotelInfo.getLocation());
        hotelResponse.setDescription(hotelInfo.getDescription());
        hotelResponse.setRatingCount(hotelInfo.getRatingCount());
        hotelResponse.setAvgRating(hotelInfo.getAvgRating());
        List<ImageDTO> imageResponseList = new ArrayList<>();
        List<HotelImage> imageList  = hotelInfo.getHotelImageList();

        for (int i = 0; i < imageList.size() ; i++) {
            ImageDTO imageDTO = new ImageDTO();
            imageDTO.setId(imageList.get(i).getId());
            imageDTO.setImageUrl("http://localhost:8080/admin/hotel/"+imageList.get(i).getName());
            imageResponseList.add(imageDTO);
        }
        hotelResponse.setImageDTOList(imageResponseList);

        return hotelResponse;

    }

    public String uploadImage(Long id,MultipartFile image) throws IOException {
        Optional<Hotel> hotelOpt = this.hotelRepository.findById(id);
        if (hotelOpt.isEmpty()){
            return "This hotel is not found in our application";
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
        return "Successfully Upload";

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

    public Double getTotalBalanceForHotel(Long id){
        String jwt  = request.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<Hotel> optionalHotel = this.hotelRepository.findByUserId(userId);
        if (optionalHotel.isEmpty())
           return -1D;
        if (!Objects.equals(optionalHotel.get().getId(), id))
           return -1D;

        Optional<HotelCard> hotelCard = this.hotelCardRepository.findByHotelId(id);
        return hotelCard.map(HotelCard::getTotalBalance).orElse(-1D);

    }












}
