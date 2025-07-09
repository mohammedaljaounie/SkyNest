package com.example.SkyNest.controller.UController;

import com.example.SkyNest.dto.NotificationResponse;
import com.example.SkyNest.dto.UserInfo;
import com.example.SkyNest.dto.UserRequestInfo;
import com.example.SkyNest.service.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;


    @GetMapping("/profile/{id}")
    public ResponseEntity<UserInfo> profile(@PathVariable Long id){
        UserInfo userInfo = this.userService.profile(id);
        if (userInfo!=null)
            return ResponseEntity.ok().body(userInfo);
        return ResponseEntity.status(400).body(null);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInfo> profile(){
        UserInfo userInfo = this.userService.profile();
        if (userInfo!=null)
            return ResponseEntity.ok().body(userInfo);
        return ResponseEntity.status(400).body(null);
    }

    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(@RequestBody UserRequestInfo userRequestInfo){
        Map<String ,String> message = this.userService.editProfile(userRequestInfo);

        if (message.get("message").equals("Successfully Updated")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }



    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationResponse>> notifications(){
        List<NotificationResponse> responses = this.userService.notification();
        if (responses!=null&&!responses.isEmpty()){
            return ResponseEntity.ok(responses);
        }
        return ResponseEntity.status(400).body(null);
    }

}
