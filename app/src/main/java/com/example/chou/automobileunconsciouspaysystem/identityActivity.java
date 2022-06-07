package com.example.chou.automobileunconsciouspaysystem;

import android.content.ContentValues;
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

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;
import com.example.chou.automobileunconsciouspaysystem.util.PhoneCodeUntil;

public class identityActivity extends AppCompatActivity {

    public DBManager dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        Intent intent = getIntent();
        final String identifyPhone =(String) intent.getStringExtra("identify");

        final Context mContext = this;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();

        final EditText phone = (EditText)findViewById(R.id.identifyphoneEdit);
        final EditText sendCode = (EditText)findViewById(R.id.identifysendCodeEdit);
        final EditText idCard = (EditText)findViewById(R.id.identifyIdEdit);
        final EditText name = (EditText)findViewById(R.id.identifyNameEdit);
        Button sendCodeBn = (Button)findViewById(R.id.identifysendCodeGet);
        Button confirmBn = (Button)findViewById(R.id.identifyButton);
        idCard.setVisibility(View.INVISIBLE);
        name.setVisibility(View.INVISIBLE);
        phone.setText(identifyPhone);

        sendCodeBn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                PhoneCodeUntil DoCode = new PhoneCodeUntil();
                String PhoneCode = DoCode.getRandonString(6);
                sendCode.setText(PhoneCode);
                idCard.setVisibility(View.VISIBLE);
                name.setVisibility(View.VISIBLE);
            }
        });

        confirmBn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                String nowIdCard =idCard.getText().toString();
                String nowName = name.getText().toString();
                int idLength =nowIdCard.length();
                if(idLength!=18&&idLength!=15)
                {
                    idCard.setText("");
                    idCard.setHint("请输入正确的身份证号！当前输入的号码位数为："+idLength);
                }else
                {
                   if(nowName=="")
                   {
                       name.setText("");
                       name.setHint("请输入正确的姓名");
                   }
                   else
                   {
                       ContentValues values=new ContentValues();
                       values.put("name",nowName);
                       values.put("idnum",nowIdCard);
                       long erroralert = db.update("user",values,"phone=?",new String[]{identifyPhone});
                       Notify notify=new Notify(mContext);
                       notify.sendNotify("身份验证成功","您已成功绑定您的身份", mContext);
                       Cursor query = db.rawQuery("select username from user where phone = ?",new String[]{identifyPhone});
                       boolean b=query.moveToNext();
                       Intent intent = new Intent(identityActivity.this, profileActivity.class);
                       intent.putExtra("username",query.getString(query.getColumnIndex("username")));
                       tools.closeDb(dbHelper,db);
                       startActivity(intent);

                   }
                }
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
