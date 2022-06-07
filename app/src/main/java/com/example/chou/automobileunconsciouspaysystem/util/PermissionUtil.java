package com.example.chou.automobileunconsciouspaysystem.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import java.util.ArrayList;

public class PermissionUtil {

    /**
     * 判断是否有某个权限
     * @param context
     * @param permission
     * @return
     */
    public static boolean hasPermission(Context context, String permission){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(context.checkSelfPermission(permission)  != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }//如果还有权限，返回失败

    public static void requestPermissions(Activity activity, String[] permissions, int requestCode){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.requestPermissions(permissions, requestCode);
        }//弹窗群求权限，前接hp.requestPermissions()
    }


    public static String[] getDeniedPermissions(Context context, String[] permissions){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> deniedPermissionList = new ArrayList<>();
            for(String permission : permissions){
                if(context.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED){
                    deniedPermissionList.add(permission);
                }
            }
            int size = deniedPermissionList.size();
            if(size > 0){
                return deniedPermissionList.toArray(new String[deniedPermissionList.size()]);
            }
        }
        return null;//获取授权失败的权限，若不存在则返回null，前接hp.requestPermissions()
    }

}
