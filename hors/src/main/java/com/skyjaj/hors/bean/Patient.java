package com.skyjaj.hors.bean;

import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/1/22.
 * 病人表
 */
public class Patient {

    private String id;
    /*登陆帐号*/
    private String username;
    /*登陆密码*/
    private String password;
    private Integer sex;
    private Timestamp birthday;
    private String mobile;
    private String email;
    private Integer state;
    private String ip;
    private Timestamp createTime;
    private Timestamp lastUpdateTime;
    private Integer errorTimes;
    /*身份证号*/
    private String idCard;
    /*保险类型*/
    private Integer insuranceType;
    private String headUrl;
    /*绑定的停用卡*/
    private String creditCardId;
    /*帐户余额*/
    private Double accountBalance;

    private String token;

    private String remark;

    private String region;

    public void setRegion(String region) {
        this.region = region;
    }

    public String getRegion() {
        return region;
    }


    private String signature;

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public void setBirthday(Timestamp birthday) {
        this.birthday = birthday;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public Integer getErrorTimes() {
        return errorTimes;
    }

    public void setErrorTimes(Integer errorTimes) {
        this.errorTimes = errorTimes;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public Integer getInsuranceType() {
        return insuranceType;
    }

    public void setInsuranceType(Integer insuranceType) {
        this.insuranceType = insuranceType;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getCreditCardId() {
        return creditCardId;
    }

    public void setCreditCardId(String creditCardId) {
        this.creditCardId = creditCardId;
    }

    public Double getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Double accountBalance) {
        this.accountBalance = accountBalance;
    }
}
