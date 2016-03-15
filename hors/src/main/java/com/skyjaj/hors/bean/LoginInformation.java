package com.skyjaj.hors.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;

/**
 * 登录信息
 * Created by Administrator on 2016/2/25.
 */
public class LoginInformation extends DataSupport{

    @Column(ignore = false)
    private String uid;
    private boolean autoLogin;
    private String token;
    private String role;
    private int state;
    private String username;
    private String password;


    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
