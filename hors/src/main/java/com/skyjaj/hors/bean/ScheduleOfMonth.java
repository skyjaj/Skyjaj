package com.skyjaj.hors.bean;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Created by Administrator on 2016/2/28.
 * 月排班表
 */
public class ScheduleOfMonth implements Serializable{

    private String id;
    private String doctorId;
    //上班
    private int periodOfTime;
    private int state;
    private String departmentId;
    private String hospitalId;
    private String remark;
    //这一天上班时间
    private Timestamp workday;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public int getPeriodOfTime() {
        return periodOfTime;
    }

    public void setPeriodOftime(int periodOftime) {
        this.periodOfTime = periodOftime;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getHospitalId() {
        return hospitalId;
    }

    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Timestamp getWorkday() {
        return workday;
    }

    public void setWorkday(Timestamp workday) {
        this.workday = workday;
    }
}
