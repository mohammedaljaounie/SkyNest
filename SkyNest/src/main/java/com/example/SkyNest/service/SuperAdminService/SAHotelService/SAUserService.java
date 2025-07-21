package com.example.SkyNest.service.SuperAdminService.SAHotelService;

import com.example.SkyNest.dto.hoteldto.UserInfo;
import com.example.SkyNest.model.entity.userDetails.User;
import com.example.SkyNest.model.repository.userDetails.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SAUserService {

    // todo : show all users in app

    private final static String user_role_name = "user";

    private final UserRepository userRepository;

    public SAUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

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




}
