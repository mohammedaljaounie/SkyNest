package com.example.SkyNest.model.repository;

import com.example.SkyNest.model.entity.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCardRepository extends JpaRepository<UserCard,Long> {

    Optional<UserCard> findByUserId(Long id);

}
