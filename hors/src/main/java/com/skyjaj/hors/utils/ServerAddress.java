package com.skyjaj.hors.utils;

/**服务端url地址
 * Created by Administrator on 2016/2/25.
 */
public class ServerAddress {


    //本地服务器地址
    public static final String LOCAL_SERVER = "http://192.168.243.2:8080/";//http://192.168.31.230:8080/
    //远程服务端地址
    public static final String REMOTE_SERVER = "http://139.129.8.184:8080/entrance-service/";
    public static final String REMOTE_SERVER_ULR = REMOTE_SERVER;

    //患者登录
    public static final String PATIENT_LOGIN_URL = REMOTE_SERVER_ULR+"patient/login";
    //医生登录
    public static final String DOCTOR_LOGIN_URL = REMOTE_SERVER_ULR+"doctor/login";
    //根据科室查找所有该科室的医生
    public static final String FIND_DOCTOR_BY_DEPARTMENT_ID_URL = REMOTE_SERVER_ULR+"doctor/findDoctorByDepartmentId";
    //管理员登录
    public static final String ADMIN_LOGIN_URL = REMOTE_SERVER_ULR+"admin/login";
    //查找医院所有科室
    public static final String FIND_ALL_DEPARTMENT_URL = REMOTE_SERVER_ULR+"department/findAll";
    //患者注册信息
    public static final String PATIENT_REGISTER_URL = REMOTE_SERVER_ULR+"patient/register";
    //确认预约
    public static final String PATIENT_RESERVATION_URL = REMOTE_SERVER_ULR + "patient/reservation";
    //获取日期时间getSchedule
    public static final String DOCTOR_SCHEDULE_URL = REMOTE_SERVER_ULR + "doctor/getSchedule";
    //获取具体的时间
    public static final String DOCTOR_SCHEDULE_TIME = REMOTE_SERVER_ULR + "doctor/reservationPointTimes";
    public static final String DOCTOR_NOWDAY_SCHEDULE_TIME = REMOTE_SERVER_ULR + "doctor/reservationNowdayPoinTimes";

    public static final String DOCTOR_STOP_URL = REMOTE_SERVER_ULR + "doctor/stopById";
    public static final String DOCTOR_DELETE_URL = REMOTE_SERVER_ULR + "doctor/deleteById";
    //模糊搜索
    public static final String FIND_DOCTORS_BY_NAME = REMOTE_SERVER_ULR + "doctor/findDoctorsByName";
    //获取病人个人自信
    public static final String PATIENT_INFORMACTION_URL = REMOTE_SERVER_ULR + "patient/informaction";
    public static final String PATIENT_INFORMACTION_UPDATE_URL = REMOTE_SERVER_ULR + "patient/updateInformaction";
    public static final String PATIENT_INFORMACTION_UPDATE_PASSWORD_URL = REMOTE_SERVER_ULR + "patient/updatePassword";
    public static final String PATIENT_FORGET_PASSWORD_URL = REMOTE_SERVER_ULR + "patient/forgetPassword";

    public static final String PATIENT_DELETE_ERSERVATION_URL = REMOTE_SERVER_ULR + "patient/deleteReservation";
    public static final String PATIENT_CANCEL_RESERVATION_URL = REMOTE_SERVER_ULR + "patient/cancelReservation";
    public static final String PATIENT_COMMENT_RESERVATION_URL = REMOTE_SERVER_ULR + "patient/commentReservation";


    public static final String DOCTOR_INFORMACTION_URL = REMOTE_SERVER_ULR + "doctor/information";
    public static final String DOCTOR_INFORMACTION_UPDATE_URL = REMOTE_SERVER_ULR + "doctor/updateInformaction";
    public static final String DOCTOR_INFORMACTION_UPDATE_PASSWORD_URL = REMOTE_SERVER_ULR + "doctor/updatePassword";


    public static final String ADMIN_INFORMACTION_URL = REMOTE_SERVER_ULR + "admin/information";
    public static final String ADMIN_INFORMACTION_UPDATE_URL = REMOTE_SERVER_ULR + "admin/updateInformation";
    public static final String ADMIN_INFORMACTION_UPDATE_PASSWORD_URL = REMOTE_SERVER_ULR + "admin/updatePassword";

    public static final String ADMIN_NOTIFICATION_TO_USER_URL = REMOTE_SERVER_ULR + "push/sendToUser";
    public static final String ADMIN_NOTIFICATION_SEND_BROADCAST_URL = REMOTE_SERVER_ULR + "push/sendBroadcase";
    public static final String ADMIN_UPDATE_DEPARTMENT_URL = REMOTE_SERVER_ULR + "admin/updateDepartment";
    public static final String ADMIN_DELETE_DEPARTMENT_URL = REMOTE_SERVER_ULR + "admin/deleteDepartment";
    public static final String ADMIN_ADD_DEPARTMENT_URL = REMOTE_SERVER_ULR + "admin/addDepartment";
    public static final String ADMIN_STOP_DEPARTMENT_URL = REMOTE_SERVER_ULR + "admin/stopDepartment";


    //获取病人预约历史信息
    public static final String PATIENT_RESERVATION_HISTORY_URL = REMOTE_SERVER_ULR + "patient/reservationHistory";

    //获取医生被预约历史信息
    public static final String DOCTOR_HISTORY_RESERVATION_URL = REMOTE_SERVER_ULR+"doctor/getHistoryReservations";
    public static final String DOCTOR_RESERVATION_URL = REMOTE_SERVER_ULR+"doctor/getReservations";

}
