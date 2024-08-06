package com.mabcoApp.mabco.Classes;

public class Notification {
    // Properties
    private int id;
    private String notificationTitle;
    private String notificationText;
    private String imageLink;
    private String notificationType;
    private String notificationDate;
    private String notificationInfo;
    private String notificationType2;

    // Constructor
    public Notification(int id, String notificationTitle, String notificationText, String imageLink, String notificationType, String notificationDate, String notificationInfo, String notificationType2) {
        this.id = id;
        this.notificationTitle = notificationTitle;
        this.notificationText = notificationText;
        this.imageLink = imageLink;
        this.notificationType = notificationType;
        this.notificationDate = notificationDate;
        this.notificationInfo = notificationInfo;
        this.notificationType2 = notificationType2;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotificationTitle() {
        return notificationTitle;
    }

    public void setNotificationTitle(String notificationTitle) {
        this.notificationTitle = notificationTitle;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationInfo() {
        return notificationInfo;
    }

    public void setNotificationInfo(String notificationInfo) {
        this.notificationInfo = notificationInfo;
    }

    public String getNotificationType2() {
        return notificationType2;
    }

    public void setNotificationType2(String notificationType2) {
        this.notificationType2 = notificationType2;
    }
}
