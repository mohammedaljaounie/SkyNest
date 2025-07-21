package com.example.SkyNest.controller.AController;

import com.example.SkyNest.dto.hoteldto.UserInfo;
import com.example.SkyNest.dto.hoteldto.UserRequestInfo;
import com.example.SkyNest.service.AdminService.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/profile")
    public ResponseEntity<UserInfo> profile(){
        UserInfo userInfo = this.adminService.profile();
        if (userInfo!=null)
            return ResponseEntity.ok().body(userInfo);
        return ResponseEntity.status(400).body(null);
    }

    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(@RequestBody UserRequestInfo userRequestInfo){
        Map<String ,String> message = this.adminService.editProfile(userRequestInfo);

        if (message.get("message").equals("Successfully Updated")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }


}
