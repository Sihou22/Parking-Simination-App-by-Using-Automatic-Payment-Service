package com.example.chou.automobileunconsciouspaysystem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.chou.automobileunconsciouspaysystem.PR.PlateRecognizer;
import com.example.chou.automobileunconsciouspaysystem.Notify.Notify;
import com.example.chou.automobileunconsciouspaysystem.platerecognizer.base.BaseActivity;
import com.example.chou.automobileunconsciouspaysystem.util.BitmapUtil;
import com.example.chou.automobileunconsciouspaysystem.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class CameraActivity extends BaseActivity implements SurfaceHolder.Callback{

    private Thread thread;
    public DBManager dbHelper;
    private SQLiteDatabase db;
    private BaseActivity alert;
    private Context mContext;
    private Camera.AutoFocusCallback myAutoFocusCallback = null;
    private String plate;

    @BindView(R.id.svCamera)
    SurfaceView mSvCamera;

    @BindView(R.id.ivPlateRect)
    ImageView PlateRectZone;

    @BindView(R.id.ivCapturePhoto)
    ImageView CapturePhotoBtn;

    @BindView(R.id.tvPlateResult)
    TextView PlateResultView;

    @BindView(R.id.parkSelectButton1)
    ToggleButton parkSelectButton1;

    @BindView(R.id.parkSelectButton2)
    ToggleButton parkSelectButton2;

    @BindView(R.id.parkSelectButton3)
    ToggleButton parkSelectButton3;

    @BindView(R.id.nowParkView)
    TextView nowParkView;

    @BindView(R.id.correctbn)
    Button correctbn;

    @BindView(R.id.recheckbn)
    Button recheckbn;

    @BindView(R.id.flashButton)
    ToggleButton flashButton;

    @BindView(R.id.ReMoButton)
    ToggleButton ReMoButton;

    private static final String TAG = CameraActivity.class.getSimpleName();

    private int cameraPosition = 0; // 0表示后置，1表示前置

    private SurfaceHolder mSvHolder;//SurfaceView选择实例
    private Camera mCamera;//摄像头
    private Camera.CameraInfo mCameraInfo;//摄像头信息实例
    private MediaPlayer mShootMP;
    private PlateRecognizer mPlateRecognizer;
    private int parkId =101;
    private String parkname="朝阳停车场";
    private String imagePath="";
    private String imagePathR="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        ButterKnife.bind(this);

        mPlateRecognizer = new PlateRecognizer(this);

        initData();

        mContext = this;

        BaseActivity.showWarming("系统提示","请尽量保证场景光线充足且车牌进入识别区域",mContext);

        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        db = dbHelper.getDatabase();

        parkSelectButton1.setChecked(true);
        parkSelectButton2.setChecked(false);
        parkSelectButton3.setChecked(false);
        nowParkView.setText(parkname);

        thread = new Thread(new ScanThread());

        parkSelectButton1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (parkSelectButton1.isChecked()) {
                    parkSelectButton2.setChecked(false);
                    parkSelectButton3.setChecked(false);
                    parkId = 101;
                    parkname = "朝阳停车场";
                    nowParkView.setText(parkname);
                    setToast("已进入"+parkname);
                }
            }
        });
        parkSelectButton2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (parkSelectButton2.isChecked()) {
                    parkSelectButton1.setChecked(false);
                    parkSelectButton3.setChecked(false);
                    parkId = 102;
                    parkname = "阳光海岸停车场";
                    nowParkView.setText(parkname);
                    setToast("已进入"+parkname);
                }
            }
        });
        parkSelectButton3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (parkSelectButton3.isChecked()) {
                    parkSelectButton1.setChecked(false);
                    parkSelectButton2.setChecked(false);
                    parkId = 103;
                    parkname = "京西停车场";
                    nowParkView.setText(parkname);
                    setToast("已进入"+parkname);
                }
            }
        });

        flashButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (!flashButton.isChecked()) {
                       turnLightOff();
                        setToast("闪光灯已关闭");
                    }else
                    {
                       turnLightOn();
                        setToast("闪光灯已打开");
                    }
            }
        });

        ReMoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (!ReMoButton.isChecked()) {
                    thread.interrupt();
                    setToast("实时识别已关闭");
                }else
                {
                    new SweetAlertDialog(mContext, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("系统提示")
                            .setContentText("实时识别仅用于测试，准确率可能低于拍照识别。使用拍照识别时请关闭实时识别。")
                            .setConfirmText("好的，我明白了")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    thread.start();
                                    setToast("实时识别已打开");
                                    sDialog.cancel();
                                }
                            })
                            .show();

                }
            }
        });


        recheckbn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PlateResultView.setText("拍照识别车牌");
                correctbn.setVisibility(View.GONE);
                recheckbn.setVisibility(View.GONE);
                deletePicture(imagePathR);
                deletePicture(imagePath);
                Toast.makeText(CameraActivity.this, "图像已删除。", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (this.checkCameraHardware(this) && (mCamera == null)) {
            // 打开camera
            mCamera = getCamera();
            // 设置camera方向
            mCameraInfo = getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK);//获取背部摄像头
            if (null != mCameraInfo) {
                adjustCameraOrientation();
            }

            if (mSvHolder != null) {
                setStartPreview(mCamera, mSvHolder);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        /**
         * 记得释放camera，方便其他应用调用
         */
        releaseCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    //初始化相关data
    private void initData() {
        // 获得句柄
        mSvHolder = mSvCamera.getHolder(); // 获得句柄SurfaceView
        // 添加回调
        mSvHolder.addCallback(this);//添加一个SurfaceHolder.Callback回调接口。
    }

    private Camera getCamera() {//获取相机
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            camera = null;
            Log.e(TAG, "Camera is not available (in use or does not exist)");
        }
        return camera;
    }

    private Camera.CameraInfo getCameraInfo(int facing) {
        int numberOfCameras = Camera.getNumberOfCameras();
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.getCameraInfo(i, cameraInfo);
            if (cameraInfo.facing == facing) {
                return cameraInfo;
            }
        }
        return null;
    }

    private void adjustCameraOrientation() { // 调整摄像头方向
        if (null == mCameraInfo || null == mCamera) {
            return;
        }

        int orientation = this.getWindowManager().getDefaultDisplay().getOrientation();
        int degrees = 0;

        switch (orientation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (mCameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (mCameraInfo.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else {
            // back-facing
            result = (mCameraInfo.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);
    }

    //释放mCamera

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();// 停掉原来摄像头的预览
            mCamera.release();
            mCamera = null;
        }
    }



    @OnClick({R.id.ivCapturePhoto,R.id.correctbn})
    public void onClick(View view) {
        switch (view.getId()) {
            case 999: // R.id.id_switch_camera_btn:
                // 切换前后摄像头
                int cameraCount = 0;
                Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
                cameraCount = Camera.getNumberOfCameras();// 得到摄像头的个数

                for (int i = 0; i < cameraCount; i++) {
                    Camera.getCameraInfo(i, cameraInfo);// 得到每一个摄像头的信息
                    if (cameraPosition == 1) {
                        // 现在是后置，变更为前置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            releaseCamera();
                            // 打开当前选中的摄像头
                            mCamera = Camera.open(i);
                            // 通过surfaceview显示取景画面
                            setStartPreview(mCamera, mSvHolder);
                            cameraPosition = 0;
                            break;
                        }
                    } else {
                        // 现在是前置， 变更为后置
                        if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            /**
                             * 记得释放camera，方便其他应用调用
                             */
                            releaseCamera();
                            mCamera = Camera.open(i);
                            setStartPreview(mCamera, mSvHolder);
                            cameraPosition = 1;
                            break;
                        }
                    }

                }
                break;
            case R.id.ivCapturePhoto:
                // 拍照,设置相关参数
              //  Camera.Parameters params = mCamera.getParameters();
              //  params.setPictureFormat(ImageFormat.JPEG);
              // DisplayMetrics metric = new DisplayMetrics();
            //    getWindowManager().getDefaultDisplay().getMetrics(metric);
          //      int width = metric.widthPixels;  // 屏幕宽度（像素）
        //        int height = metric.heightPixels;  // 屏幕高度（像素）
              //  params.setPreviewSize(width, height);
                // 自动对焦
                //params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
               // mCamera.setParameters(params);





                    try {

                  mCamera.takePicture(shutterCallback, null, jpgPictureCallback);
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }

                break;
            case R.id.correctbn:
                Cursor cur = db.rawQuery("select * from car where carid = ?",new String[]{plate});
                if(cur!=null && cur.getCount() >= 1)//如果查询到的信息不为空
                {
                    boolean b=cur.moveToNext();
                    final String time = tools.getTime();
                    if(!cur.getString(cur.getColumnIndex("parkstate")).equals("1"))
                    {
                        Cursor nowid = db.rawQuery("select recordid from parkrecord order by recordid desc LIMIT 1",null);
                        nowid.moveToNext();
                        ContentValues values=new ContentValues();
                        values.put("carid",plate);
                        values.put("intime",time);
                        values.put("parkid",parkId);
                        values.put("recordid",nowid.getInt(nowid.getColumnIndex("recordid"))+1);
                        long erroralert = db.insert("parkrecord",null,values);
                        if(erroralert!=-1)
                        {
                            ContentValues n = new ContentValues();
                            n.put("parkstate","1");
                            long changealert = db.update("car", n, "carid=?", new String[]{plate});
                            if(changealert!=-1)
                            {
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("成功进入停车场", "欢迎来到"+parkname, mContext);
                            }
                        }
                    }else{
                        String grasp = "0";
                        int money = 0;
                        ContentValues values=new ContentValues();
                        values.put("outtime",time);
                        //放置费用
                        Cursor t = db.rawQuery("select * from parkrecord where outtime is null and carid= ?",new String[]{plate});
                        t.moveToNext();
                        String intime = t.getString(t.getColumnIndex("intime"));
                        String outtime = time;
                        try{
                            grasp = tools.countDate(intime,outtime);
                            money = tools.countMoney(grasp);}
                        catch(ParseException e)
                        {}
                        values.put("pay",money);
                        values.put("state","已支付");
                        long erroralert = db.update("parkrecord", values, "carid=? and outtime is ?", new String[]{plate,null});
                        if(erroralert!=-1)
                        {
                            ContentValues n = new ContentValues();
                            n.put("parkstate","0");
                            long changealert = db.update("car", n, "carid=?", new String[]{plate});
                            if(changealert!=-1)
                            {   String payalert = "";
                                Cursor p = db.rawQuery("select balance,owner from car c  LEFT OUTER JOIN user u on c.owner = u.id where carid = ? and payway is null", new String[]{plate});
                                if(p!=null && p.getCount() >= 1)//如果查询到的信息不为空
                                {
                                    p.moveToFirst();
                                    ContentValues m = new ContentValues();
                                    m.put("balance", p.getInt(p.getColumnIndex("balance")) - money);
                                    long mchangealert = db.update("user", m, "id=?", new String[]{p.getString(p.getColumnIndex("owner"))});
                                    if (mchangealert > -1) {
                                        payalert = "因为您没有绑定支付方式，本次费用已从您的余额中扣除，请及时充值。";
                                    }
                                }
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("成功离开停车场", "您已离开"+parkname+"，欢迎再次光临!本次您所停留的时间为" + grasp + "秒,花费金额：" + money + "元。"+payalert, mContext);
                            }
                        }
                    }

                }else{
                    final String time = tools.getTime();
                    Cursor excur = db.rawQuery("SELECT * FROM parkrecord WHERE state is null and carid = ?",new String[]{plate});
                    if(excur!=null && excur.getCount() >= 1 &&tools.getGusetCar()==true)//如果查询到的信息不为空,证明该车已经进来了。
                    {
                        int money = 0;
                        String grasp="0";
                        excur.moveToNext();
                        String intime = excur.getString(excur.getColumnIndex("intime"));
                        try {
                            grasp=tools.countDate(intime,time);
                            money = tools.countMoney(grasp);}
                        catch(ParseException e)
                        {}
                        ContentValues values=new ContentValues();
                        values.put("outtime",time);
                        values.put("state","待支付");
                        values.put("pay",money);
                        long erroralert = db.update("parkrecord", values, "carid=? and outtime is ?", new String[]{plate,null});
                        if(erroralert!=-1){
                            Notify notify01 = new Notify(mContext);
                            notify01.sendNotify("模拟短信通知", "车牌号："+plate+"车主您好！请注意！您正离开"+parkname+"，但您还没有注册成为我们的用户。因此请选择到人工平台支付停车费用或下载应用并注册会员支付费用。您本次的费用为："+money+"元，用时为："+grasp+"秒", mContext);
                            tools.setGusetCar(false);
                        }
                    }else{//否则证明该车尚未进入，插入数据
                        Cursor nowid = db.rawQuery("select recordid from parkrecord order by recordid desc LIMIT 1",null);
                        nowid.moveToNext();
                        ContentValues values=new ContentValues();
                        values.put("carid",plate);
                        values.put("intime",time);
                        values.put("parkid",parkId);
                        values.put("recordid",nowid.getInt(nowid.getColumnIndex("recordid"))+1);
                        long erroralert = db.insert("parkrecord",null,values);
                        if(erroralert!=-1){
                            Notify notify01 = new Notify(mContext);
                            notify01.sendNotify("模拟短信通知", "车牌号："+plate+"车主您好！您已成功进入"+parkname+"，并成功使用了我们的无感支付服务。为了方便您接下来的支付，请点击下方链接下载我们的应用并注册账号且绑定该车牌。", mContext);
                            tools.setGusetCar(true);
                        }
                    }
                }
                PlateResultView.setText("拍照识别车牌");
                correctbn.setVisibility(View.GONE);
                recheckbn.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Camera.Parameters params = mCamera.getParameters();
        //  params.setPictureFormat(ImageFormat.JPEG);
        // DisplayMetrics metric = new DisplayMetrics();
        //    getWindowManager().getDefaultDisplay().getMetrics(metric);
        //      int width = metric.widthPixels;  // 屏幕宽度（像素）
        //        int height = metric.heightPixels;  // 屏幕高度（像素）
        //  params.setPreviewSize(width, height);
        // 自动对焦
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(params);
        setStartPreview(mCamera, mSvHolder);
    }//创建preview

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mSvHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        setStartPreview(mCamera, mSvHolder);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 当surfaceview关闭时，关闭预览并释放资源
        /**
         * 记得释放camera，方便其他应用调用
         */
        releaseCamera();
        holder = null;
        mSvCamera = null;
        thread.interrupt();
    }




    /**
     * TakePicture回调
     */
    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            shootSound();
            mCamera.setOneShotPreviewCallback(previewCallback);
        }
    };

    Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();
        }
    };

    Camera.PictureCallback jpgPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();

            File pictureFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                // 照片转方向
                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//根据拍照所得的数据创建位图
                Bitmap normalBitmap = BitmapUtil.createRotateBitmap(bitmap);
//                fos.write(data);
                normalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
                // 更新图库
                // 把文件插入到系统图库
//                try {
//                    MediaStore.Images.Media.insertImage(CameraActivity.this.getContentResolver(),
//                            pictureFile.getAbsolutePath(), pictureFile.getName(), "Photo taked by RoadParking.");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
                // 最后通知图库更新
                CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + pictureFile.getAbsolutePath())));
                imagePath =  pictureFile.getAbsolutePath();
                Toast.makeText(CameraActivity.this, "图像已保存", Toast.LENGTH_SHORT).show();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

    /**
     * Check if this device has a camera
     */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * activity返回式返回拍照图片路径
     *
     * @param mediaFile
     */
    private void returnResult(File mediaFile) {
//        Intent intent = new Intent();
//        intent.setData(Uri.fromFile(mediaFile));
//        this.setResult(RESULT_OK, intent);
        this.finish();
    }

    /**
     * 设置camera显示取景画面,并预览
     *
     * @param camera
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);//设置显示取景图片的SurfaceView
            camera.startPreview();//取景
        } catch (IOException e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    /**
     * 播放系统拍照声音
     */
    private void shootSound() {
        AudioManager meng = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (volume != 0) {
            if (mShootMP == null)
                mShootMP = MediaPlayer.create(this, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            if (mShootMP != null)
                mShootMP.start();
        }
    }

    /**
     * 获取Preview界面的截图，并存储
     */
    Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            // 获取Preview图片转为bitmap并旋转
            Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
            final int w = size.width;  //宽度
            final int h = size.height;
            final YuvImage image = new YuvImage(data, ImageFormat.NV21, w, h, null);
            // 转Bitmap
            ByteArrayOutputStream os = new ByteArrayOutputStream(data.length);
            if (!image.compressToJpeg(new Rect(0, 0, w, h), 100, os)) {
                return;
            }
            byte[] tmp = os.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(tmp, 0, tmp.length);
            Bitmap rotatedBitmap = BitmapUtil.createRotateBitmap(bitmap);

            cropBitmapAndRecognize(rotatedBitmap);

        }
    };

    public void cropBitmapAndRecognize(Bitmap originalBitmap) {//车牌识别部分关键
        // 裁剪出关注区域
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;  // 屏幕宽度（像素） //1080
        int height = metric.heightPixels;  // 屏幕高度（像素）//1920
        Bitmap sizeBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true);

        int rectWidth = (int) (PlateRectZone.getWidth() * 1.5);//523
        int rectHight = (int) (PlateRectZone.getHeight() * 1.5);//184
        int[] location = new int[2];
        PlateRectZone.getLocationOnScreen(location);//365，837
        location[0] -= PlateRectZone.getWidth() * 0.5 / 2;//277
        location[1] -= PlateRectZone.getHeight() * 0.5 / 2;//806
        Bitmap normalBitmap = Bitmap.createBitmap(sizeBitmap, location[0], location[1], rectWidth, rectHight);

        // 保存图片并进行车牌识别
        File pictureFile = FileUtil.getOutputMediaFile(FileUtil.FILE_TYPE_PLATE);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return;
        }

        try {
            PlateResultView.setText("正在识别...");
            FileOutputStream fos = new FileOutputStream(pictureFile);
            normalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            // 最后通知图库更新
            CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + pictureFile.getAbsolutePath())));

            // 进行车牌识别
            plate="";
            plate = mPlateRecognizer.recognize(pictureFile.getAbsolutePath());
            imagePathR=pictureFile.getAbsolutePath();
            //plate="津C11111";
            if (null != plate && !plate.equalsIgnoreCase("0")) {
                correctbn.setVisibility(View.VISIBLE);
                recheckbn.setVisibility(View.VISIBLE);
                plate = plate.substring(plate.indexOf(":")+1);
                PlateResultView.setText(plate);

               /*Cursor cur = db.rawQuery("select * from car where carid = ?",new String[]{plate});
                if(cur!=null && cur.getCount() >= 1)//如果查询到的信息不为空
                {
                    boolean b=cur.moveToNext();
                    Date now = new Date();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
                    final String time = dateFormat.format( now );
                    if(!cur.getString(cur.getColumnIndex("parkstate")).equals("1"))
                    {
                        Cursor nowid = db.rawQuery("select recordid from parkrecord order by recordid desc LIMIT 1",null);
                        nowid.moveToNext();
                        ContentValues values=new ContentValues();
                        values.put("carid",plate);
                        values.put("intime",time);
                        values.put("parkid",parkId);
                        values.put("recordid",nowid.getInt(nowid.getColumnIndex("recordid"))+1);
                        long erroralert = db.insert("parkrecord",null,values);
                        if(erroralert!=-1)
                        {
                            ContentValues n = new ContentValues();
                            n.put("parkstate","1");
                            long changealert = db.update("car", n, "carid=?", new String[]{plate});
                            if(changealert!=-1)
                            {
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("成功进入停车场", "欢迎来到"+parkname, mContext);
                            }
                        }
                    }else{
                        String grasp = "0";
                        int money = 0;
                        ContentValues values=new ContentValues();
                        values.put("outtime",time);
                        //放置费用
                        Cursor t = db.rawQuery("select * from parkrecord where outtime is null and carid= ?",new String[]{plate});
                        t.moveToNext();
                        String intime = t.getString(t.getColumnIndex("intime"));
                        String outtime = time;
                        try{
                            grasp=tools.countDate(intime,outtime);
                            money = tools.countMoney(grasp);}
                        catch(ParseException e)
                        {}
                        values.put("pay",money);
                        values.put("state","已支付");
                        long erroralert = db.update("parkrecord", values, "carid=? and outtime is ?", new String[]{plate,null});
                        if(erroralert!=-1)
                        {
                            ContentValues n = new ContentValues();
                            n.put("parkstate","0");
                            long changealert = db.update("car", n, "carid=?", new String[]{plate});
                            if(changealert!=-1)
                            {   String payalert = "";
                                Cursor p = db.rawQuery("select balance,owner from car c  LEFT OUTER JOIN user u on c.owner = u.id where carid = ? and payway is null", new String[]{plate});
                                if(p!=null && p.getCount() >= 1)//如果查询到的信息不为空
                                {
                                    p.moveToFirst();
                                    ContentValues m = new ContentValues();
                                    m.put("balance", p.getInt(p.getColumnIndex("balance")) - money);
                                    long mchangealert = db.update("user", m, "id=?", new String[]{p.getString(p.getColumnIndex("owner"))});
                                    if (mchangealert > -1) {
                                        payalert = "因为您没有绑定支付方式，本次费用已从您的余额中扣除，请及时充值。";
                                    }
                                }
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("成功离开停车场", "您已离开"+parkname+"，欢迎再次光临!本次您所停留的时间为" + grasp + "秒,花费金额：" + money + "元。"+payalert, mContext);
                                }
                            }
                        }

                    }else{
                        final String time = tools.getTime();
                        PlateResultView.setText(plate);
                        Cursor excur = db.rawQuery("SELECT * FROM parkrecord WHERE state is null and carid = ?",new String[]{plate});
                        if(excur!=null && excur.getCount() >= 1 &&tools.getGusetCar()==true)//如果查询到的信息不为空,证明该车已经进来了。
                        {
                            int money = 0;
                            String grasp="0";
                            excur.moveToNext();
                            String intime = excur.getString(excur.getColumnIndex("intime"));
                            try {
                                grasp=tools.countDate(intime,time);
                                money = tools.countMoney(grasp);}
                            catch(ParseException e)
                            {}
                            ContentValues values=new ContentValues();
                            values.put("outtime",time);
                            values.put("state","待支付");
                            values.put("pay",money);
                            long erroralert = db.update("parkrecord", values, "carid=? and outtime is ?", new String[]{plate,null});
                        if(erroralert!=-1){
                            Notify notify01 = new Notify(mContext);
                            notify01.sendNotify("模拟短信通知", "车牌号："+plate+"车主您好！请注意！您正离开"+parkname+"，但您还没有注册成为我们的用户。因此请选择到人工平台支付停车费用或下载应用并注册会员支付费用。您本次的费用为："+money+"元，用时为："+grasp+"秒", mContext);
                            tools.setGusetCar(false);
                            }
                        }else{//否则证明该车尚未进入，插入数据
                            Cursor nowid = db.rawQuery("select recordid from parkrecord order by recordid desc LIMIT 1",null);
                            nowid.moveToNext();
                            ContentValues values=new ContentValues();
                            values.put("carid",plate);
                            values.put("intime",time);
                            values.put("parkid",parkId);
                            values.put("recordid",nowid.getInt(nowid.getColumnIndex("recordid"))+1);
                            long erroralert = db.insert("parkrecord",null,values);
                            if(erroralert!=-1){
                                Notify notify01 = new Notify(mContext);
                                notify01.sendNotify("模拟短信通知", "车牌号："+plate+"车主您好！您已成功进入"+parkname+"，并成功使用了我们的无感支付服务。为了方便您接下来的支付，请点击下方链接下载我们的应用并注册账号且绑定该车牌。", mContext);
                                tools.setGusetCar(true);
                            }
                        }
                    }*/
            } else {
                /*Date now = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
                String hehe = dateFormat.format( now );
                System.out.println(hehe);
                PlateResultView.setText(hehe);

                Date time=null;
                try {
                    time = dateFormat.parse(hehe);
                    //time= (Date)sdf.parse(sdf.format(new Date()));
                    System.out.println(time.toString());
                } catch (ParseException e) {

                    e.printStackTrace();
                }*/

                PlateResultView.setText("获取失败，请调整角度！");
                deletePicture(imagePathR);
                deletePicture(imagePath);
                setToast("图像已删除");
            }

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    public void deletePicture(String path)
    {
        File file =new File(path);
        file.delete();
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?",new String[]{path});
        CameraActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(path))));
    }

    public synchronized  void turnLightOn() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameters);
    }
    /**
     * 通过设置Camera关闭闪光灯
     */
    public synchronized void turnLightOff() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameters  = mCamera.getParameters();
        if (parameters == null) {
            return;
        }
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameters);
    }

    /*private class CarTask extends AsyncTask<Void, Void, Void> {
        private byte[] mData;
        //构造函数
        PalmTask(byte[] data) {
        this.mData = data;
        return;}
    }

    @Override
    protected Void doInBackground(Void... params) {
        // TODO Auto-generated method stub
        Camera.Size size = mCamera.getParameters().getPreviewSize(); //获取预览大小
        final int w = size.width;//宽度
        final int h = size.height;
        final YuvImage image = new YuvImage(mData, ImageFormat.NV21, w, h, null);
        ByteArrayOutputStream os = new ByteArrayOutputStream(mData.length);
        if(!image.compressToJpeg(new Rect(0, 0, w, h), 100, os))
            return null;
        byte[] tmp = os.toByteArray();
        Bitmap bmp = BitmapFactory.decodeByteArray(tmp, 0,tmp.length);
        doSomethingNeeded(bmp); //自己定义的实时分析预览帧视频的算法
        return null;
        }

    public void onPreviewFrame(byte[] data, Camera camera) {
        // TODO Auto-generated method stub
        if(null != mFaceTask){
            switch(mFaceTask.getStatus()){
            case RUNNING:
            return;
            case PENDING:
            mFaceTask.cancel(false);
            break;
            }
        }
        mFaceTask = new PalmTask(data);
        mFaceTask.execute((Void)null);
    }*/

private class ScanThread implements Runnable{
    public volatile boolean exit = false;

    public void run() {
        // TODO Auto-generated method stub
        while(!Thread.currentThread().isInterrupted()){
            try {
                if(null != mCamera)
                {
//myCamera.autoFocus(myAutoFocusCallback);
                    mCamera.setOneShotPreviewCallback(previewCallback);
                    Log.i(TAG, "setOneShotPreview...");
                }
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

}
public void setToast(String text)
{
    Toast.makeText(CameraActivity.this, text, Toast.LENGTH_SHORT).show();
}

}


