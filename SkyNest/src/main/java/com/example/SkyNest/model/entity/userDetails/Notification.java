package com.example.SkyNest.model.entity.userDetails;

import jakarta.persistence.*;
import org.aspectj.weaver.ast.Not;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String notificationDetails;

    @ManyToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private User user;

    public Notification(){
        this.id = 0L ;
        this.notificationDetails = null;
        this.user = null;
    }

    public Notification(String notificationDetails,User user){
        this.notificationDetails = notificationDetails;
        this.user = user;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(String notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
