package com.example.SkyNest.config;

import com.example.SkyNest.model.entity.Role;
import com.example.SkyNest.model.entity.User;
import com.example.SkyNest.model.repository.RoleRepo;
import com.example.SkyNest.model.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Optional;

@Component
public class ApplicationUp implements ApplicationRunner {
    @Autowired
    private RoleRepo roleRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Override
    public void run(ApplicationArguments args) throws Exception {

        if (roleRepo.count()<=0){

            Role superAdmin = new Role();
            superAdmin.setName("super_admin");
            this.roleRepo.save(superAdmin);



            Role admin = new Role();
            admin.setName("admin");
            this.roleRepo.save(admin);


            Role user = new Role();
            user.setName("user");
            this.roleRepo.save(user);


            User userSuperAdmin = new User();
            userSuperAdmin  .setFullName("Mohammed samir");
            userSuperAdmin .setEmail("so2004m@gmail.com");
            userSuperAdmin .setPassword(passwordEncoder.encode("12345"));
            userSuperAdmin.setLatitude(32.124);
            userSuperAdmin.setLongitude(32.245);
            userSuperAdmin.setRole(superAdmin);
                userRepository.save(userSuperAdmin);





        }



    }
}
