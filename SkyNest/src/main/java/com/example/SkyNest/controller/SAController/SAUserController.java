package com.example.SkyNest.controller.SAController;

import com.example.SkyNest.dto.UserInfo;
import com.example.SkyNest.service.SuperAdminService.SAHotelService.SAUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping("/super_admin")
public class SAUserController {

    @Autowired
    private SAUserService saUserService;

    @GetMapping("/unblock_users")
    public ResponseEntity<List<UserInfo>> showUnBlockUser(){
        return ResponseEntity.ok(this.saUserService.showUnBlockUsers());
    }

    @GetMapping("/block_users")
    public ResponseEntity<List<UserInfo>> showBlockUser(){
        return ResponseEntity.ok(this.saUserService.showBlockUsers());
    }

    @GetMapping("/get_admin")
    public ResponseEntity<List<UserInfo>> showAdmin(){
        return ResponseEntity.ok(this.saUserService.showAllAdmin());
    }



}
