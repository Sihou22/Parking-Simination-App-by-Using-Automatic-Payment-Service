package com.example.chou.automobileunconsciouspaysystem;



import android.app.TabActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TabHost;
import android.content.Context;

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;
import com.example.chou.automobileunconsciouspaysystem.util.PhoneCodeUntil;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity  extends TabActivity {

    public DBManager dbHelper;
    private BaseActivity alert;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        final Context mContext;
       // if (savedInstanceState != null) {
        //    String oldString = savedInstanceState.getString("Activity");
        //}

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();

        mContext = this;
        String path = dbHelper.DB_PATH;

        final Timer timer = new Timer();

        TabHost tabHost = getTabHost();
        TabHost.TabSpec tab1 = tabHost.newTabSpec("tab1").setIndicator("密码登录").setContent(R.id.tab01);
        tabHost.addTab(tab1);
        TabHost.TabSpec tab2 = tabHost.newTabSpec("tab2").setIndicator("验证码登录").setContent(R.id.tab02);
        tabHost.addTab(tab2);


        final EditText phone = (EditText) findViewById(R.id.phoneEdit);
        final EditText phone2 = (EditText) findViewById(R.id.phoneEdit2);
        final EditText password = (EditText) findViewById(R.id.passwEdit);
        final EditText sendcode = (EditText) findViewById(R.id.sendCodeEdit);


        Button cbn = (Button)findViewById(R.id.CameraBn);

        cbn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(LoginActivity.this, CameraActivity.class);
                startActivity(intent);
            }

        });


        Button loginbn = (Button)findViewById(R.id.loginButton);//登陆按钮
        loginbn.setOnClickListener(new View.OnClickListener()//登陆监听
        {
            @Override
            public void onClick(View arg0) {


                String phonenum = phone.getText().toString();//获取输入的手机号和密码
                if(phonenum.length()<11)
                {alert.showError("请输入正确的手机号",mContext);}
                else{
                    String passW = password.getText().toString();
                    if(passW.equals(""))
                    {alert.showError("密码不能为空",mContext);}
                    else{
                        final Cursor cur = db.rawQuery("select * from user where phone = ? and password = ?",
                                new String[]{phonenum, passW});//匹配查询
                        if (cur != null && cur.getCount() >= 1) {//若有结果
                            alert.showDialogWithTitle("登录成功", mContext);
                            final TimerTask task = new TimerTask() {
                                @Override
                                public void run() {
                                    boolean b = cur.moveToNext();
                                    String username = cur.getString(cur.getColumnIndex("username"));
                                    db.close();
                                    dbHelper.closeDatabase();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);
                                    finish();
                                    }//转移activity
                                };
                                timer.schedule(task, 1000);
                        } else {
                            phone.setText("");
                            password.setText("");
                            alert.showError("您输入的信息有误，请重新确认",mContext);
                            }

                        }
                    }
                }
        });

        Button signbn = (Button)findViewById(R.id.SignBn);
        signbn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(LoginActivity.this, phoneInputActivity.class);
                intent.putExtra("function","用户注册");
                startActivity(intent);
            }

        });
        Button findbn = (Button)findViewById(R.id.findPwbn);
        findbn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(LoginActivity.this, phoneInputActivity.class);
                intent.putExtra("function","找回密码");
                startActivity(intent);
            }

        });


        Button sendCodebn = (Button)findViewById(R.id.sendCodeGet);
        final EditText sendCodeEdit = (EditText)findViewById(R.id.sendCodeEdit);
        final EditText phoneEdit2 = (EditText)findViewById(R.id.phoneEdit2);
        sendCodebn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                if (phoneEdit2.getText().toString().length() < 11) {
                    alert.showError("请输入正确的手机号", mContext);
                } else {

                    PhoneCodeUntil DoCode = new PhoneCodeUntil();
                    final String PhoneCode = DoCode.getRandonString(6);
                    final String title = "您收到了验证码";
                    final String context = "【无感支付停车场】您的验证码是" + PhoneCode + ",欢迎使用无感支付，请不要将验证码泄露给他人！";
                    //SmsManager smsManager=SmsManager.getDefault();  //获取系统短信管理器
                    //           List<String> list=smsManager.divideMessage(content);
                    //for(String l:list){  //如果短信超过70个字，则将短信内容拆分为几条发送
                    //   smsManager.sendTextMessage(phoneEdit2.getText().toString(), null, l, null, null);}
                    //alert.showSuccess("验证码已发送",mContext);
                    //获取通知管理器
                    Notify notify01 = new Notify(mContext);
                    notify01.sendNotify(title,context, mContext);

                    sendCodeEdit.setText(PhoneCode);

                }
            }
        });

        Button loginbn2 = (Button)findViewById(R.id.loginButton2);
        loginbn2.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(sendCodeEdit.getText().toString().length()<5)
                {alert.showError("请获取验证码",mContext);}
                else{
                    final Cursor cur = db.rawQuery("select * from user where phone = ?",
                            new String[]{phoneEdit2.getText().toString()});//匹配查询
                    if (cur != null && cur.getCount() >= 1) {//若有结果
                        alert.showDialogWithTitle("登录成功",mContext);
                        final TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                            boolean b = cur.moveToNext();
                            String username = cur.getString(cur.getColumnIndex("username"));
                            tools.closeDb(dbHelper,db);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", username);
                            startActivity(intent);
                            finish();}//转移activity
                        };
                        timer.schedule(task, 1000);
                    } else {
                        phone.setText("");
                        password.setText("");
                        alert.showError("您输入手机号不正确或未注册，请重新输入",mContext);
                    }

                }



            }

        });




}


}
