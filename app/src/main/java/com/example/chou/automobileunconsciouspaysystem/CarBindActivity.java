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

public class CarBindActivity extends AppCompatActivity {

    public DBManager dbHelper;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_bind);

        ActivityCollectorUtil.addActivity(this);//该Activity启动时添加到已启动Activity列表

        final Context mContext = this;
        Intent intent = getIntent();
        final String PhoneNum =(String) intent.getStringExtra("phone");

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        db = dbHelper.getDatabase();

        final EditText carNum = (EditText)findViewById(R.id.carNumEdit);
        final EditText carBoard = (EditText)findViewById(R.id.carbandEdit);
        Button carBindBn = (Button)findViewById(R.id.carBind);

        carBindBn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0)
            {
                if(carNum.getText().toString().length()!=7)
                {
                    carNum.setText("");
                    carNum.setHint("请输入正确车牌号");
                }else
                {
                    Cursor query = db.rawQuery("select username ,id ,balance from user where phone = ?",new String[]{PhoneNum});
                    boolean b=query.moveToNext();
                    String carBrand = carBoard.getText().toString();
                    String carId=carNum.getText().toString();
                    int balance = query.getInt(query.getColumnIndex("balance"));
                    ContentValues values=new ContentValues();
                    values.put("carid",carId);
                    Cursor carstate = db.rawQuery("select * from parkrecord where carid = ? and outtime is null",new String[]{carId});
                    if(carstate!=null && carstate.getCount() >= 1)
                    {values.put("parkstate","1");}
                    else{values.put("parkstate","0");}
                    values.put("owner",query.getString(query.getColumnIndex("id")));
                    if(carBrand.length()>1)
                    {values.put("carbrand",carBrand);}

                    long erroralert = db.insert("car",null,values);
                    Notify notifycar=new Notify(mContext);
                    if(erroralert!=-1)
                    {
                        String exinform = "";
                        Cursor fare = db.rawQuery("select pay from parkrecord where state= ? and outtime not null and carid = ?",new String[]{"待支付",carId});
                        if (fare.moveToFirst()) {
                            do {
                                balance-=fare.getInt(fare.getColumnIndex("pay"));
                            } while (fare.moveToNext());
                            ContentValues money=new ContentValues();
                            money.put("balance",balance);
                            long moneyupalert = db.update("user", money, "phone=?", new String[]{PhoneNum});
                            if(moneyupalert!=-1)
                            {
                                ContentValues state=new ContentValues();
                                state.put("state","已支付");
                                long stateupalert=db.update("parkrecord",state,"carid=?",new String[]{carId});
                                if(stateupalert!=-1)
                                    exinform="。由于此车辆在绑定之前已有未缴费停车记录，未缴费款已从您的余额中扣除。";
                            }
                        }

                        notifycar.sendNotify("车辆绑定成功","您已成功在您的户下绑定车辆"+carId+exinform, mContext);
                        Intent intent = new Intent(CarBindActivity.this, profileActivity.class);
                        intent.putExtra("username",query.getString(query.getColumnIndex("username")));
                        startActivity(intent);
                        finish();}else if(carBrand.length()>1)
                    {
                        ContentValues n=new ContentValues();
                        n.put("carbrand",carBrand);
                        long uperroralert = db.update("car", n, "carid=?", new String[]{carId});
                        if(uperroralert!=-1)
                        {
                            notifycar.sendNotify("车辆信息修改成功","您已成功将您的车辆"+carId+"的品牌信息修改为"+carBrand, mContext);
                            Intent intent = new Intent(CarBindActivity.this, profileActivity.class);
                            intent.putExtra("username",query.getString(query.getColumnIndex("username")));
                            startActivity(intent);
                            finish();
                        }
                    }

                        else{

                        carNum.setText("");
                        carNum.setHint("该车已被绑定");
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
        tools.closeDb(dbHelper,db);
    }

}
