package com.liuzozo.stepdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.liuzozo.stepdemo.bean.PathRecord;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;


import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;

import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.liuzozo.stepdemo.OtherFunction.*;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.SportRecord;
import com.liuzozo.stepdemo.ui.UIHelperUtil;
import com.liuzozo.stepdemo.OtherFunction.MyCountDownTimer;
import com.liuzozo.stepdemo.OtherFunction.StepUtils;



import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 运动结果页面，点击完成时跳到该页面
 *  评分运行规则：依次判断 距离大于0 ★；运动时间大于40分钟 ★★；速度在3~6km/h之间 ★★★
 */
public class SportResult_Activity extends AppCompatActivity implements View.OnClickListener, AMap.OnMapScreenShotListener{


    private AMap aMap;  // 地图
    private MapView mapView;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    PathRecord pathRecord; // PathRecord 类记录本次运行的所有路径
    private List<LatLng> mPathLinePoints = new ArrayList<>();

    TextView resultStandard1,resultStandard2,resultStandard3;
    // 整个容器最大的布局
    private ViewGroup mViewGroupContainer;
    // 除地图外的布局
    private View mScreemShotView;

    private  MyDatabaseHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_result);
        mapView = findViewById(R.id.stepMap);
        mapView.onCreate(savedInstanceState);//
        initMap();

        //设置分享和返回
        TextView back = findViewById(R.id.sport_record_back);
        back.setText("< Back");
        TextView share = findViewById(R.id.sport_record_share);
        share.setText("Share");
        share.setOnClickListener(this);
        back.setOnClickListener(this);
        /*View v = (View) findViewById(R.id.root_pic_id);
        v.getBackground().setAlpha(200);*/

        //设置截屏画面
        mViewGroupContainer = findViewById(R.id.root_view);
        mScreemShotView = findViewById(R.id.my_screenshot_view);

        Intent intent = getIntent();
        pathRecord = intent.getParcelableExtra("path_record");
        //设置终点Marker
       /* aMap.getUiSettings().setMyLocationButtonEnabled(false);*/
      /*  LatLng endPoint = pathRecord.getEndpoint();
        MapUtils.addMarkerToMap(endPoint,aMap,this,"终点","无",R.mipmap.end_point);*/
        //根据数据设置星星和textview

        ImageView star1 = findViewById(R.id.image_star1);
        ImageView star2 = findViewById(R.id.image_star2);
        ImageView star3 = findViewById(R.id.image_star3);
        BigDecimal duration = new BigDecimal(pathRecord.getDuration());
        BigDecimal divisor_min = new BigDecimal(60000);
        BigDecimal divisor_hour = new BigDecimal(3600000);
        Log.d("TAG", "Duration" + pathRecord.getDuration() + "/min:" + duration.divide(divisor_min, 2, BigDecimal.ROUND_HALF_UP).intValue() + "distance" + pathRecord.getDistance() + "speed" + pathRecord.getSpeed());

        //设置结果和评价
        resultStandard1 = findViewById(R.id.result_standard1);
        resultStandard2 = findViewById(R.id.result_standard2);

        if((duration.divide(divisor_min, 2, BigDecimal.ROUND_HALF_UP).intValue()) > 40)
            resultStandard1.setText(" 运动40分钟以上,脂肪开始燃烧啦");
        else
            resultStandard1.setText(" 继续坚持,努力达到运动40分钟以上哦");

        if (pathRecord.getSpeed() >= 3 && pathRecord.getSpeed() < 6)
            resultStandard2.setText(" 跑步速度不错，继续保持");
        else
            resultStandard2.setText(" 调整速度至3到6km/h,将更利于脂肪燃烧呢");


        if (pathRecord.getDistance() > 0) {
            star1.setImageResource(R.mipmap.big_star);
            if ((duration.divide(divisor_min, 2, BigDecimal.ROUND_HALF_UP).intValue()) > 40) {
                star2.setImageResource(R.mipmap.big_star);
                if (pathRecord.getSpeed() >= 3 && pathRecord.getSpeed() < 6)
                    star3.setImageResource(R.mipmap.big_star);

            }
        }

        TextView distance = findViewById(R.id.sport_result_km);
        distance.setText(String.format("%.2f",pathRecord.getDistance()));
        TextView durationview = findViewById(R.id.sport_result_duration);
        durationview.setText(pathRecord.getTimeFormatInString());
        TextView cal = findViewById(R.id.sport_result_kcal);
        double calorie = pathRecord.getCalorie();
        cal.setText(String.format("%.1f",calorie));

        if(pathRecord.getPathline().size()==0)
            Toast.makeText(this,"本次运动距离为零，将不保存数据",Toast.LENGTH_LONG).show();


        Log.d("TAG", "时间控件的str " + pathRecord.getTimeFormatInString() );


     //检验数据是否储存好
     /*   dbHelper =  new MyDatabaseHelper(this, "SportData.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("SportRecord", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String datadistance = cursor.getString(cursor.getColumnIndex("Distance"));
                double dataduration = cursor.getDouble(cursor.getColumnIndex("Duration"));
                String date = cursor.getString(cursor.getColumnIndex("mDateTag"));
                Log.d("TAG", "数据库Diatance: " + datadistance);
                Log.d("TAG", "数据库Duration:  " + dataduration );
                Log.d("TAG", "数据库DateTag:  " + date );

            } while (cursor.moveToNext());

        }
*/
    }


   /*  * 下面代码为点击分享朋友圈
     * 1 .首先根据页面布局，选择特定区域的View的id (root view id) , 转bitmap
     * 2. 压缩图片，以免图片过大，内存溢出
     * 3. 把图片保存成手机上的文件，得到Uri 路径，
     * 4. 利用安卓自带的应用程序之间的分享功能，进行分享到朋友圈，微信等
     * @param view*/
     //禁止返回键
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            //传入结束前一个活动的信息
            Intent intent = new Intent();
            intent.putExtra("info","MapActivity_Finish");
            setResult(RESULT_OK,intent);

            //销毁当前活动
            finish();
            return true;
        }
        else {
            return super.dispatchKeyEvent(event);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sport_record_share:

             /*   final View textView = LayoutInflater.from(this).inflate(R.layout.activity_sport_result, null);
                View rootView = textView.findViewById(R.id.root_pic_id);
                Bitmap bitmap = getBitmapByView(rootView);
                Bitmap img = compressImage(bitmap);
                File file = bitMap2File(img);
                // 地图截屏*/
                aMap.getMapScreenShot(this);
                // Bitmap bitmap = getDiskBitmap(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.png");
                // 得到地图截屏后的结果文件
                File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "test1.png");

                if (file != null && file.exists() && file.isFile()) {
                    //由文件得到uri

                    /*Uri imageUri = Uri.fromFile(file);*/
                    Uri imageUri= FileProvider.getUriForFile(this,"com.liuzozo.stepdemo.fileProvider", file);//这里进行替换uri的获得方式

                    Log.d("TAG", "开始获得uri");
                    Intent shareIntent = new Intent();
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    /*ComponentName comp = new ComponentName("com.tencent.mm", "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                    shareIntent.setComponent(comp);*/
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "测试标题");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "测试内容");
                    /*shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);*/
                    startActivity(Intent.createChooser(shareIntent, "来自xxx"));
                    /*this.startActivity(shareIntent);*/
                    /*startActivity(Intent.createChooser(shareIntent, "分享图片"));*/
                }
                break;
            case R.id.sport_record_back:
                //传入结束前一个活动的信息
                Intent intent = new Intent();
                intent.putExtra("info","MapActivity_Finish");
                setResult(RESULT_OK,intent);

                //销毁当前活动
                finish();
                break;
            default:
                break;
        }
    }


    // 地图截屏回调函数
    @Override
    public void onMapScreenShot(Bitmap bitmap) {
        ScreenShotHelper.saveScreenShot(bitmap, mViewGroupContainer, mapView, mScreemShotView);

    }

    @Override
    public void onMapScreenShot(Bitmap bitmap, int i) {

    }
    /**
     * 将布局转化为bitmap这里传入的是你要截的布局的根View
     */
   /* public Bitmap getBitmapByView(View view) {
        view.destroyDrawingCache();
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.setDrawingCacheEnabled(true);
        Log.d("TAG", "截屏变成bitmap");
        return view.getDrawingCache(true);

    }

    *//**
     * 压缩图片
     *//*

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 8, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 400) {  //循环判断如果压缩后图片是否大于400kb,大于继续压缩（这里可以设置大些）
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        Log.d("TAG", "截屏被压缩");
        return bitmap;
    }

    *//**
     * 把bitmap转化为file
     *//*
    public File bitMap2File(Bitmap bitmap) {
        String path = "";
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = Environment.getExternalStorageDirectory() + File.separator;//保存到sd根目录下
        }

        File f = new File(path, "share" + ".jpg");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Log.d("TAG", "截屏变成文件");
            return f;
        }
    }




        // 判断是否安装指定app
        public static boolean isInstallApp(Context context, String app_package){
            final String PACKAGE_WECHAT = "com.tencent.mm";
            final String PACKAGE_MOBILE_QQ = "com.tencent.mobileqq";
             final String PACKAGE_QZONE = "com.qzone";
             final String PACKAGE_SINA = "com.sina.weibo";
            final PackageManager packageManager = context.getPackageManager();
            List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
            if (pInfo != null) {
                for (int i = 0; i < pInfo.size(); i++) {
                    String pn = pInfo.get(i).packageName;
                    if (app_package.equals(pn)) {
                        return true;
                    }
                }
            }
            return false;
        }
*/

    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setCompassEnabled(false);// 设置显示指南针
            setUpMap();
        }
    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位回调
        // 自定义系统定位小蓝点
        //设置地图属性
        UiSettings uiSettings = aMap.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);// 设置默认定位按钮是否显示
      /*  MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.TRANSPARENT);*/

       /* MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.TRANSPARENT); // 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); // 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);*/
       //设置地图标记
        setmLocationStyle();
       /* aMap.setLocationSource(this);*/// 设置定位监听
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }
    //设置定位图标
    public void setmLocationStyle() {
        MyLocationStyle locationStyle = new MyLocationStyle();
        locationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.end_point));
        /* locationStyle.strokeColor(Color.);*/
        /*locationStyle.strokeWidth(2);*/
        locationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);

        locationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        /* locationStyle.strokeColor(Color.TRANSPARENT);*/ // 设置圆形的边框颜色
        aMap.setMyLocationStyle(locationStyle);

    }
}
