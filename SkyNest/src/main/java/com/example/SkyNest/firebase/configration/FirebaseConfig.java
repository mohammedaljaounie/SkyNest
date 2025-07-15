package com.example.SkyNest.firebase.configration;

import com.example.SkyNest.firebase.properties.FirebaseProperties;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Autowired
    private FirebaseProperties firebaseProperties;


    /// Steps to adjust notification settings :
    /// create bean for send message
    /// create bean for connect with firebase app
    /// create bean for google credentials to make real connect

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp){
        return FirebaseMessaging.getInstance(firebaseApp);
    }

    /// bean for first connect with firebase app
    @Bean
    public FirebaseApp firebaseApp(GoogleCredentials credentials){

        FirebaseOptions options = FirebaseOptions
                .builder()
                .setCredentials(credentials)
                .build();

        return FirebaseApp.initializeApp(options);
    }

    /**
     * bean for make final connect with firebase app*/
    @Bean
    public  GoogleCredentials googleCredentials() throws  Exception{
        if (firebaseProperties.getAccountService()!=null){
            try(InputStream inputStream = firebaseProperties.getAccountService().getInputStream()) {
                return GoogleCredentials.fromStream(inputStream);
            }
        }
        else{
            return GoogleCredentials.getApplicationDefault();
        }

    }



}
