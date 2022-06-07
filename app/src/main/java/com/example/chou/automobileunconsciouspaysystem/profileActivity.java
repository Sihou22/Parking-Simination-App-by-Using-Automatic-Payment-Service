package com.example.chou.automobileunconsciouspaysystem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class profileActivity extends AppCompatActivity {


    public DBManager dbHelper;
    private String phoneNumber;
    //private Button carbn;
    private String goUserName;

    @BindView(R.id.carButton)
    Button carbn;

    @BindView(R.id.phoneButton)
    Button phonebn;

    @BindView(R.id.iDButton)
    Button idbn;

    @BindView(R.id.payButton)
    Button paybn;

    @BindView(R.id.changePwbutton)
    Button pwbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        ButterKnife.bind(this);

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();

        String nowid="";
        final TextView username = (TextView) findViewById(R.id.userName);
        Intent fromIntent = getIntent();
        String comeUserName = fromIntent.getStringExtra("username");
        goUserName = comeUserName;
        Cursor cur = db.rawQuery("select name ,phone,idnum,id,payway from user where username = ? ", new String[]{comeUserName});

        if (cur != null && cur.getCount() >= 1) {
            boolean b = cur.moveToNext();
            if (cur.getString(cur.getColumnIndex("name")) != null)
                comeUserName = cur.getString(cur.getColumnIndex("name"));
            phoneNumber = cur.getString(cur.getColumnIndex("phone"));
            nowid = cur.getString(cur.getColumnIndex("id"));
        }
        username.setText(comeUserName);

        //carbn = (Button) findViewById(R.id.carButton);
        Cursor carfind = db.rawQuery("select * from car where owner = ? ", new String[]{nowid});
        if (carfind != null && carfind.getCount() >= 1) {
            carbn.setText("已绑定车辆");
        }
        tools.closeDb(dbHelper,db);
       /* carbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(profileActivity.this, CarBindActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);

            }

        });*/

        //Button phonebn = (Button) findViewById(R.id.phoneButton);

        if (cur.getString(cur.getColumnIndex("phone")) != null) {
            phonebn.setText("已绑定电话号码");
        }


        //Button idbn = (Button) findViewById(R.id.iDButton);

        if (cur.getString(cur.getColumnIndex("idnum")) != null) {
            idbn.setText("已通过实名认证");
        }


        /*idbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(profileActivity.this, identityActivity.class);
                intent.putExtra("identify", phoneNumber);
                startActivity(intent);
            }

        });*/


        //Button paybn = (Button) findViewById(R.id.payButton);
        if (cur.getString(cur.getColumnIndex("payway")) != null) {
            String payway = "已绑定支付方式" + cur.getString(cur.getColumnIndex("payway"));
            paybn.setText(payway);
        }
        /*paybn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(profileActivity.this, payBindActivity.class);
                intent.putExtra("username", goUserName);
                startActivity(intent);
            }

        });*/



    }

    public void onBackPressed()
    {
        Intent intent = new Intent(profileActivity.this,MainActivity.class);
        intent.putExtra("username",goUserName);
        startActivity(intent);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

    @OnClick({R.id.carButton,R.id.phoneButton,R.id.iDButton,R.id.payButton,R.id.changePwbutton})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.carButton:
                intent = new Intent(profileActivity.this, CarBindActivity.class);
                intent.putExtra("phone", phoneNumber);
                startActivity(intent);
                break;
            case R.id.phoneButton:
                break;
            case R.id.iDButton:
                intent = new Intent(profileActivity.this, identityActivity.class);
                intent.putExtra("identify", phoneNumber);
                startActivity(intent);
                break;
            case R.id.payButton:
                intent = new Intent(profileActivity.this, payBindActivity.class);
                intent.putExtra("username", goUserName);
                startActivity(intent);
                break;
            case R.id.changePwbutton:
                intent = new Intent(profileActivity.this, phoneCodeActivity.class);
                intent.putExtra("function","修改密码");
                intent.putExtra("phone",phoneNumber);
                startActivity(intent);
        }
        }



}
