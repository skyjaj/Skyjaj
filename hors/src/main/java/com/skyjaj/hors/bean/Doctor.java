package com.skyjaj.hors.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/1/22.
 */
public class Doctor extends BaseMessage implements Serializable{

    private String id;
    /*医生姓名*/
    private String name;
    /*住址*/
    private String address;
    /*简介*/
    private String introduction;
    /*手机*/
    private String mobile;
    /*编号*/
    private String number;
    /*部门*/
    private String departmentId;
    private String email;
    private Integer sex;
    private Timestamp birthday;
    /*登陆帐号*/
    private String username;
    private String password;
    private Timestamp createTime;
    private String createUser;
    private Timestamp lastLoginTime;
    private Integer state;
    /*昵称*/
    private String nickname;
    private String ip;
    private String remark;
    private String hospitalId;
    private String title;
    private Integer errorTimes;
    /*身份证*/
    private String idCard;
    private String headUrl;
    private String creditCardId;
    /*挂号费*/
    private Double registeredFee;
    /*是否先付费再排队或预约*/
    private Integer isFirstPay;
    /*上班时段*/
    private String dayVisitsTime;
    /*已预约人数*/
    private Integer amountOfReservation;
    /*可预约的总人数*/
    private Integer sumOfReservation;

    private int type;

    private String token;

    private String signature;

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSignature() {
        return signature;
    }

    public void setToken(String token) {
        this.token = token;
    }


    public String getToken() {
        return token;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Double getRegisteredFee() {
        return registeredFee;
    }

    public void setRegisteredFee(Double registeredFee) {
        this.registeredFee = registeredFee;
    }

    public Integer getIsFirstPay() {
        return isFirstPay;
    }

    public void setIsFirstPay(Integer isFirstPay) {
        this.isFirstPay = isFirstPay;
    }

    public String getDayVisitsTime() {
        return dayVisitsTime;
    }

    public void setDayVisitsTime(String dayVisitsTime) {
        this.dayVisitsTime = dayVisitsTime;
    }

    public Integer getAmountOfReservation() {
        return amountOfReservation;
    }

    public void setAmountOfReservation(Integer amountOfReservation) {
        this.amountOfReservation = amountOfReservation;
    }

    public Integer getSumOfReservation() {
        return sumOfReservation;
    }

    public void setSumOfReservation(Integer sumOfReservation) {
        this.sumOfReservation = sumOfReservation;
    }
}
