package com.example.SkyNest.firebase.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class FirebaseService {

    @Autowired
    private FirebaseMessaging firebaseMessaging;

    public void sendNotification(String title,String body,String fcmToken) throws Exception{
        

            Notification notification = Notification.builder()
                    .setTitle(title)
                    .setBody(body)
                    .build();

            Message message = Message.builder()
                    .setNotification(notification)
                    .setToken(fcmToken)
                    .build();

             firebaseMessaging.send(message);
    }







}
