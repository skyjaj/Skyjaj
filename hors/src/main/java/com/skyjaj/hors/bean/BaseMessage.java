package com.skyjaj.hors.bean;

/**
 * Created by Administrator on 2015/11/26.
 */
public class BaseMessage {


    protected Type itemType = Type.INCOMING;
    //拼音索引
    private String index;

    public  enum Type{

        INCOMING,OUTCOMING

    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getIndex() {
        return index;
    }

    public Type getItemType() {
        return itemType;
    }

    public void setItemType(Type type) {
       this.itemType=type;
    }
}
