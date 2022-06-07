package com.example.chou.automobileunconsciouspaysystem.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String getDateFormatString(Date date) {
        String dateString = null;
        if (null != date) {
            SimpleDateFormat format=new SimpleDateFormat("yyyyMMdd_HHmmss");
            dateString = format.format(date);
        }

        return dateString;
    }
}
