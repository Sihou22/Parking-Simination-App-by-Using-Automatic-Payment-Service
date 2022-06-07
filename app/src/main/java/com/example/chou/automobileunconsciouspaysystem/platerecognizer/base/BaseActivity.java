package com.example.chou.automobileunconsciouspaysystem.platerecognizer.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.chou.automobileunconsciouspaysystem.R;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class BaseActivity extends AppCompatActivity {
    static SweetAlertDialog pDialog;

    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_left_in, R.anim.hold);

    }

    public static void cancel()
    {
        pDialog.cancel();
    }

    public static void showDialog(Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText("加载中");
        pDialog.show();
        pDialog.setCancelable(false);

    }


    public static void showDialogWithTitle(String c,Context context) {
        pDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE)
                .setTitleText(c);
        pDialog.show();
        pDialog.setCancelable(false);

    }

    public static void showError(String c,Context context){
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(c)
                .show();
    }
    public static void showSuccess(String c,Context context){
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("系统消息")
                .setContentText(c)
                .show();

    }

    public static void showWarming(String title,String text,Context context){
        new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(title)
                .setContentText(text)
                .setConfirmText("好的，我明白了")
                .show();
    }
    public void dimissDialog() {
        pDialog.dismissWithAnimation();
    }


    public void getTopBar(String arg0) {
//        TextView tvMid = (TextView) findViewById(R.id.tvMid);
//        tvMid.setText(arg0);
//
//        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });


    }
//
//    public void getTopBarNoBack(String arg0) {
//        TextView tvMid = (TextView) findViewById(R.id.tvMid);
//        tvMid.setText(arg0);
//
//        ImageView ivBack = (ImageView) findViewById(R.id.ivBack);
//        ivBack.setVisibility(View.GONE);
//
//
//    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
    }
}
