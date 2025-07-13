package com.example.SkyNest.service.SuperAdminService.SAHotelService;

import com.example.SkyNest.dto.hoteldto.UserInfo;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class SAUserService {

    // todo : show all users in app

    private final static String user_role_name = "user";
    private final static String admin_role_name = "admin";
    @Autowired
    private UserRepository userRepository;

    public List<UserInfo> showUnBlockUsers(){

      List<User> userList = userRepository.findByRoleNameAndEnabled(SAUserService.user_role_name,true);
      List<UserInfo> userInfoList  = new ArrayList<>();
      for (User user : userList){
          UserInfo userInfo = new UserInfo(
                  user.getId(),
                  user.getFullName(),
                  user.getEmail(),
                  user.getLatitude(),
                  user.getLongitude(),
                  user.getLevel()
          );
          userInfoList.add(userInfo);
      }
      return userInfoList;
    }

    public List<UserInfo> showBlockUsers(){
        final Long roleIdForUser = 3L;
        List<User> userList = userRepository.findByRoleNameAndEnabled(SAUserService.admin_role_name,false);
        List<UserInfo> userInfoList  = new ArrayList<>();
        for (User user : userList){
            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getLatitude(),
                    user.getLongitude(),
                    user.getLevel()
            );
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    public  List<UserInfo> showAllAdmin(){


        List<User> userList = userRepository.findByRoleNameAndEnabled(SAUserService.user_role_name,false);
        List<UserInfo> userInfoList  = new ArrayList<>();
        for (User user : userList){
            UserInfo userInfo = new UserInfo(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getLatitude(),
                    user.getLongitude(),
                    user.getLevel()
            );
            userInfoList.add(userInfo);
        }
        return userInfoList;
    }

    // todo : block user from app
    public Map<String,String> blockAUser(Long userId){
        Optional<User> userOptional  = this.userRepository.findById(userId);
        if (userOptional.isEmpty()){
            return Map.of("message",
                    "Sorry , this user is not found in our system");
        }




        return Map.of("message",
                "Sorry , you can't block this user now" +
                        "you can't access token");
    }




}
