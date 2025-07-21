package com.example.SkyNest.controller.SAController;

import com.example.SkyNest.dto.hoteldto.UserInfo;
import com.example.SkyNest.dto.hoteldto.UserRequestInfo;
import com.example.SkyNest.service.SuperAdminService.SAHotelService.SAUserService;
import com.example.SkyNest.service.SuperAdminService.SuperAdminServ;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/super_admin")
public class SuperAdminController {

    private final SuperAdminServ superAdminServ;
    private final SAUserService saUserService;

    public SuperAdminController(SuperAdminServ superAdminServ, SAUserService saUserService) {
        this.superAdminServ = superAdminServ;
        this.saUserService = saUserService;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserInfo> profile(){
        UserInfo userInfo = this.superAdminServ.profile();
        if (userInfo!=null)
            return ResponseEntity.ok().body(userInfo);
        return ResponseEntity.status(400).body(null);
    }

    @PostMapping("/editProfile")
    public ResponseEntity<?> editProfile(@RequestBody UserRequestInfo userRequestInfo){
        Map<String ,String> message = this.superAdminServ.editProfile(userRequestInfo);

        if (message.get("message").equals("Successfully Updated")){
            return ResponseEntity.ok(message);
        }
        return ResponseEntity.status(400).body(message);
    }



    @GetMapping("/unblock_users")
    public ResponseEntity<List<UserInfo>> showUnBlockUser(){
        return ResponseEntity.ok(this.saUserService.showUnBlockUsers());
    }


    @GetMapping("/get_admin")
    public ResponseEntity<List<UserInfo>> showAdmin(){
        return ResponseEntity.ok(this.saUserService.showAllAdmin());
    }



}
