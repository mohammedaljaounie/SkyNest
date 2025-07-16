package com.example.SkyNest.model.repository.userDetails;

import com.example.SkyNest.model.entity.userDetails.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndRoleName(Long userId,String role);

    List<User> findByRoleNameAndEnabled(String  roleName, boolean isEnable);

    List<User> findAllByRoleName(String role);
}
