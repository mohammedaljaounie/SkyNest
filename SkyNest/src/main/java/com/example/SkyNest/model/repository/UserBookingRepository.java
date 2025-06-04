package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.UserBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserBookingRepository extends JpaRepository<UserBooking,Long> {
    List<UserBooking> findByUserId(Long userId);

    Optional<UserBooking> findByIdAndUserId(Long userBookingId, Long userId);
}
