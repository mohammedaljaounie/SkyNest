package com.example.SkyNest.controller.AController;

import com.example.SkyNest.dto.HotelResponse;
import com.example.SkyNest.model.entity.Hotel;
import com.example.SkyNest.model.entity.HotelImage;
import com.example.SkyNest.model.repository.HotelRepository;
import com.example.SkyNest.service.AdminService.AHotelService.AHotelService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/hotel")
public class AHotelController {

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Autowired
    private AHotelService aHotelService;

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/showHotel_direct")
    public ResponseEntity<HotelResponse> showHotelDirect(){

        return ResponseEntity.
                ok(this.aHotelService.showHotelInfo());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam Long id,@RequestParam MultipartFile file) {
            try {
                String message = aHotelService.uploadImage(id,file);
                return ResponseEntity.ok(message);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("فشل في رفع الصورة");
            }
        }

    @GetMapping("/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) throws Exception {

            byte[] imageData = this.aHotelService.viewImage(imageName);

            String contentType = Files.probeContentType(Path.of(uploadDir + imageName));
            return ResponseEntity.ok().
                    contentType(MediaType.parseMediaType(contentType)).body(imageData);

    }

    @GetMapping("/total_balance/{hotelId}")
    public ResponseEntity<Map<String,Double>> getTotalBalanceForHotel(@PathVariable Long hotelId){

        Double totalBalance = this.aHotelService.getTotalBalanceForHotel(hotelId);
        if (totalBalance != -1){
            return ResponseEntity.ok()
                    .body(Map.of("totalBalance",totalBalance));
        }

        return ResponseEntity.status(403)
                .body(null);
    }





}
