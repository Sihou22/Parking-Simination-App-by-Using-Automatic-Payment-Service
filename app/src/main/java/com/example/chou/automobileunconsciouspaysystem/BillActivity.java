package com.example.chou.automobileunconsciouspaysystem;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.example.chou.automobileunconsciouspaysystem.CarData.Log;
import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;
import com.example.chou.automobileunconsciouspaysystem.util.PhoneCodeUntil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class BillActivity extends AppCompatActivity {

    private BaseActivity alert;
    DBManager dbHelper;
    ArrayList<Log> billList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        final Context mContext = this;

        ListView billlist = (ListView) findViewById(R.id.billList);
        billList = new ArrayList<Log>();

        final Timer timer = new Timer();

        Intent fromIntent = getIntent();
        String comeUserCarId = fromIntent.getStringExtra("carid");//拿出传递来的username
        final String goUserCarId = comeUserCarId;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();//打开数据库

        final Cursor carQuery = db.rawQuery("select parkrecord.carid as _id,intime,outtime,parkname,carbrand,pay from parkrecord LEFT OUTER JOIN park on parkrecord.parkid = park.parkid LEFT OUTER JOIN car on parkrecord.carid = car.carid where parkrecord.carid = ?", new String[]{comeUserCarId});
        SimpleCursorAdapter simple = new SimpleCursorAdapter(this,
                R.layout.billitem, carQuery, new String[]{"_id", "intime", "outtime", "parkname", "carbrand","pay"}, new int[]{R.id.billCarId, R.id.billInTime, R.id.billOutTime, R.id.billPark, R.id.billCarBoard,R.id.billFare}, 0);
        billlist.setAdapter(simple);
        db.close();
        dbHelper.closeDatabase();
        Button printbn = (Button) findViewById(R.id.printButton);
        printbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                alert.showDialogWithTitle("正在为您生成打印码", mContext);
                final TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        alert.cancel();
                        PhoneCodeUntil DoCode = new PhoneCodeUntil();
                        String PhoneCode = DoCode.getRandonString(4);
                        Notify notify01 = new Notify(mContext);
                        notify01.sendNotify("系统消息", "打印码生成成功！您的打印码为："+PhoneCode+"请前往就近的打印点凭该码打印。请勿泄露给他人", mContext);
                        }//转移activity
                };
                timer.schedule(task, 500);
            } //转移activity
        });
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }
}
