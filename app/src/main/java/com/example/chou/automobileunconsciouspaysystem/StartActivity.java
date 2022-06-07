package com.example.chou.automobileunconsciouspaysystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StartActivity extends AppCompatActivity implements PermissionInterface{

    private PermissionHelper mPermissionHelper;
    private int count = 5;
    TextView countDown;
    private int  MY_PERMISSIONS_WRITE_EXTERNAL_STORAGE;

    @BindView(R.id.appFunction1)
    Button parkFuntion;

    @BindView(R.id.appFunction2)
    Button userFuntion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        ButterKnife.bind(this);

        WindowManager windowManager = getWindowManager();

        Display display = windowManager.getDefaultDisplay();

        System.out.println(display.getWidth());
        System.out.println(display.getHeight());
        countDown = (TextView)findViewById(R.id.countDown);
        countDown.setVisibility(View.GONE);
        //初始化并发起权限申请
        mPermissionHelper = new PermissionHelper(this, this);
        mPermissionHelper.requestPermissions();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){
            //权限请求结果，并已经处理了该回调
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public int getPermissionsRequestCode() {
        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。
        return 10000;
    }

    @Override
    public String[] getPermissions() {
        //设置该界面所需的全部权限
        return new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE
        };
    }

    @Override
    public void requestPermissionsSuccess() {
        //权限请求用户已经全部允许
        initViews();
    }

    @Override
    public void requestPermissionsFail() {
        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。
        finish();
    }

    private void initViews(){
        //已经拥有所需权限，可以放心操作任何东西了
      /*  countDown.setVisibility(View.VISIBLE);
        handler.sendEmptyMessageDelayed(0, 1000);*/

    }

  /*  @SuppressLint("HandlerLeak")
 private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0) {
                count--;
                countDown.setText(String.valueOf(count));
                if(count==0)
                {
                    Intent intent = new Intent(StartActivity.this, LoginActivity.class);
                    startActivity(intent);}
                handler.sendEmptyMessageDelayed(0, 1000);
            }
        }

        ;

    };
*/
    @OnClick({R.id.appFunction1,R.id.appFunction2})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId())
        {
            case R.id.appFunction1:
                intent = new Intent(StartActivity.this, CameraActivity.class);
                startActivity(intent);
                break;
            case R.id.appFunction2:
                intent = new Intent(StartActivity.this, LoginActivity.class);
                startActivity(intent);
                break;
        }

    }


}





