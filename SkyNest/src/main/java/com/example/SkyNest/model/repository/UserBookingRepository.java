package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.UserBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBookingRepository extends JpaRepository<UserBooking,Long> {
    List<UserBooking> findByUserId(Long userId);
}
