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
import android.widget.EditText;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;

public class phoneInputActivity extends AppCompatActivity {

    private BaseActivity alert;
    public DBManager dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);

        boolean state = false;
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();
        Intent comePhone = getIntent();
        final String function = comePhone.getStringExtra("function");

        TextView signupalert = (TextView)findViewById(R.id.SignUpboard);
        signupalert.setText(function);

        final Context mContext = this;
        Button bn = (Button)findViewById(R.id.getSendCode);
        final EditText phonenum = (EditText)findViewById(R.id.phoneEdit);
        bn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                if (phonenum.getText().toString().length() < 11) {
                    alert.showError("请输入正确的手机号", mContext);
                    phonenum.setText("");
                } else {
                    final Cursor cur = db.rawQuery("select * from user where phone = ? ",
                            new String[]{phonenum.getText().toString()});//匹配查询
                    if (cur != null && cur.getCount() >= 1) {//若有结果
                        if(function.equals("用户注册"))
                        {
                            alert.showError("该手机号已被注册，请重新输入", mContext);
                            phonenum.setText("");
                        }else
                        {
                            Intent intent = new Intent(phoneInputActivity.this, phoneCodeActivity.class);
                            String phone = phonenum.getText().toString();
                            intent.putExtra("phone", phone);
                            intent.putExtra("function",function);
                            db.close();
                            dbHelper.closeDatabase();
                            startActivity(intent);
                        }
                    } else {
                        if(function.equals("用户注册"))
                        {
                            Intent intent = new Intent(phoneInputActivity.this, phoneCodeActivity.class);
                            String phone = phonenum.getText().toString();
                            intent.putExtra("phone", phone);
                            intent.putExtra("function",function);
                            db.close();
                            dbHelper.closeDatabase();
                            startActivity(intent);}
                        else{
                            alert.showError("该手机号还没被注册，请重新输入", mContext);
                            phonenum.setText("");
                        }
                    }
                }
            }
        });
    }

}
