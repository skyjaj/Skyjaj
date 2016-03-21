package com.skyjaj.hors.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/1/22.
 * 预约表
 */
public class Reservation extends BaseMessage implements Serializable{

    private String id;
    private String name;
    private String hospitalId;
    private String patientId;
    private String hospitalName;
    private String doctorName;
    private String doctorId;
    private String departmentName;
    private String departmentId;
    /*挂号费*/
    private Double registeredFee;
    /*是否付费*/
    private Integer isPaid;
    private Integer state;
    private Timestamp createTime;
    private Timestamp completeTime;
    private String remark;
    /*支付方式*/
    private int methodOfPayment;
    /*评论*/
    private String commentContent;
    private Integer isComment;
    private Timestamp commentTime;
    //预约时间
    private String appointmentTime;
    private boolean cancel;
    private boolean delete;

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean getCancel() {
        return cancel;
    }

    public boolean getDelete() {
        return delete;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentTime() {
        return appointmentTime;
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

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public Double getRegisteredFee() {
        return registeredFee;
    }

    public void setRegisteredFee(Double registeredFee) {
        this.registeredFee = registeredFee;
    }

    public Integer getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Integer isPaid) {
        this.isPaid = isPaid;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Timestamp completeTime) {
        this.completeTime = completeTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getMethodOfPayment() {
        return methodOfPayment;
    }

    public void setMethodOfPayment(int methodOfPayment) {
        this.methodOfPayment = methodOfPayment;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public Integer getIsComment() {
        return isComment;
    }

    public void setIsComment(Integer isComment) {
        this.isComment = isComment;
    }

    public Timestamp getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(Timestamp commentTime) {
        this.commentTime = commentTime;
    }
}