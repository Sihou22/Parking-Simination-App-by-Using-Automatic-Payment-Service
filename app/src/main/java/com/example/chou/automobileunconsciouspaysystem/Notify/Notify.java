package com.example.chou.automobileunconsciouspaysystem.Notify;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.example.chou.automobileunconsciouspaysystem.R;

public class Notify {
    private String channel_ID="001";
    private NotificationManager notifyManager;
    private NotificationChannel channel;
    public Notify(Context mcontext)
    {
        notifyManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        //实例化NotificationCompat.Builde并设置相关属性
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("001", "channel_name", NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notifyManager.createNotificationChannel(channel);}
    }

    public void setChannel_ID(String channel_ID) {
        this.channel_ID = channel_ID;
    }

    public String getChannel_ID() {
        return channel_ID;
    }

    public void sendNotify(String title,String text,Context mcontext)
    {


        NotificationCompat.Builder builder = new NotificationCompat.Builder(mcontext)
                //设置小图标
                .setSmallIcon(R.mipmap.ic_app_icon_round)
                //设置通知标题
                .setContentTitle(title)
                //设置通知内容
                .setContentText(text)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(text))

                ;

        builder.setChannelId("001");
        notifyManager.notify(1, builder.build());
        //通过builder.build()方法生成Notification对象,并发送通知,id=1
    }
}
