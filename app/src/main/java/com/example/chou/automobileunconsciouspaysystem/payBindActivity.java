package com.example.chou.automobileunconsciouspaysystem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import butterknife.BindView;


public class payBindActivity extends AppCompatActivity {

    private ImageView mIvWechatSelect;
    private ImageView mIvAliSelect;
    private ImageView mIvCardSelect;

    private static final int PAY_WAY_WECHAT = 0;  //微信支付,默认支付方式
    private static final int PAY_WAY_ALIBABA = 1;  //支付宝支付
    private static final int PAY_WAY_CARD = 2;  //银行卡支付
    private int payWay = 0;
    public DBManager dbHelper;
    private Button payway ;
    private Button payWaybt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bind);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();
        final Context mContext = this;
        payway = findViewById(R.id.payWayView);
        payWaybt = findViewById(R.id.payWaybt);
        Intent intent = getIntent();
        final String username =(String) intent.getStringExtra("username");
        Cursor query = db.rawQuery("select payway from user where username = ?",new String[]{username});
        boolean b=query.moveToNext();
        String nowPayWay = query.getString(query.getColumnIndex("payway"));
        if(nowPayWay!=null)
        {
           if(nowPayWay.equals("alipay"))
           nowPayWay="支付宝";
           else if(nowPayWay.equals("wechat"))
               nowPayWay="微信";
           else if(nowPayWay.equals("card"))
               nowPayWay="银行卡";

        }else
            nowPayWay="请点击选择支付方式";
        payway.setText(nowPayWay);



        payway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectway();
            }
        });
        payWaybt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues values=new ContentValues();
                String wayName = "";
                switch(payWay){
                    case PAY_WAY_WECHAT:values.put("payway","wechat");wayName="微信";break;
                    case PAY_WAY_ALIBABA:values.put("payway","alipay");wayName="支付宝";break;
                    case PAY_WAY_CARD:values.put("payway","card");wayName="银联卡";break;
                }
                Notify notifypay=new Notify(mContext);
                long erroralert = db.update("user", values, "username=?", new String[]{username});
                if(erroralert!=-1)
                {
                    notifypay.sendNotify("支付方式绑定成功","您已成功将您的支付方式绑定为"+wayName, mContext);
                    Intent intent = new Intent(payBindActivity.this, profileActivity.class);
                    intent.putExtra("username",username);
                    tools.closeDb(dbHelper,db);
                    startActivity(intent);
                    finish();

                }

            }
        });
    }
    public void selectway() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_pay_type, null);
        //微信支付的选择
        mIvWechatSelect = dialogView.findViewById(R.id.iv_buy_wechat_select);
        //支付宝的选择
        mIvAliSelect = dialogView.findViewById(R.id.iv_buy_alipay_select);

        mIvCardSelect = dialogView.findViewById(R.id.iv_buy_card_select);



        PayBottomDialog dialog = new PayBottomDialog(payBindActivity.this, dialogView, new int[]{R.id.ll_pay_weichat, R.id.ll_pay_ali, R.id.ll_pay_card,R.id.tv_confirm, R.id.tv_cancel});
        dialog.bottmShow();
        dialog.setOnBottomItemClickListener(new PayBottomDialog.OnBottomItemClickListener() {
            @Override
            public void onBottomItemClick(PayBottomDialog dialog, View view) {
                switch (view.getId()) {
                    case R.id.ll_pay_weichat:   //微信支付
                        showToast("微信");
                        if (PAY_WAY_WECHAT != payWay) {
                            mIvWechatSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_selected));
                            mIvAliSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            mIvCardSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            payWay = PAY_WAY_WECHAT;
                            payway.setText("微信");
                        }

                        break;
                    case R.id.ll_pay_ali:  //支付宝支付
                        showToast("支付宝");
                        if (PAY_WAY_ALIBABA != payWay) {
                            mIvWechatSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            mIvAliSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_selected));
                            mIvCardSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            payWay = PAY_WAY_ALIBABA;
                            payway.setText("支付宝");
                        }
                        break;

                    case R.id.ll_pay_card:  //银行卡支付
                        showToast("银行卡");
                        if (PAY_WAY_CARD != payWay) {
                            mIvWechatSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            mIvAliSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_un_selected));
                            mIvCardSelect.setImageDrawable(getResources().getDrawable(R.mipmap.ic_selected));
                            payWay = PAY_WAY_CARD;
                            payway.setText("银行卡");
                        }
                        break;
                    case R.id.tv_confirm:  //确认支付
                        //TODO 支付
                        showToast("确认");
                        dialog.cancel();
                        break;
                    case R.id.tv_cancel:  //取消支付
                        showToast("取消");
                        //重置
                        payWay = PAY_WAY_WECHAT;
                        dialog.cancel();
                        break;
                }
            }
        });
    }

    private void showToast(String s){
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

}
