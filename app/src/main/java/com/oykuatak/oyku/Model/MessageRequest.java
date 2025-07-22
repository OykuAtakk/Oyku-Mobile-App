package com.oykuatak.oyku.Model;

public class MessageRequest {
    private String channelId;
    private String userId;
    private String userName;
    private String userProfile;

    public MessageRequest() {
    }

    public String getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(String userProfile) {
        this.userProfile = userProfile;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MessageRequest(String channelId, String userId,String userName,String userProfile) {
        this.channelId = channelId;
        this.userId = userId;
        this.userName=userName;
        this.userProfile=userProfile;
    }
}
