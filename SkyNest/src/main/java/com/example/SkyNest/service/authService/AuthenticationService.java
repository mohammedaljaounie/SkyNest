package com.example.SkyNest.service.authService;


import com.example.SkyNest.dto.LoginResponse;
import com.example.SkyNest.dto.LoginUserDto;
import com.example.SkyNest.dto.OtpRequest;
import com.example.SkyNest.dto.RegisterUserDto;
import com.example.SkyNest.model.entity.OtpToken;
import com.example.SkyNest.model.entity.Role;
import com.example.SkyNest.model.entity.User;
import com.example.SkyNest.model.entity.UserCard;
import com.example.SkyNest.model.repository.OtpRepository;
import com.example.SkyNest.model.repository.RoleRepo;
import com.example.SkyNest.model.repository.UserCardRepository;
import com.example.SkyNest.model.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthenticationService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCardRepository userCardRepository;
    @Autowired
    private HttpServletRequest request;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private RoleRepo roleRepo  ;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Map<String,String> register(RegisterUserDto input) {
        User user = new User();
        user  .setFullName(input.getFullName());
        user .setEmail(input.getEmail());
        user .setPassword(passwordEncoder.encode(input.getPassword()));
        user.setLongitude(input.getLongitude());
        user.setLatitude(input.getLatitude());
        user.setLevel(0);
        user.setEnabled(false);
        Optional<Role> role = this.roleRepo.findByName("user");
        if (role.isPresent()){
            user.setRole(role.get());
               User user1 = this.userRepository.save(user);
            OtpToken otpToken = new OtpToken();
            otpToken.setEmail(user.getEmail());
            String otpCode = emailService.generateOtp();
            otpToken.setCode(otpCode);
            otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            otpToken.setVerified(false);
            this.otpRepository.save(otpToken);
            this.emailService.sendOTP(user.getEmail(), otpCode);
            return Map.of("email", user1.getEmail(),"message","Successfully Added , we will send code to your email to verified");
        }
return Map.of("message","Not Successfully added");
    }

    public  Map<String,String>  adminRegister(RegisterUserDto input) {
        User user = new User();
        user .setFullName(input.getFullName());
        user .setEmail(input.getEmail());
        user .setPassword(passwordEncoder.encode(input.getPassword()));
        user.setLongitude(input.getLongitude());
        user.setLatitude(input.getLatitude());
        user.setLevel(0);
        user.setEnabled(false);
        Optional<Role> role = this.roleRepo.findByName("admin");
        if (role.isPresent()){
            user.setRole(role.get());
            this.userRepository.save(user);
            OtpToken otpToken = new OtpToken();
            otpToken.setEmail(user.getEmail());
            String otpCode = emailService.generateOtp();
            otpToken.setCode(otpCode);
            otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            otpToken.setVerified(false);
            this.otpRepository.save(otpToken);
            this.emailService.sendOTP(user.getEmail(), otpCode);
            return Map.of("email",input.getEmail(),"message","Successfully added , check in your email to give code");
        }
        return Map.of("message","Not Successfully added");
    }

    public LoginResponse login(LoginUserDto loginUserDto) {
        Optional<User> user = this.userRepository.findByEmail(loginUserDto.getEmail());

        if (user.isPresent()) {

            if (passwordEncoder.matches(loginUserDto.getPassword(), user.get().getPassword())) {
//               String otpCode = emailService.generateOtp();
//               OtpToken otpToken = new OtpToken();
//               otpToken.setEmail(user.get().getEmail());
//               otpToken.setCode(otpCode);
//               otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
//               otpToken.setVerified(false);
//               otpRepository.save(otpToken);
//               emailService.sendOTP(user.get().getEmail(),otpCode);


                //User authenticatedUser = authenticationService.authenticate(loginUserDto);
                String jwtToken = jwtService.generateToken(user.get());
                LoginResponse loginResponse = new LoginResponse();
                loginResponse.setToken(jwtToken);
                loginResponse.setExpiresIn(jwtService.getExpirationTime());

//               return Map.of("email",
//                       user.get().getEmail(),
//                       "message",
//                       "Successfully Matches , look to your email to give code"
//               );
//           }
//           return Map.of("message","Not Successfully Matches , your password it wrong");
                return loginResponse;
            }
            return null;
        }

            return null;

        }
    // TODO : start FORGET PASSWORD

    public Map<String,String> rememberPassword(String email){

        Optional<User> user = this.userRepository.findByEmail(email);

        if (user.isPresent()){
            String otpCode = emailService.generateOtp();
            OtpToken otpToken = new OtpToken();
            otpToken.setEmail(email);
            otpToken.setCode(otpCode);
            otpToken.setVerified(false);
            otpToken.setExpiresAt(LocalDateTime.now().plusMinutes(5));
            otpRepository.save(otpToken);
            emailService.sendOTP(email,otpCode);
            return Map.of("message","we will send code to your email , enter in next page");
        }
        return Map.of("message","this email is wrong");

    }

    public Map<String,String> verifyRemember(OtpRequest request){

        Optional<OtpToken> otpToken = this.otpRepository.findByEmailAndCodeOrderByIdDesc(request.getEmail(), request.getCode());
        if (otpToken.isPresent()){

            if (otpToken.get().getExpiresAt().isBefore(LocalDateTime.now()))
                return Map.of("message",
                        "Finish time to this code , send code again");


            otpToken.get().setVerified(true);
            otpRepository.save(otpToken.get());

            return Map.of("email",
                    request.getEmail());

        }
        else {
            return Map.of("message",
                    "There are something is wrong , please enter correct information");
        }
    }

    public LoginResponse updatePassword(String email,String password){

        Optional<User> user = this.userRepository.findByEmail(email);
        if (user.isPresent()){
            User updatedUser  = user.get();
            updatedUser.setPassword(passwordEncoder.encode(password));
            updatedUser.setEnabled(true);
            userRepository.save(updatedUser);
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            loginResponse.setToken(jwtService.generateToken(updatedUser));
            return  loginResponse;
        }
        return null;

    }

    // TODO : finish FORGET PASSWORD


    @Transactional
    public String verifyOtp(OtpRequest request) {
        Optional<OtpToken> tokenOpt = otpRepository.findByEmailAndCodeOrderByIdDesc(request.getEmail(), request.getCode());

        if (tokenOpt.isPresent()) {

            if (tokenOpt.isEmpty() || tokenOpt.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                return "OTP is inactive or expired";
            }

            OtpToken token = tokenOpt.get();
            token.setVerified(true);
            otpRepository.save(token);

            Optional<User> user = userRepository.findByEmail(request.getEmail());
            if (user.isPresent()) {
                user.get().setEnabled(true);
                userRepository.save(user.get());

                if (userCardRepository.findByUserId(user.get().getId()).isPresent()){
                    return jwtService.generateToken(user.get());
                }else {

                    createUserCard(user.get());
                    return jwtService.generateToken(user.get());
                }
            }
            return "Verification failed";
        }
        else
            return "Verification failed";
    }

    private void createUserCard(User user){
        UserCard userCard = new UserCard();
        userCard.setUser(user);
        userCard.setTotalBalance(0);
        userCardRepository.save(userCard);
    }



    public Map<String,String> logout(){

          String jwt   = request.getHeader("Authorization");
          String token = jwt.substring(7);
          Long id = jwtService.extractId(token);
        System.out.println(id   );
        Optional<User> user = this.userRepository.findById(id);
        if (user.isPresent()){
            this.userRepository.deleteById(id);
            return Map.of("message","Successfully logout");
        }
        return Map.of("message","Not Successfully logout");

    }






}