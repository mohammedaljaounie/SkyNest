package com.example.SkyNest.controller.authController;

import com.example.SkyNest.dto.LoginResponse;
import com.example.SkyNest.dto.LoginUserDto;
import com.example.SkyNest.dto.OtpRequest;
import com.example.SkyNest.dto.RegisterUserDto;
import com.example.SkyNest.model.repository.OtpRepository;
import com.example.SkyNest.model.repository.UserRepository;
import com.example.SkyNest.service.authService.AuthenticationService;
import com.example.SkyNest.service.authService.EmailService;
import com.example.SkyNest.service.authService.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private OtpRepository otpRepository;


    @Autowired

    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String,String>> register(@RequestBody RegisterUserDto registerUserDto) {
       // User registeredUser = authenticationService.signup(registerUserDto);
        //String jwtToken = jwtService.generateToken(registeredUser);
//        LoginResponse loginResponse = new LoginResponse();
//        loginResponse.setToken(jwtToken);
//        loginResponse.setExpiresIn(jwtService.getExpirationTime());
//        return ResponseEntity.ok(loginResponse);

        Map<String ,String> listOfMessage = authenticationService.register(registerUserDto);
        if(!listOfMessage.get("message").equals("Not Successfully added")){

            return ResponseEntity.ok(listOfMessage);

        }
        return ResponseEntity.status(403).body(listOfMessage);
    }

    @PostMapping("/admin-register")
    public ResponseEntity<Map<String,String>> adminRegister(@RequestBody RegisterUserDto registerUserDto) {
        // User registeredUser = authenticationService.signup(registerUserDto);
        //String jwtToken = jwtService.generateToken(registeredUser);
//        LoginResponse loginResponse = new LoginResponse();
//        loginResponse.setToken(jwtToken);
//        loginResponse.setExpiresIn(jwtService.getExpirationTime());
//        return ResponseEntity.ok(loginResponse);
        Map<String ,String > message = authenticationService.adminRegister(registerUserDto);
        if (!message.get("message").equals("Not Successfully added")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(304).body(message);
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
//        User authenticatedUser = authenticationService.authenticate(loginUserDto);
//        String jwtToken = jwtService.generateToken(authenticatedUser);
//        LoginResponse loginResponse = new LoginResponse();
//        loginResponse.setToken(jwtToken);
//        loginResponse.setExpiresIn(jwtService.getExpirationTime());

       LoginResponse loginResponse = authenticationService.login(loginUserDto);
//        if (message.get("message").equals("Successfully Matches , look to your email to give code")){
//            return ResponseEntity.ok(message);
//        }
//        return ResponseEntity.ok(authenticationService.login(loginUserDto));

        if (loginResponse!=null){
            return ResponseEntity.ok(loginResponse);
        }
        return ResponseEntity.status(304).body(null);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verified(@RequestBody OtpRequest request){

        String jwtToken = authenticationService.verifyOtp(request);
        if (!jwtToken.equals("OTP is inactive or expired")&&!jwtToken.equals("Verification failed")){
            LoginResponse loginResponse = new LoginResponse();
            loginResponse.setToken(jwtToken);
            loginResponse.setExpiresIn(jwtService.getExpirationTime());
            return ResponseEntity.ok(loginResponse);
        }
       return ResponseEntity.status(400).body(Map.of("message",jwtToken));
    }


    @PostMapping("/remember-password")
    public ResponseEntity<Map<String,String>> rememberPassword(@RequestParam String email){

        Map<String,String> message = this.authenticationService.rememberPassword(email);
        if (message.get("message").equals("this email is wrong")){
            return ResponseEntity.status(400).body(message);
        }
        return ResponseEntity.ok(authenticationService.rememberPassword(email));
    }

//    verifyRemember
    @PostMapping("/remember-verify")
    public ResponseEntity<Map<String,String>> verifyRemember(@RequestBody OtpRequest request){
        return ResponseEntity.ok(authenticationService.verifyRemember(request));
    }

    @PostMapping("/pass-update")
    public ResponseEntity<LoginResponse> passUpdate(@RequestParam String email,@RequestParam String password){

        LoginResponse loginResponse = authenticationService.updatePassword(email, password);
        if (loginResponse!=null) {
            return ResponseEntity.ok(authenticationService.updatePassword(email, password));
        }
        else {
            return ResponseEntity.status(304).body(null);
        }
    }

    @DeleteMapping("/logout")
    public ResponseEntity<Map<String,String>> logout(){
        return ResponseEntity.ok(authenticationService.logout());
    }

}