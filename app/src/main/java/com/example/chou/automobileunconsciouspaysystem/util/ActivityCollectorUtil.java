package com.example.chou.automobileunconsciouspaysystem.util;

import android.app.Activity;

import java.util.ArrayList;

public class ActivityCollectorUtil {
    public static ArrayList<Activity> mActivityList = new ArrayList<Activity>();
    public static int getSize(){
        return mActivityList.size();
    }

    public static void addActivity(Activity activity){
        if (!mActivityList.contains(activity)){
            mActivityList.add(activity);
        }
    }
    public static void removeActivity(Activity activity){
        mActivityList.remove(activity);
    }
    public static void finishAllActivity(){
        for (Activity activity : mActivityList){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
