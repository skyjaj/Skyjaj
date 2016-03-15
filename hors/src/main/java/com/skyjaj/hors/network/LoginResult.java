package com.skyjaj.hors.network;

public class LoginResult {
    private String uid;
    private String time;
    private String token;
    private String username;
    private boolean result;//请求结果
    private String message;//错误信息
    private String role;
    private int type;//角色类型

    public LoginResult() {
    }

    /**
     * @param uid
     * @param time
     * @param token
     * @param username
     * @param result
     * @param message
     * @param role
     * @param type
     */
    public LoginResult(String uid, String time, String token, String username, boolean result, String message, String role, int type) {
        this.uid = uid;
        this.time = time;
        this.token = token;
        this.username = username;
        this.result = result;
        this.message = message;
        this.role = role;
        this.type = type;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean getResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}