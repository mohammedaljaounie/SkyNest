package com.example.SkyNest.model.repository;

import com.example.SkyNest.dto.UserInfo;
import com.example.SkyNest.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);

    List<User> findByRoleNameAndEnabled(String  roleName, boolean isEnable);
}
