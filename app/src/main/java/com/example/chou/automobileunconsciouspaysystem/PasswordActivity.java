package com.example.chou.automobileunconsciouspaysystem;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import java.util.Timer;
import java.util.TimerTask;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class PasswordActivity extends AppCompatActivity {

    private BaseActivity alert;
    public DBManager dbHelper;
    private SQLiteDatabase db;
    private int id=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        final Context mContext = this;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        db = dbHelper.getDatabase();

        final Activity mactivity = this;
        Button bn = (Button)findViewById(R.id.signupButton);
        final EditText pw = (EditText)findViewById(R.id.passWEdit);
        final EditText pwr = (EditText)findViewById(R.id.passWREdit);
        Intent comeIntent = getIntent();
        final String phoneNumber = comeIntent.getStringExtra("phone");
        final String function = comeIntent.getStringExtra("function");
        final Timer timer = new Timer();

        TextView Passwordalert = (TextView)findViewById(R.id.Passwordboard);
        Passwordalert.setText(function);

        if(function.equals("用户注册"))
        {
        Cursor nowid = db.rawQuery("select id from user order by id desc LIMIT 1",null);
        boolean b=nowid.moveToNext();
        id = nowid.getInt(nowid.getColumnIndex("id"))+1;
        }else bn.setText("确定修改");

        bn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(pw.getText().toString().length()>=8)
                {
                        if(pw.getText().toString().equalsIgnoreCase(pwr.getText().toString()))
                        {
                            if(function.equals("用户注册"))
                            {
                            ContentValues values=new ContentValues();
                            values.put("id",id);
                            values.put("phone",phoneNumber);
                            values.put("password",pw.getText().toString());
                            values.put("username","user"+phoneNumber);
                            long erroralert = db.insert("user",null,values);
                            if(erroralert!=-1)
                            {
                                alert.showDialogWithTitle("注册中",mContext);
                                final TimerTask task = new TimerTask() {
                                    @Override
                                    public void run() {Notify notify01 = new Notify(mContext);
                                        notify01.sendNotify("系统消息", "注册成功",mContext);}//转移activity
                                };
                                timer.schedule(task, 300);
                                Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }

                            }else{
                                ContentValues values=new ContentValues();
                                values.put("password",pw.getText().toString());
                                long erroralert = db.update("user", values, "phone=?", new String[]{phoneNumber});
                                if(erroralert!=-1)
                                {
                                    new SweetAlertDialog(mContext, SweetAlertDialog.SUCCESS_TYPE)
                                            .setTitleText("系统提示")
                                            .setContentText("密码修改成功")
                                            .setConfirmText("确定")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {
                                                    Intent intent = new Intent(PasswordActivity.this, LoginActivity.class);
                                                    if(function.equals("修改密码"))
                                                    {startActivity(intent);
                                                        sDialog.cancel();
                                                        ActivityCollectorUtil.removeActivity(mactivity);
                                                        finish();}
                                                    else {
                                                    startActivity(intent);
                                                    sDialog.cancel();
                                                    finish();}
                                                }
                                            })
                                            .show();
                                }
                            }
                        }else {pwr.setText("");alert.showError("输入的密码不一致",mContext);}
                }else {pw.setText("");alert.showError("密码格式错误或为空，密码最小长度为8位",mContext);}
            }

        });
    }
    public void onDestroy()
    {
        super.onDestroy();
        tools.closeDb(dbHelper,db);
    }

}
