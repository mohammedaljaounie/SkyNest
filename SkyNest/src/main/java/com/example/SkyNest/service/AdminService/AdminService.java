package com.example.SkyNest.service.AdminService;

import com.example.SkyNest.dto.hoteldto.UserInfo;
import com.example.SkyNest.dto.hoteldto.UserRequestInfo;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.userDetails.NotificationRepo;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import com.example.SkyNest.service.UserService.UserService;
import com.example.SkyNest.service.authService.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HttpServletRequest http;
    @Autowired
    private JwtService jwtService;
    private static final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NotificationRepo notificationRepo;


    // todo : admin profile by token
    public UserInfo profile() {
        String jwt = http.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);

        return userOptional.map(AdminService::getProfileInfo).orElse(null);
    }

    private static UserInfo getProfileInfo(User user) {

        return new UserInfo(user.getId(), user.getFullName(), user.getEmail(),user.getLatitude() ,user.getLongitude(), user.getLevel());
    }



    @Transactional
    public Map<String, String> editProfile(UserRequestInfo userRequestInfo) {

        String jwt = http.getHeader("Authorization");
        String token = jwt.substring(7);
        Long userId = jwtService.extractId(token);
        Optional<User> userOptional = this.userRepository.findById(userId);
        if (userOptional.isEmpty())
            return Map.of("message",
                    "Sorry , your account is not found in  our app");

        User user  = userOptional.get();

        if (userRequestInfo.getPassword()!=null){
            user.setPassword(passwordEncoder.encode(userRequestInfo.getPassword()));
        }
        if (userRequestInfo.getFullName()!=null){
            user.setFullName(userRequestInfo.getFullName());
        }
        if (userRequestInfo.getLatitude()!=0){
            user.setLatitude(userRequestInfo.getLatitude());
        }
        if (userRequestInfo.getLongitude()!=0){
            user.setLongitude(userRequestInfo.getLongitude());
        }
        this.userRepository.save(user);
        return Map.of("message",
                "Successfully Updated");
    }

}
