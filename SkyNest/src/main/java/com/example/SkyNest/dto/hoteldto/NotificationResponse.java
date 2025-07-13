package com.example.SkyNest.dto.hoteldto;

public class NotificationResponse {

    private Long id;
    private String notificationDetails;

    public NotificationResponse() {
    }

    public NotificationResponse(Long id, String notificationDetails) {
        this.id = id;
        this.notificationDetails = notificationDetails;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setNotificationDetails(String notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public String getNotificationDetails() {
        return notificationDetails;
    }
}
