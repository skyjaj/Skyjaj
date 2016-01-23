package com.skyjaj.baseAdapter.bean;

/**
 * Created by Administrator on 2015/11/26.
 */
public class BaseMessage {


    protected Type type;

    public  enum Type{

        INCOMING,OUTCOMING

    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
