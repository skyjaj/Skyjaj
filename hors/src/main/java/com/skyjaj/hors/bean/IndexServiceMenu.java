package com.skyjaj.hors.bean;

/**
 * Created by Administrator on 2016/1/15.
 */
public class IndexServiceMenu extends BaseMessage{

    //图片id
    private int resId;
    //text
    private String text;
    //类型
    private int form;


    public IndexServiceMenu(int resId, String text, int form) {
        this.resId = resId;
        this.text = text;
        this.form = form;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getForm() {
        return form;
    }

    public void setForm(int form) {
        this.form = form;
    }
}
