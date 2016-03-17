package com.skyjaj.hors.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.skyjaj.hors.adapter.TimestampTypeAdapter;
import com.skyjaj.hors.bean.Department;
import com.skyjaj.hors.db.DBDepartment;

import java.sql.Timestamp;
import java.util.List;

/**
 * 对象字段转换
 * Created by Administrator on 2016/3/16.
 */
public class DBUtil {

    public static final Gson gson = new GsonBuilder().registerTypeAdapter(Timestamp.class, new TimestampTypeAdapter()).create();


    /**
     * 将对象字段转换
     * @param department
     * @return
     */
    public static DBDepartment turn2DB(Department department) {

        DBDepartment dbDepartment = new DBDepartment();
        dbDepartment.setDepartId(department.getId());
        dbDepartment.setNameCn(department.getNameCn());
        dbDepartment.setNameEn(PinYinUtil.getPingYin(department.getNameCn()));
        dbDepartment.setHospitalId(department.getHospitalId());
        dbDepartment.setState(department.getState());
        dbDepartment.setCreateTime(dbDepartment.getCreateTime());
        dbDepartment.setMasterId(department.getMasterId());
        dbDepartment.setMasterName(department.getMasterName());
        dbDepartment.setDepartmentOrder(department.getDepartmentOrder());
        dbDepartment.setNameIndex(PinYinUtil.getPinYinHeadChar(department.getNameCn()));
        return dbDepartment;
    }


}
