package com.liuzozo.stepdemo.fragment;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.liuzozo.stepdemo.OtherFunction.MapUtils;
import com.liuzozo.stepdemo.OtherFunction.MyApplication;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.ui.BitmapUtil;
import com.liuzozo.stepdemo.utils.PathSmoothTool;
import com.liuzozo.stepdemo.utils.StepUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.liuzozo.stepdemo.R.drawable.move_icon3;

/***
 *  运动地图记录详情页
 *  1. 在地图上根据传来的PathRecord 值的经纬度等信息，画出轨迹等
 */
public class SportRecordDetails_Map_Fragment extends Fragment implements LocationSource {

    // 地图
    private AMap aMap;
    private MapView mapView;
    private PathRecord pathRecord;
    private  SmoothMoveMarker moveMarker;
    List<LatLng> pathoptimizeList;
    private TextView moveDisatance, moveTime;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details_map, container,
                false);
        context = MyApplication.getContext();

        // 初始化地图组件
        mapView =  view.findViewById(R.id.detalsMap);
        mapView.onCreate(savedInstanceState);
        moveDisatance = view.findViewById(R.id.move_distance);
        moveTime = view.findViewById(R.id.move_time);


        initSendData();
        initMap();
        moveMaker();
      /*  drawPath();*/
        return view;
    }

    private void initSendData() {

        // 得到转到这个 fragmnet 的值
       Bundle receiverBundle = getArguments();
       if (receiverBundle != null) {
          pathRecord = receiverBundle.getParcelable("SPORT_DATA");
       }
       moveDisatance.setText("公里： " +decimalFormat.format( pathRecord.getDistance()) +"km" );
       moveTime.setText("时长： " + StepUtils.formatseconds((long)(pathRecord.getDuration()/ 1000)));
    }

    /**
     * 地图开始
     */
    private void initMap() {
        if (aMap == null) {
            aMap = mapView.getMap();
            // 缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(19));
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
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {


    }

    @Override
    public void deactivate() {

    }

    private  void drawPath(){
        List<LatLng> originlist = pathRecord.getPathline();
        PathSmoothTool mpathSmoothTool = new PathSmoothTool();
        mpathSmoothTool.setIntensity(4);
        pathoptimizeList = mpathSmoothTool.pathOptimize(originlist);
        if (pathoptimizeList != null && pathoptimizeList.size()>0) {
            LatLngBounds bounds=new LatLngBounds.Builder()
                    .include(pathoptimizeList.get(0))
                    .include(pathoptimizeList.get(pathoptimizeList.size()-1)).build();
            aMap.addPolyline(new PolylineOptions().addAll(pathoptimizeList).color(Color.GREEN));
            aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        }
    }

    private void addMarkerToMap(LatLng latLng) {
        /* LatLng latLng = new LatLng(39.9081728469, 116.3867845961);*/
        /*markerOption.draggable(true);*/
        /* marker.showInfoWindow();*/
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title("起点").snippet("北京市东城区东长安街");
        /* markerOption.draggable(true);//设置Marker可拖动*/
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(),R.drawable.map_marker)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        /* markerOption.setFlat(true);//设置marker平贴地图效果*/
        final Marker marker = aMap.addMarker(markerOption);

    }

    private  void moveMaker(){
        if( pathRecord.getPathline().size()!= 0){
            PathSmoothTool mpathSmoothTool = new PathSmoothTool();
            pathoptimizeList = mpathSmoothTool.pathOptimize(pathRecord.getPathline());
            List<LatLng> points = pathoptimizeList;
            addPolylineInPlayGround();
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < points.size(); i++) {
                b.include(points.get(i));
            }

            LatLngBounds bounds = b.build();
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            moveMarker = new SmoothMoveMarker(aMap);
            moveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.drawable.move_icon1));
            SharedPreferences read = context.getSharedPreferences("personal_data", MODE_PRIVATE);
            Log.d("TAG", read.getString("state", null));
            if (read.getString("picture_path", null) != null) {
                String file_path = read.getString("picture_path", null);
                Bitmap bitmap0 = BitmapFactory.decodeFile(file_path);

                Bitmap bitmap1 = BitmapUtil.getCircleBitmap(bitmap0);
                Bitmap bitmap = BitmapUtil.changeBitmapSize(bitmap1, 80, 80);
                moveMarker.setDescriptor(BitmapDescriptorFactory.fromBitmap(bitmap));
            }

            moveMarker.setPoints(points);//设置平滑移动的轨迹list
            moveMarker.setTotalDuration(10);
            /* aMap.setInfoWindowAdapter(infoWindowAdapter);*///设置平滑移动的总时间

            LatLng drivePoint = points.get(0);
            Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
            points.set(pair.first, drivePoint);
            List<LatLng> subList = points.subList(pair.first, points.size());

            // 设置滑动的轨迹左边点
            moveMarker.setPoints(subList);
// 设置滑动的总时间
            moveMarker.setTotalDuration(40);
// 开始滑动
            moveMarker.startSmoothMove();
        }
    }

    private void addPolylineInPlayGround() {

        List<Integer> colorList = new ArrayList<Integer>();
        Log.d("TAG","修改轨迹线颜色");

        aMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.drawable.move_pathline))//setCustomTextureList(bitmapDescriptors)
                .addAll(pathoptimizeList));
        //设置终点起点图标
        MapUtils.addMarkerToMap(pathRecord.getStartpoint(),aMap,getActivity(),"起点","",R.mipmap.start_point);
        MapUtils.addMarkerToMap(pathRecord.getEndpoint(),aMap,getActivity(),"终点","",R.mipmap.end_point);

               /* .useGradient(true)
                .width(18));*/
    }
}
