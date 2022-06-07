package com.example.chou.automobileunconsciouspaysystem;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.CarData.Car;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import java.util.ArrayList;
import java.util.List;

public class carActivity extends AppCompatActivity {

    DBManager dbHelper;
    ArrayList<Car> carList;


    private class car{
        String carid;
        String brand;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        ListView carlist = (ListView) findViewById(R.id.carList);
        carList = new ArrayList<Car>();

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        Intent fromIntent = getIntent();
        String comeUserId =fromIntent.getStringExtra("id");//拿出传递来的username
        final String goUserId = comeUserId;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();//打开数据库

        final Cursor carQuery = db.rawQuery("select carid as _id,carbrand from car where owner = ?",new String[]{comeUserId});

        SimpleCursorAdapter simple = new SimpleCursorAdapter(this,
                R.layout.caritem,carQuery, new String[]{"_id","carbrand"},new int[]{R.id.carItemNum,R.id.carItembrand},0);
        carlist.setAdapter(simple);

        tools.closeDb(dbHelper,db);
        carlist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                Intent intent = new Intent(carActivity.this, CarlogActivity.class);
                String text = (String) ((TextView)arg1.findViewById(R.id.carItemNum)).getText();
                intent.putExtra("carid", text);
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


