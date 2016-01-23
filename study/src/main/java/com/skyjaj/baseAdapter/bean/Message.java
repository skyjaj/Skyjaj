package com.skyjaj.baseAdapter.bean;

import java.util.Date;

/**
 * 封装消息类
 * Created by Administrator on 2015/11/26.
 */
public class Message extends BaseMessage{

    private String name;
    private String msg;
    private Date date;


    public Message(String name, String msg, Date date, Type type) {
        this.name = name;
        this.msg = msg;
        this.date = date;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

}
