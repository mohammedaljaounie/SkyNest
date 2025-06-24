package com.example.SkyNest.firebase.controller;

import com.example.SkyNest.firebase.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notification")
public class FirebaseController {

    @Autowired
    private FirebaseService firebaseService;

    @PostMapping("/send")
    public String sendNotification(
            @RequestParam String title,
            @RequestParam String body,
            @RequestParam String fcmToken
    )throws  Exception{

            return this.firebaseService.sendNotification(title, body, fcmToken);

    }




}
