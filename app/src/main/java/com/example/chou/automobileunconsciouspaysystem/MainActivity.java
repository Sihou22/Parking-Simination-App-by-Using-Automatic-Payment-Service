package com.example.chou.automobileunconsciouspaysystem;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    public DBManager dbHelper;
    private String goUsername;
    private String userId;
    private FragmentManager manager = getSupportFragmentManager();
    private long firstTime;

    @BindView(R.id.pursezone)
    View pursezone;

    @BindView(R.id.carzone)
    View carzone;

    @BindView(R.id.paycheck)//检测支付方式
    View paycheck;

    @BindView(R.id.profile)//用户资料按钮
    Button profilebn;

    @BindView(R.id.logoutbutton)
    Button logoutBn;

    @BindView(R.id.moneyNum)
    TextView Balance;

    @BindView(R.id.userView)
    TextView username;

    @BindView(R.id.carNumView)
    TextView carnum;

    @BindView(R.id.payWayView)
    TextView payway;

    @BindView(R.id.purseAlert)
    TextView pursealert;

    @BindView(R.id.parkNumView)
    TextView parknum;

    @BindView(R.id.billNumView)
    TextView billnum;

    @BindView(R.id.orderstate)
    View orderzone;

    @BindView(R.id.nowOrder)
    TextView orderstate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCollectorUtil.addActivity(this);

        ButterKnife.bind(this);

        //final TextView Balance = (TextView) findViewById(R.id.moneyNum);
        //final TextView username = (TextView) findViewById(R.id.userView);

       // final View paycheck = (View)findViewById(R.id.paycheck);
        //final View pursezone = (View)findViewById(R.id.pursezone);
        //final View carzone = (View)findViewById(R.id.carzone);

        //final TextView carnum =(TextView)findViewById(R.id.carNumView);
        //final TextView payway =(TextView)findViewById(R.id.payWayView);

        Intent fromIntent = getIntent();
        String comeUsername =fromIntent.getStringExtra("username");//拿出传递来的username
        goUsername = comeUsername;

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();//打开数据库

        final Cursor cur = db.rawQuery("select name,balance,id,payway from user where username = ? ", new String[]{comeUsername});
        if(cur!=null && cur.getCount() >= 1)//如果查询到的信息不为空
        {
            boolean b=cur.moveToNext();//开始查询
            if(cur.getString(cur.getColumnIndex("name"))!=null)//如果该user有姓名
            comeUsername = cur.getString(cur.getColumnIndex("name"));//将comeUsername设为姓名
        }

        username.setText("欢迎回来！"+comeUsername);//将username栏文字设为comeUsername

        userId=cur.getString(cur.getColumnIndex("id"));

        //TextView pursealert = (TextView)findViewById(R.id.purseAlert);
        int balance =cur.getInt(cur.getColumnIndex("balance"));
        Balance.setText(String.valueOf(balance));//显示余额
        if(balance>=0)//如果有余额
        {
            pursealert.setVisibility(View.GONE);
        } else
        {
            pursealert.setVisibility(View.VISIBLE);
        }
//-----------------支付方式检测区域
        String payWay =cur.getString(cur.getColumnIndex("payway"));
        if(payWay!=null)
        {
            if(payWay.equals("alipay"))
            {payWay="支付宝";}
            else if(payWay.equals("wechat"))
            {payWay="微信";}
            else {payWay="银行卡";}
            payway.setText(payWay);
            paycheck.setVisibility(View.GONE);

        }else { payway.setText("无"); paycheck.setVisibility(View.VISIBLE);}

        /*paycheck.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(MainActivity.this, payBindActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
            }

        });*/

//-----------------已绑定车辆检测区域
        String userid = cur.getString(cur.getColumnIndex("id"));
        if(userid!=null)//如果有用户id
        {
            Cursor carstatic = db.rawQuery("SELECT count (*) from car where owner = ?",new String[]{userid});//查询用户车辆
            carstatic.moveToFirst();
            int num = carstatic.getInt(0);//获取车辆数量
            String numView;
            if(num<10)//如果数量低于10
            {numView = "0"+String.valueOf(num);}//+0
           else {numView = String.valueOf(num);}
            carnum.setText(numView);//显示车辆数量

        }

        //Button profilebn = (Button)findViewById(R.id.profile);//用户资料按钮
        /*profilebn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                Intent intent = new Intent(MainActivity.this, profileActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
            }

        });/*

        pursezone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {//点击钱包区域时，转移到钱包
                Intent intent = new Intent(MainActivity.this, PurseActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
            }

        });

       /* carzone.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {//点击车辆区域
                Intent intent = new Intent(MainActivity.this, carActivity.class);
                intent.putExtra("id",cur.getString(cur.getColumnIndex("id")));//携带username
                startActivity(intent);
            }

        });*/
//-----------------停车次数检测区域
       // TextView parknum = (TextView)findViewById(R.id.parkNumView);
       // TextView billnum = (TextView)findViewById(R.id.billNumView);
        Cursor parkstatic = db.rawQuery("SELECT count (*) from parkrecord LEFT OUTER JOIN car ON parkrecord.carid = car.carid where owner = ?",new String[]{userid});//查询用户车辆
        parkstatic.moveToFirst();
        int num = parkstatic.getInt(0);//获取车次数
        String numView;
        if(num<10)//如果数量低于10
        {numView = "0"+String.valueOf(num);}//+0
        else {numView = String.valueOf(num);}
        parknum.setText(numView);//显示停车次数
        billnum.setText(numView);

//-----------------上一个/当前订单检测区域
        Cursor order = db.rawQuery("select pr.carid,parkname,intime from parkrecord pr  LEFT OUTER JOIN car c on pr.carid = c.carid LEFT OUTER JOIN park p on pr.parkid=p.parkid where owner = ? and outtime is null order by recordid DESC limit 1" ,new String[]{userid});
        if(order!=null && order.getCount() >= 1)//如果查询到的信息不为空
        {
            //View orderzone = (View)findViewById(R.id.orderstate);
            orderzone.setVisibility(View.VISIBLE);
            boolean b = order.moveToNext();//开始查询
            TextView ordercar = (TextView)findViewById(R.id.nowStateCarNum);
            ordercar.setText(order.getString(order.getColumnIndex("carid")));
            TextView orderintime = (TextView)findViewById(R.id.nowStateInTime);
            orderintime.setText(order.getString(order.getColumnIndex("intime")));
            TextView orderpark = (TextView)findViewById(R.id.nowStateParkId);
            orderpark.setText(order.getString(order.getColumnIndex("parkname")));
        }else
        {
            order = db.rawQuery("select pr.carid,parkname,intime,outtime from parkrecord pr  LEFT OUTER JOIN car c on pr.carid = c.carid LEFT OUTER JOIN park p on pr.parkid=p.parkid where owner = ?  order by recordid DESC limit 1" ,new String[]{userid});
            //View orderzone = (View)findViewById(R.id.orderstate);
            //TextView orderstate =(TextView)findViewById(R.id.nowOrder);
            if(order!=null && order.getCount() >= 1)//如果查询到的信息不为空
            {boolean b = order.moveToNext();orderstate.setText("您的上一件订单");
            orderzone.setVisibility(View.VISIBLE);
            TextView ordercar = (TextView)findViewById(R.id.nowStateCarNum);
            ordercar.setText(order.getString(order.getColumnIndex("carid")));
            TextView orderintime = (TextView)findViewById(R.id.nowStateInTime);
            orderintime.setText(order.getString(order.getColumnIndex("intime")));
            TextView orderpark = (TextView)findViewById(R.id.nowStateParkId);
            orderpark.setText(order.getString(order.getColumnIndex("parkname")));
            TextView orderouttime = (TextView)findViewById(R.id.nowStateOutTime);
            orderouttime.setText(order.getString(order.getColumnIndex("outtime")));
            View orderoutzone = (View)findViewById(R.id.orderoutzone);
            orderoutzone.setVisibility(View.VISIBLE);}
        }



        tools.closeDb(dbHelper,db);

        //Button logoutBn =(Button)findViewById(R.id.logoutbutton);
       /* logoutBn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {//登出
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                ActivityCollectorUtil.finishAllActivity();
                Toast.makeText(MainActivity.this, "已登出账号", Toast.LENGTH_SHORT).show();
            }

        });*/


    }



    @Override
    public void onBackPressed()
    {

    }



    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){// 点击了返回按键
            if(manager.getBackStackEntryCount() != 0){
                manager.popBackStack();
            }else {
                exitApp(2000);// 退出应用
            }
            return true;// 返回true，防止该事件继续向下传播
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 退出应用
     * @param timeInterval 设置第二次点击退出的时间间隔
     */
    private void exitApp(long timeInterval) {
        // 第一次肯定会进入到if判断里面，然后把firstTime重新赋值当前的系统时间
        // 然后点击第二次的时候，当点击间隔时间小于2s，那么退出应用；反之不退出应用
        if(System.currentTimeMillis() - firstTime >= timeInterval){
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            firstTime = System.currentTimeMillis();
        }else {
            ActivityCollectorUtil.finishAllActivity();
            System.exit(0);// 完全退出应用
        }
    }
    @OnClick({R.id.pursezone,R.id.carzone,R.id.paycheck,R.id.profile,R.id.logoutbutton})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.pursezone:
                intent = new Intent(MainActivity.this, PurseActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
                break;
            case R.id.carzone:
                intent = new Intent(MainActivity.this, carActivity.class);
                intent.putExtra("id",userId);//携带username
                startActivity(intent);
                break;
            case R.id.paycheck:
                intent = new Intent(MainActivity.this, payBindActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
                break;
            case R.id.profile:
                intent = new Intent(MainActivity.this, profileActivity.class);
                intent.putExtra("username",goUsername);//携带username
                startActivity(intent);
                break;
            case R.id.logoutbutton:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                ActivityCollectorUtil.finishAllActivity();
                Toast.makeText(MainActivity.this, "已登出账号", Toast.LENGTH_SHORT).show();
                break;
        }
    }




}
