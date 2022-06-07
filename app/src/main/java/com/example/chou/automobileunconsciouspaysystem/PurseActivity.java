package com.example.chou.automobileunconsciouspaysystem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

public class PurseActivity extends AppCompatActivity {
    public DBManager dbHelper;
    private String username;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purse);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        Intent intent = getIntent();
        username =(String) intent.getStringExtra("username");

        TextView money =(TextView)findViewById(R.id.moneyNum);
        Button Confirm =(Button)findViewById(R.id.depositbutton) ;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        db = dbHelper.getDatabase();

        Cursor query = db.rawQuery("select balance from user where username = ?",new String[]{username});

        String balance="0";
        if(query !=null && query .getCount() >= 1)
        {boolean b=query.moveToNext();
        balance = String.valueOf(query.getInt(query.getColumnIndex("balance"))+0);}
        money.setText(balance);

        Confirm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(PurseActivity.this, DepositActivity.class);
                intent.putExtra("username",username);
                tools.closeDb(dbHelper,db);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(PurseActivity.this,MainActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        tools.closeDb(dbHelper,db);
        ActivityCollectorUtil.removeActivity(this);
    }

}
