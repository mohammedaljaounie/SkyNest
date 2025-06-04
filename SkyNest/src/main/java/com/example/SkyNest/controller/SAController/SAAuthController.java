package com.example.SkyNest.controller.SAController;

import com.example.SkyNest.dto.RegisterUserDto;
import com.example.SkyNest.service.authService.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/super_admin/auth")
public class SAAuthController {

    @Autowired
    private AuthenticationService authenticationService;

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



}
