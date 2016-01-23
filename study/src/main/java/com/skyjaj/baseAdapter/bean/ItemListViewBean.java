package com.skyjaj.baseAdapter.bean;

/**
 * Created by skyjaj on 2015/11/25.
 */
public class ItemListViewBean extends  BaseMessage{


    private String mTitle;
    private String mText;
    private String mTime;
    private String mPhone;


    public ItemListViewBean() {

    }

    public ItemListViewBean(String mTitle, String mText, String mTime, String mPhone) {
        this.mTitle = mTitle;
        this.mText = mText;
        this.mTime = mTime;
        this.mPhone = mPhone;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getmText() {
        return mText;
    }

    public void setmText(String mText) {
        this.mText = mText;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }

    public String getmPhone() {
        return mPhone;
    }

    public void setmPhone(String mPhone) {
        this.mPhone = mPhone;
    }
}
