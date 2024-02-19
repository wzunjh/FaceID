package com.face.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author tanyongpeng
 * <p>des</p>
 **/
public class TimeUtils {

    public static String timeQuantum(){
        int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if (hour >=0 && hour <= 11){
            return "上午";
        }
        if (hour == 12 || hour == 13){
            return "中午";
        }
        if (hour > 13 && hour <= 18){
            return "下午";
        }
        return "晚上";
    }
}
