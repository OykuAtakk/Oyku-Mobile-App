package com.oykuatak.oyku.Model;

public class User {
    private String userName,userEmail,userId,userProfile;
    public User(String userName, String userEmail, String userId,String userProfile) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userId = userId;
        this.userProfile=userProfile;
    }
    public User() {

    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



}
