package com.example.dlapd.seoulcarmap.model;

import com.google.firebase.database.Exclude;
import com.kakao.kakaonavi.Destination;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    private String mKey;

    public Map<String,Boolean> users = new HashMap<>(); //채팅방의 유저들 - 다가지고있는 애
    public Map<String,Comment> comments = new HashMap<>();//채팅방의 대화내용

    @Exclude
    public String getKey() {
        return mKey;
    }

    @Exclude
    public void setKey(String key) {
        mKey = key;
    }

    public static class Comment {

        public String muserId; //uid
        public String messagge;
        public Object timestamp;
        public String fcmToken;
        public Map<String,Object> readUsers = new HashMap<>();
    }


}
