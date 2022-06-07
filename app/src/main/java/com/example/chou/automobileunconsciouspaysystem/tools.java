package com.example.chou.automobileunconsciouspaysystem;

import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.util.Date;

public class tools {

    private static boolean gusetCar =false;

    public static void setGusetCar(boolean gusetCar) {
        tools.gusetCar = gusetCar;
    }

    public static boolean getGusetCar() {
        return gusetCar;
    }

    //时间差计算函数
    public static String countDate(String intime,String outtime) throws ParseException {
        SimpleDateFormat sdfx = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//模板1
        SimpleDateFormat sdfh = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//模板2
        String inTime = sdfh.format(sdfx.parse(intime));//先将时间转换为模板2
        String outTime = sdfh.format(sdfx.parse(outtime));
        String differenceFormat = null;
        Date inDateTime = sdfh.parse(inTime);//将模板2的时间转换为Date类型
        Date outDateTime = sdfh.parse(outTime);
        long difference = (outDateTime.getTime() - inDateTime.getTime())/1000;
        long days = difference / (1000 * 60 * 60 * 24);
        long hours = (difference % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (difference % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (difference % (1000 * 60)) / 1000;
        return String.valueOf(difference);
    }
    //费用计算函数
    public static int countMoney(String time){
        int total=0;
        int times=Integer.parseInt(time);
        if(times<3600){
            total=1;
        } else if(times<10800){
            total=5;
            }else{
                total=5+((times-10800)%3600)*3;
                }
        return total;
    }

    //当前时间获取函数
    public static String getTime(){
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
        return dateFormat.format( now );
    }

    public static void closeDb(DBManager dbHelper, SQLiteDatabase db)
    {
        db.close();
        dbHelper.closeDatabase();
    }

}

