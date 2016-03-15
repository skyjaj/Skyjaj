package com.skyjaj.hors.utils;

import com.skyjaj.hors.bean.SortModel;

import java.util.Comparator;

/**
 * 拼音比较类
 * Created by Administrator on 2016/3/15.
 */
public class PinyinComparator implements Comparator<SortModel> {

    public int compare(SortModel o1, SortModel o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}