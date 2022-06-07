package com.example.chou.automobileunconsciouspaysystem;

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

import com.example.chou.automobileunconsciouspaysystem.CarData.Car;
import com.example.chou.automobileunconsciouspaysystem.CarData.Log;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import java.util.ArrayList;

public class CarlogActivity extends AppCompatActivity {
    DBManager dbHelper;
    ArrayList<Log> logList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_log);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        ListView carloglist = (ListView) findViewById(R.id.carlogList);
        logList = new ArrayList<Log>();

        Intent fromIntent = getIntent();
        String comeUserCarId =fromIntent.getStringExtra("carid");//拿出传递来的username
        final String goUserCarId = comeUserCarId;


        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();//打开数据库

        final Cursor carQuery = db.rawQuery("select parkrecord.carid as _id,intime,outtime,parkname,carbrand ,state ,pay from parkrecord LEFT OUTER JOIN park on parkrecord.parkid = park.parkid LEFT OUTER JOIN car on parkrecord.carid = car.carid where parkrecord.carid = ?",new String[]{comeUserCarId});
        SimpleCursorAdapter simple = new SimpleCursorAdapter(this, R.layout.carlogitem,carQuery, new String[]{"_id","intime","outtime","parkname","carbrand","state","pay"},new int[]{R.id.logCarId,R.id.logInTime,R.id.logOutTime,R.id.logPark,R.id.logCarBoard,R.id.logState,R.id.logFare},0);

        carloglist.setAdapter(simple);

        Button billbn = (Button)findViewById(R.id.billButton);
        billbn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(CarlogActivity.this, BillActivity.class);
                intent.putExtra("carid",goUserCarId);//携带username
                tools.closeDb(dbHelper,db);
                startActivity(intent);
            }

        });


    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

}
