package com.liuzozo.stepdemo;

import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;

import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import com.liuzozo.stepdemo.OtherFunction.*;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.SportRecord;
import com.liuzozo.stepdemo.ui.BitmapUtil;
import com.liuzozo.stepdemo.ui.UIHelperUtil;
import com.liuzozo.stepdemo.OtherFunction.MyCountDownTimer;
import com.liuzozo.stepdemo.OtherFunction.StepUtils;
import com.liuzozo.stepdemo.OtherFunction.VibratorUtil;



import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.math.BigDecimal.ROUND_HALF_DOWN;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * 开始跑步页面  ,分跑步模式和地图模式
 *
 *    当开始跑步时 倒计时3秒，
 *    同时利用 PathRecord 设置 startTime 、记录路径的经纬度，
 *    可以通过记录的两个经纬度的距离计算跑了多少公里
 *    数据库里面保存:
 *    当前时间的时间戳，  距离， 时长， 开始时间， 结束时间， 开始位置的经纬度， 结束位置的经纬度，路线的经纬度
 *    根据体重计算的卡路里， 平均时速（公里/小时） 平均配速（分钟/公里）
 *
 *  tips:
 *  onLocationChanged 可以每隔几秒钟获得一次当前位置的经纬度，你可以把它保存到List 里面，
 *  当跑完步把list 保存到PathRecord 类，从而保存到数据库
 */

public class SportMap_Activity extends AppCompatActivity implements LocationSource, AMapLocationListener, View.OnClickListener ,
        GeocodeSearch.OnGeocodeSearchListener, AMap.OnMarkerClickListener,AMap.OnCameraChangeListener {


    private AMap aMap;  // 地图
    private MapView mapView;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private GeocodeSearch geocoderSearch;
    private  Marker mGPSMarker;
    private MarkerOptions markOptions;
    private LatLng  startlatLng , planEndLatLng;

  /*  private View sportMyView;*/

    /*private AMapLocation privLocation;*/
    LatLng privLatLng;
    double distance = 0;

    private  TextView tv_modeText; // 地图模式与跑步模式控件

    private RelativeLayout rlMap; // 控制地图布局显示与否
    private boolean mode = false , isLock = false, isRun = false; // true 跑步模式, false 地图模式
    private boolean isFirstLocation = true;
    private boolean isToStartVibrate = true;
   /* private boolean isLocationRight = false, confirmFirstLocation = false;*/
    private boolean isCustomizedEndpoint = false;
    private Date startTime , endTime,pauseTime,continueTime, currentTime;
    private int count = 0;
   /* StepUtils stepUtils;*/

    PathRecord pathRecord; // PathRecord 类记录本次运行的所有路径
    List<LatLng> mPathLinePoints = new ArrayList<>();

    private TextView tvSportComplate;
    private TextView tvSportPause;
    private TextView tvSportContinue;
    private  TextView  tvSportStart ;
    private ImageView lockBtn;
    private Button planEndPoint;
    private LinearLayout endPointText;
    Timer timer;
    MyCountDownTimer myCountDownTimer;
    Context context;
    String endAddressName;
    double distanceToPlanEnd;




    /**
     * 需要进行检测的权限数组
     */
    protected String[] needPermissions = {
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };
    private static final int PERMISSON_REQUESTCODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_map);
        //锁频显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED

                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        //设定轨迹记录类
        pathRecord = new PathRecord();

        // 初始化地图组件
        mapView = findViewById(R.id.stepMap);
        mapView.onCreate(savedInstanceState);//
        initMap();
        initView();//显示起点
        context = MyApplication.getContext();

        //弹出对话框是否自定义起点
        SharedPreferences read = getSharedPreferences("notification_data", MODE_PRIVATE);
       /* SharedPreferences.Editor editor = getSharedPreferences("notification_data", MODE_PRIVATE).edit();
        editor.putString("planEndPointService", "true");
        editor.apply();*/
        if(read.getString("planEndPointService",null)==null || read.getString("planEndPointService",null).equals("true")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("Tips");
            dialog.setMessage("启用自定义终点功能，拖拽地图选择终点，并在到达终点后自动结束运动");
            dialog.setCancelable(false);
            dialog.setPositiveButton("                        确认并再次提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = getSharedPreferences("notification_data", MODE_PRIVATE).edit();
                    editor.putString("planEndPointService", "true");
                    editor.apply();


                }
            });
            dialog.setNegativeButton("不再提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("notification_data", MODE_PRIVATE).edit();
                    editor.putString("planEndPointService", "flase");
                    editor.apply();


                }
            });
            dialog.show();
        }











    }

    //禁止使用返回键返回到上一页,但是可以直接退出程序**

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            if(isRun){
            Toast.makeText(this,"正在跑步，退出请先点击暂停",Toast.LENGTH_SHORT).show();
            return true;//代表事件被消耗掉
            }
            else
                return super.dispatchKeyEvent(event);

        }

        return super.dispatchKeyEvent(event);

    }



    //当结果界面结束返回时调用以下
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    finish();
                }
            default:
                break;
        }
    }



    private void initView() {
        // 点击时隐藏|显示地图

        tv_modeText = findViewById(R.id.tv_mode);
        tv_modeText.setOnClickListener(this);

        rlMap = findViewById(R.id.rlMap);
       /* rlMap.setVisibility(View.GONE);*/ // 刚开始不现实地图
        //继续按钮

        //设置开始按钮
        tvSportStart = findViewById(R.id.tv_sport_start);
        tvSportStart.setOnClickListener(this);

        //设置完成按钮
        tvSportComplate = findViewById(R.id.tv_sport_complate);
        tvSportComplate.setOnClickListener(this);
        tvSportComplate.setVisibility(View.GONE);

        //设置继续按钮,无法点击
        tvSportContinue = findViewById(R.id.tv_sport_continue);
        tvSportContinue.setOnClickListener(this);
        tvSportContinue.setVisibility(View.GONE);

        //设置暂停按钮
        tvSportPause = findViewById(R.id.tv_sport_pause);
        tvSportPause.setOnClickListener(this);
        tvSportPause.setVisibility(View.GONE);

        //按钮锁定
        lockBtn = findViewById(R.id.button_lock);
        lockBtn.setOnClickListener(this);
        lockBtn.setVisibility(View.GONE);

        //设置自定义终点按钮
        planEndPoint = findViewById(R.id.plan_end_point);
        planEndPoint.setOnClickListener(this);

        //终点文字
        endPointText = findViewById(R.id.end_point_text);
        endPointText.setVisibility(View.GONE);





    }


    /**
     * 每个按钮的点击事件
     *
     @param view
     */
    @Override
    public void onClick(View view) {
        Chronometer passtime = findViewById(R.id.cm_passtime);
        switch (view.getId()) {

            case R.id.plan_end_point:
                planEndPoint.setVisibility(View.GONE);
                endPointText.setVisibility(View.VISIBLE);
                isCustomizedEndpoint = true;
                mLocationClient.stopLocation();

                break;

            case R.id.tv_sport_start:
                //跑步状态
                isRun = true;

                planEndPoint.setVisibility(View.GONE);

                if(isCustomizedEndpoint){
                    mGPSMarker.remove();
                    setFixedMarker(planEndLatLng,"目的地",endAddressName,R.mipmap.start_point1);
                    isCustomizedEndpoint =false;
                    Log.d("TAG","目的地经纬度：" + planEndLatLng.toString());
                }



                startlatLng = privLatLng;
                Log.d("TAG","目的地起点：" + startlatLng.toString());
                setFixedMarker(startlatLng,"起点","",R.mipmap.start_point);
                mLocationClient.startLocation();
                //点击开始跑步按钮
                final Window window = getWindow();
                TextView countdown = (TextView) findViewById(R.id.tv_countdown);
                //启动计时器
                timer = new Timer((Chronometer) findViewById(R.id.cm_passtime),window,countdown);
                timer.onRecordFirstStart();

                //同时利用 PathRecord 设置 startTime 、记录路径的经纬度，可以通过记录的两个经纬度的距离计算跑了多少公里
                startTime = new Date();
                pathRecord.setStartTime(startTime.getTime() + 3000);
                String datestr = startTime.toString();
                String[] array = datestr.split(" ");
                Log.d("TAG", "cuurent value is " + Arrays.toString(array) + datestr);
                String strDate = array[5] + array[1] + array[2] +array[0] + "";//2019May25Sat
                pathRecord.setDateTag(strDate);
                Log.d("TAG", "cuurent value is " + strDate);

                //可移动Marker消失
                //连线从当前位置开始

                pathRecord.setStartpoint(new LatLng(startlatLng.latitude, startlatLng.longitude));

                //触发定位图标属性
             /*   setmLocationStyle();*/
                tvSportStart.setVisibility(View.GONE);
                tvSportPause.setVisibility(View.VISIBLE);
                lockBtn.setVisibility(View.VISIBLE);



                break;
            case R.id.tv_sport_complate:
                sportComplate();
             /*   Log.d("TAG","时间控件在结束前的字符串返回" + timer.getTimeString());
                String timestr = timer.getTimeString();
                pathRecord.setTimeFormatInString(timestr);//返回为null?
                timer.onRecordStop();
                endTime = new Date();
                pathRecord.setEndTime(endTime.getTime());
                pathRecord.setEndpoint(privLatLng);
                pathRecord.setDistance(distance);
                pathRecord.setDuration(timer.getChronometerSeconds());//使得结果时间于之前活动显示的一致

                pathRecord.setPathline(mPathLinePoints);
                BigDecimal distance1 = new BigDecimal(pathRecord.getDistance());
                BigDecimal duration1 = new BigDecimal(pathRecord.getDuration());
                BigDecimal divisorSec = new BigDecimal(1000);
                BigDecimal divisorMin = new BigDecimal(60000);
              //  BigDecimal multiplyMin = new BigDecimal(60);
                BigDecimal multiplyHour = new BigDecimal(3600);
                BigDecimal seconds = duration1.divide(divisorSec,3,ROUND_HALF_UP);
                BigDecimal speedSec = distance1.divide(seconds,4,ROUND_HALF_UP);//公里/秒
               //BigDecimal hours = duration1.divide(divisorHour,4,ROUND_HALF_UP);
                BigDecimal minutes = duration1.divide(divisorMin,4,ROUND_HALF_UP);
                pathRecord.setSpeed(speedSec.multiply(multiplyHour).doubleValue()) ;
                if(distance1.doubleValue() != 0)
                pathRecord.setDistribution(minutes.divide(distance1,4,ROUND_HALF_UP).doubleValue());
                else
                    pathRecord.setDistribution(0.0);
                SharedPreferences read = context.getSharedPreferences("personal_data",MODE_PRIVATE);
                double weight = Double.parseDouble(read.getString("weight",""));
                double cal = weight * distance * 1.036;

                pathRecord.setCalorie(cal);
                Log.i("TAG",  " 当前错误热量" + pathRecord.getCalorie());
                SportRecord sportRecord = StepUtils.pathRecordToSportRecord(pathRecord);
                sportRecord.saveInSQL(this);
                mLocationClient.stopLocation();

                Intent intent = new Intent(this, SportResult_Activity.class);
                intent.putExtra("path_record", pathRecord);
                startActivityForResult(intent,1);//当结果页面点击返回时传出信息对应的请求码
             *//*   Toast.makeText(SportMap_Activity.this, "点击完成" , Toast.LENGTH_SHORT).show();*//*
                isRun = false;*/
                break;
            case R.id.tv_sport_pause:
             /*   Toast.makeText(SportMap_Activity.this, "点击暂停", Toast.LENGTH_SHORT).show();*/
                pauseTime = new Date();
                timer.onRecordPause();
              /*  pause = true;*/
                isRun = false;
                mLocationClient.stopLocation();
                tvSportPause.setVisibility(View.GONE);
                lockBtn.setVisibility(View.GONE);
                tvSportComplate.setVisibility(View.VISIBLE);
                tvSportContinue.setVisibility(View.VISIBLE);

                break;
            case R.id.tv_sport_continue:
              /*  Toast.makeText(SportMap_Activity.this, "经纬度" + "KM", Toast.LENGTH_SHORT).show();*/
                timer.onRecordContinue();
                isRun =true;
                mLocationClient.startLocation();
                tvSportContinue.setVisibility(View.GONE);
                tvSportComplate.setVisibility(View.GONE);
                tvSportPause.setVisibility(View.VISIBLE);
                lockBtn.setVisibility(View.VISIBLE);

                break;
            case R.id.tv_mode:
                if (mode) {
                    rlMap.setVisibility(View.VISIBLE);
                    tv_modeText.setText("地图模式");
                    UIHelperUtil.setLeftDrawable(tv_modeText, ContextCompat.getDrawable(this, R.mipmap.run_mode));
                    mode = false;
                } else {
                    rlMap.setVisibility(View.GONE);
                    tv_modeText.setText("跑步模式");
                    UIHelperUtil.setLeftDrawable(tv_modeText, ContextCompat.getDrawable(this, R.mipmap.map_mode));
                    mode = true;
                }
            case R.id.button_lock:
                if(isLock){
                    tvSportPause.setVisibility(View.VISIBLE);
                    lockBtn.setImageResource(R.mipmap.unlocked);
                    aMap.getUiSettings().setAllGesturesEnabled (true);
                    isLock = false;
                }
                else {
                    aMap.getUiSettings().setAllGesturesEnabled (false);
                    tvSportPause.setVisibility(View.GONE);
                    lockBtn.setImageResource(R.mipmap.locked);
                    isLock = true;
                }
                break;
            default:
                break;
        }
    }

    /**
     * 地图开始
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(20));
            aMap.getUiSettings().setZoomControlsEnabled(false);
            aMap.getUiSettings().setCompassEnabled(true);// 设置显示指南针
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
        uiSettings.setCompassEnabled(true);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setScaleControlsEnabled(true);
        uiSettings.setTiltGesturesEnabled(true);
        uiSettings.setRotateGesturesEnabled(true);
        uiSettings.setMyLocationButtonEnabled(true);

/*
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.strokeColor(Color.TRANSPARENT); // 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)); // 设置圆形的填充颜色
        aMap.setMyLocationStyle(myLocationStyle);*/
        setmLocationStyle();


        // 设置定位监听
       /* aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);*/
        aMap.setOnMarkerClickListener(this);

        aMap.setLocationSource(this);
        //设置地图监听
        aMap.setOnCameraChangeListener(this);
        // 绑定marker拖拽事件
        ////逆编码监听事件
//              GeocodeSearch.OnGeocodeSearchListener,
        geocoderSearch = new GeocodeSearch(this);
        geocoderSearch.setOnGeocodeSearchListener(this);



        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

    }


    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            // 初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            // 设置定位监听 属性
            mLocationClient.setLocationListener(this);//this
            // 可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
            // mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.Sport);

            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔 2s 最低1000ms
            mLocationOption.setInterval(2000);
            mLocationOption.setNeedAddress(true);
            mLocationOption.setHttpTimeOut(20000);
            if (null != mLocationClient) {
                mLocationClient.setLocationOption(mLocationOption);
                // 设置定位监听
                mLocationClient.startLocation();

                /*AMapLocation firstLocation = mLocationClient.getLastKnownLocation();
                pathRecord.setStartpoint(new LatLng(firstLocation.getLatitude(),firstLocation.getLongitude()));*/
            }

        }
    }

    // 停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;

    }

    // 定位成功后 会 每 2秒钟 回调函数

        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {


            if (mListener != null && aMapLocation != null) {
                // code 12 is no premission
                Log.i("TAG", aMapLocation.getErrorCode() + " 当前错误code");
                if (aMapLocation.getErrorCode() == 0) {
                    mListener.onLocationChanged(aMapLocation);// 显示系统圆点
                    aMapLocation.getLocationType();//查看是什么类型的点
                   /* if(isFirstLoaction) {
                        //先设置起点marker
                        TextView startpoint = findViewById(R.id.sport_map_startpoint);
                        startlatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                        startAddressName = aMapLocation.getAddress();
                        startpoint.setText(startAddressName);
                        mLocationClient.stopLocation();
                        isFirstLoaction = false;
                    }*/

                   if(isRun) {

                      /* mPathLinePoints.add(new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude()));*/
                     /*  if (pause) {
                           privLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                           *//* privLocation = aMapLocation;*//*//当暂停后先获得当前位置再连线
                           pause = false;
                       }*/
                       //一边定位一边连线
                       LatLng curLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    /*if(confirmFirstLocation){
                        privLatLng = startlatLng;
                        confirmFirstLocation = false;
                    }*/
                       TextView speedView = findViewById(R.id.tvSpeed);
                       double durdistance = drawLines(curLatLng);
                       speedView.setText(String.format("%.2f", ((durdistance * 3.6) / 2.0))); //2秒回调 获得速度米/秒 * 3.6 //获取速度信息 aMaplocation.getSpeed() 单位：米/秒
                       distance += durdistance / 1000;
                       /* Toast.makeText(SportMap_Activity.this, "经纬度" + distance + "KM", Toast.LENGTH_SHORT).show();*/
                       TextView mileage = findViewById(R.id.tvMileage);
                       /* Mon May 27 19:21:08 GMT+08:00 2019*/
                       mileage.setText(String.format("%.2f", distance));
                       /* privLocation = aMapLocation;*///当暂停后先获得当前位置再连线


                       /*addMarkerToMap(pathRecord.getStartpoint());*/
                       //添加起点图标
                       /*final Marker marker = aMap.addMarker(new MarkerOptions().position(pathRecord.getStartpoint()).title("起点").snippet(privLocation.getAddress()));*/
                       mPathLinePoints.add(curLatLng);
                   }
                    privLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                }
                else {
                    String errText = "定位失败" + aMapLocation.getErrorCode() + ":" + aMapLocation.getErrorInfo();
                    Log.e("AmapError", errText);
                }


            }

        }

        public double drawLines(LatLng curLocation) {

            if (null == curLocation) {
                return 0;
            }

            PolylineOptions options = new PolylineOptions();
            //上一个点的经纬度
            options.add(privLatLng);
            //当前的经纬度
            options.add(new LatLng(curLocation.latitude, curLocation.longitude));
            options.width(10).geodesic(false).color(Color.parseColor("#0E6C67"));
            aMap.addPolyline(options);
            //距离的计算

            //目的地的计算
            if(planEndLatLng != null){
                distanceToPlanEnd = AMapUtils.calculateLineDistance(privLatLng,planEndLatLng);
                if(distanceToPlanEnd <= 5 && isToStartVibrate){
                    VibratorUtil.Vibrate(this,new long[]{0,1500,2000,1500,2000,1500,2000,1500},false);
                    sportComplate();
                    isToStartVibrate =false;

                }
                Log.d("TAG","目的地起点：" + distanceToPlanEnd);
            }
            return AMapUtils.calculateLineDistance(privLatLng, curLocation);//米

        }


        @Override
        protected void onResume() {
            super.onResume();
            mapView.onResume();
            setNeedCheckPermission();
            Log.d("TAG", "onResume: " );
        }

        @Override
        protected void onPause() {
            super.onPause();
            mapView.onPause();
         /*   deactivate();*/
            Log.d("TAG", "onPause: ");
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            // 退出销毁Map
            mapView.onDestroy();
        }


        /**
         * 判断是否需要检测，防止不停的弹框
         */
        private boolean isNeedCheck = true;

        /**
         * 检查手机是否开启定位权限，没开启会提醒开启
         */
        private void setNeedCheckPermission() {

            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                if (isNeedCheck) {
                    checkPermissions(needPermissions);
                }
            }
        }

        /**
         * 检查定位权限
         *
         * @param permissions
         * @since 2.5.0
         */
        private void checkPermissions(String... permissions) {
            try {
                if (Build.VERSION.SDK_INT >= 23
                        && getApplicationInfo().targetSdkVersion >= 23) {
                    List<String> needRequestPermissonList = findDeniedPermissions(permissions);
                    if (null != needRequestPermissonList
                            && needRequestPermissonList.size() > 0) {
                        String[] array = needRequestPermissonList.toArray(new String[needRequestPermissonList.size()]);
                        Method method = getClass().getMethod("requestPermissions", new Class[]{String[].class,
                                int.class});

                        method.invoke(this, array, PERMISSON_REQUESTCODE);
                    }
                }
            } catch (Throwable e) {
            }
        }

        /**
         * 获取权限集中需要申请权限的列表
         *
         * @param permissions
         * @return
         * @since 2.5.0
         */
        private List<String> findDeniedPermissions(String[] permissions) {
            List<String> needRequestPermissonList = new ArrayList<String>();
            if (Build.VERSION.SDK_INT >= 23
                    && getApplicationInfo().targetSdkVersion >= 23) {
                try {
                    for (String perm : permissions) {
                        Method checkSelfMethod = getClass().getMethod("checkSelfPermission", String.class);
                        Method shouldShowRequestPermissionRationaleMethod = getClass().getMethod("shouldShowRequestPermissionRationale",
                                String.class);
                        if ((Integer) checkSelfMethod.invoke(this, perm) != PackageManager.PERMISSION_GRANTED
                                || (Boolean) shouldShowRequestPermissionRationaleMethod.invoke(this, perm)) {
                            needRequestPermissonList.add(perm);
                        }
                    }
                } catch (Throwable e) {

                }
            }
            return needRequestPermissonList;
        }






    public long getTimeInterval(Date startDate , Date endDate)  {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return endTime - startTime;

    }

    //设置定位图标
    public void setmLocationStyle() {
        MyLocationStyle locationStyle = new MyLocationStyle();
        SharedPreferences read = this.getSharedPreferences("personal_data", MODE_PRIVATE);
        Log.d("TAG", read.getString("state", null));
        if (read.getString("picture_path", null) != null) {
            String file_path = read.getString("picture_path", null);
            Bitmap bitmap0 = BitmapFactory.decodeFile(file_path);
            Bitmap bitmap1 = BitmapUtil.getCircleBitmap(bitmap0);
            Bitmap bitmap = BitmapUtil.changeBitmapSize(bitmap1, 60, 60);
            locationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
            /* locationStyle.strokeColor(Color.);*/
            /*locationStyle.strokeWidth(2);*/

            locationStyle.strokeColor(Color.argb(0, 0, 0, 0));
            locationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
            locationStyle.anchor(0.5f, 1.0f);
            /* locationStyle.strokeColor(Color.TRANSPARENT);*/ // 设置圆形的边框颜色
            //开启定位回调但不显示定位图标
          /* locationStyle.showMyLocation(false);*/
            aMap.setMyLocationStyle(locationStyle);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown())
        {
            marker.hideInfoWindow();

        }
        else
        {
            marker.showInfoWindow();
        }
        return false;
    }

    private void setFixedMarker(LatLng latLng, String title, String content,int picture) {
        Marker mFixedMarker ;
        markOptions = new MarkerOptions();
        markOptions.draggable(false);//设置Marker可拖动
        /*markOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.plan_end_point))).anchor(0.5f, 0.7f);*/
        markOptions.icon(BitmapDescriptorFactory.fromResource(picture)).anchor(0.5f, 0.7f);
        mFixedMarker = aMap.addMarker(markOptions);
        mFixedMarker.setPosition(latLng);
        mFixedMarker.setTitle(title);
        mFixedMarker.setSnippet(content);
        /*if (!TextUtils.isEmpty(content))
        {
            mGPSMarker.showInfoWindow();
        }*/
       /* mapView.invalidate();*/
    }


    private void setMarker(LatLng latLng, String title, String content) {
        if (mGPSMarker != null)
        {
            mGPSMarker.remove();
        }
        //获取屏幕宽高
        WindowManager wm = this.getWindowManager();
        int width = (wm.getDefaultDisplay().getWidth()) / 2;
        int height = ((wm.getDefaultDisplay().getHeight()) / 2) - 80;
        markOptions = new MarkerOptions();
        markOptions.draggable(false);//设置Marker可拖动
        markOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.start_point1)).anchor(0.5f, 0.7f);
        //设置一个角标
        mGPSMarker = aMap.addMarker(markOptions);
        //设置marker在屏幕的像素坐标
        mGPSMarker.setPosition(latLng);
        mGPSMarker.setTitle(title);
        mGPSMarker.setSnippet(content);
        //设置像素坐标
        mGPSMarker.setPositionByPixels(width, height);
        Log.d("TAG","设置目的地" + endAddressName);
     /*   if (!TextUtils.isEmpty(content))
        {
            mGPSMarker.showInfoWindow();
        }
        mMapView.invalidate();*/
    }


    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

    }

    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
            if(isCustomizedEndpoint){
                planEndLatLng = cameraPosition.target;
                double latitude = planEndLatLng.latitude;
                double longitude = planEndLatLng.longitude;
                Log.e("latitude", latitude + "");
                Log.e("longitude", longitude + "");
                getAddress(planEndLatLng);
            }

    }

    public void getAddress(final LatLng latLng) {
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 50, GeocodeSearch.AMAP);
        geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求


    }
  /*     * 逆地理编码回调*/
    @Override
    public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
        if (rCode == 1000) {
            if (result != null && result.getRegeocodeAddress() != null
                    && result.getRegeocodeAddress().getFormatAddress() != null && isCustomizedEndpoint) {

                endAddressName = result.getRegeocodeAddress().getFormatAddress(); // 逆转地里编码不是每次都可以得到对应地图上的opi
                Log.d("TAG","逆地理编码回调  得到的地址：" + endAddressName);
                if(count ==0){
                    setMarker(new LatLng(planEndLatLng.latitude + 5,planEndLatLng.longitude + 5), "终点", endAddressName);

                }
                else{
                setMarker(planEndLatLng, "终点", endAddressName);

                }
                TextView endpoint = findViewById(R.id.sport_map_endpoint);
                endpoint.setText(endAddressName);
                pathRecord.setPlanEndAddress(endAddressName);
//              mAddressEntityFirst = new AddressSearchTextEntity(addressName, addressName, true, convertToLatLonPoint(mFinalChoosePosition));
             count++;
            }
        }
    }

    /**
     * 地理编码查询回调
     */
    @Override
    public void onGeocodeSearched(GeocodeResult result, int rCode) {
    }


    public void  sportComplate(){
        Log.d("TAG","时间控件在结束前的字符串返回" + timer.getTimeString());
        String timestr = timer.getTimeString();
        pathRecord.setTimeFormatInString(timestr);//返回为null?
        timer.onRecordStop();
        endTime = new Date();
        pathRecord.setEndTime(endTime.getTime());
        pathRecord.setEndpoint(privLatLng);
        pathRecord.setDistance(distance);
        pathRecord.setDuration(timer.getChronometerSeconds());//使得结果时间于之前活动显示的一致

        pathRecord.setPathline(mPathLinePoints);
        BigDecimal distance1 = new BigDecimal(pathRecord.getDistance());
        BigDecimal duration1 = new BigDecimal(pathRecord.getDuration());
        BigDecimal divisorSec = new BigDecimal(1000);
        BigDecimal divisorMin = new BigDecimal(60000);
        //  BigDecimal multiplyMin = new BigDecimal(60);
        BigDecimal multiplyHour = new BigDecimal(3600);
        BigDecimal seconds = duration1.divide(divisorSec,3,ROUND_HALF_UP);
        BigDecimal speedSec = distance1.divide(seconds,4,ROUND_HALF_UP);//公里/秒
        //BigDecimal hours = duration1.divide(divisorHour,4,ROUND_HALF_UP);
        BigDecimal minutes = duration1.divide(divisorMin,4,ROUND_HALF_UP);
        pathRecord.setSpeed(speedSec.multiply(multiplyHour).doubleValue()) ;
        if(distance1.doubleValue() != 0)
            pathRecord.setDistribution(minutes.divide(distance1,4,ROUND_HALF_UP).doubleValue());
        else
            pathRecord.setDistribution(0.0);
        SharedPreferences read = context.getSharedPreferences("personal_data",MODE_PRIVATE);
        double weight = Double.parseDouble(read.getString("weight",""));
        double cal = weight * distance * 1.036;

        pathRecord.setCalorie(cal);
        Log.i("TAG",  " 当前错误热量" + pathRecord.getCalorie());
        SportRecord sportRecord = StepUtils.pathRecordToSportRecord(pathRecord);
        //如果没有运动轨迹，距离为0，不保存数据
        if(mPathLinePoints.size()!= 0) {
            sportRecord.saveInSQL(this);
        }
        mLocationClient.stopLocation();

        Intent intent = new Intent(this, SportResult_Activity.class);
        intent.putExtra("path_record", pathRecord);
        startActivityForResult(intent,1);//当结果页面点击返回时传出信息对应的请求码
        /*   Toast.makeText(SportMap_Activity.this, "点击完成" , Toast.LENGTH_SHORT).show();*/
        isRun = false;

    }



}

