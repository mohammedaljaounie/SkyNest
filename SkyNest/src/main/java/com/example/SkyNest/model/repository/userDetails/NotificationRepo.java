package com.example.SkyNest.model.repository.userDetails;

import com.example.SkyNest.model.entity.userDetails.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification,Long> {



    @Query(value = "select n from Notification n where n.user.id = :userId")
    List<Notification> showUserNotification(Long userId);

}
