package com.example.SkyNest.firebase.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {

    @Value(value = "${firebase.service-account-path}")
    private Resource accountService;

    public Resource getAccountService(){
        return this.accountService;
    }

    public void setAccountService(Resource accountService){
        this.accountService  = accountService;
    }


}
