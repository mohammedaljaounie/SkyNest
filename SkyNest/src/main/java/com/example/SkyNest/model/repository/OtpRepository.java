package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpToken,Long> {

   Optional<OtpToken> findByEmailAndCodeOrderByIdDesc(String email, String code);
}
