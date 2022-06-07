package com.example.chou.automobileunconsciouspaysystem;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.util.PhoneCodeUntil;

public class phoneCodeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_code);

        final Context mContext = this;

        Intent comePhone = getIntent();
        final String phoneNumber =comePhone.getStringExtra("phone");
        final String function = comePhone.getStringExtra("function");

        TextView Codealert = (TextView)findViewById(R.id.SignUpCodeboard);
        Codealert.setText(function);

        Button bn = (Button)findViewById(R.id.confirmButton);
        final EditText phonecode = (EditText)findViewById(R.id.phoneCodeEdit);

        PhoneCodeUntil DoCode = new PhoneCodeUntil();
        String PhoneCode = DoCode.getRandonString(6);
        final String context = "【无感支付停车场】您的验证码是" + PhoneCode + ",欢迎使用无感支付，请不要将验证码泄露给他人！";
        final String title = "您收到了验证码";
        Notify notify01 = new Notify(mContext);
        notify01.sendNotify(title,context, mContext);
        phonecode.setText(PhoneCode);
        bn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(phonecode.getText()!=null)
                {
                    Intent intent = new Intent(phoneCodeActivity.this, PasswordActivity.class);
                    intent.putExtra("phone",phoneNumber);
                    intent.putExtra("function",function);
                    startActivity(intent);
                }

            }

        });


    }
}
