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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;
import com.example.chou.automobileunconsciouspaysystem.util.ActivityCollectorUtil;

import java.util.Timer;
import java.util.TimerTask;

public class DepositActivity extends AppCompatActivity {

    public DBManager dbHelper;
    Spinner spinner;
    private BaseActivity alert;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposit);
        final Context mContext = this;
        final Timer timer = new Timer();

        Intent intent = getIntent();
        username =(String) intent.getStringExtra("username");

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        final SQLiteDatabase db = dbHelper.getDatabase();


        spinner= (Spinner) findViewById(R.id.paySpinner);
        String[] arr={"支付宝","微信","银行卡"};
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line,arr);
        spinner.setAdapter(adapter);

        final EditText Money = (EditText)findViewById(R.id.moneyNumEdit);
        Button DespoitConfirmBn = (Button)findViewById(R.id.depositConfirmButton);

        DespoitConfirmBn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View arg0) {
                if (Money.getText().toString() == "") {
                    Money.setText("");
                    Money.setHint("请输入金额");
                } else {
                    Cursor query = db.rawQuery("select balance from user where username = ?", new String[]{username});

                    boolean b = query.moveToNext();
                    String now = Money.getText().toString();
                    now = now.substring(now.indexOf("￥")+1);
                    int money = Integer.parseInt(now);
                    int balance = query.getInt(query.getColumnIndex("balance")) + money;
                    ContentValues values = new ContentValues();
                    values.put("balance", balance);
                    long erroralert = db.update("user", values, "username=?", new String[]{username});
                    if (erroralert > -1) {
                        alert.showDialogWithTitle("充值中", mContext);
                        final TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("系统消息","充值成功", mContext);
                                Intent intent = new Intent(DepositActivity.this, PurseActivity.class);
                                intent.putExtra("username", username);
                                tools.closeDb(dbHelper,db);
                                startActivity(intent);
                                finish();
                            }
                        };
                        timer.schedule(task, 500);
                    }

                }
            }
        });

    }

    /*  public void onBackPressed()
    {
      Intent intent = new Intent(DepositActivity.this,PurseActivity.class);
        intent.putExtra("username",username);
        startActivity(intent);
        }*/

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        ActivityCollectorUtil.removeActivity(this);
    }

}
