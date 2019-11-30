package com.liuzozo.stepdemo.OtherFunction;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.liuzozo.stepdemo.R;
import com.amap.api.maps.AMap;

public class MapUtils {

    public static void addMarkerToMap(LatLng latLng, AMap aMap, Context context,String title,String snippet,int pictureId) {
        /* LatLng latLng = new LatLng(39.9081728469, 116.3867845961);*/
        /*markerOption.draggable(true);*/
        /* marker.showInfoWindow();*/
        MarkerOptions markerOption = new MarkerOptions();
        markerOption.position(latLng);
        markerOption.title(title).snippet(snippet);
        /* markerOption.draggable(true);//设置Marker可拖动*/
        markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(context.getResources(), pictureId)));
        // 将Marker设置为贴地显示，可以双指下拉地图查看效果
        /* markerOption.setFlat(true);//设置marker平贴地图效果*/
         Marker marker = aMap.addMarker(markerOption);

    }
}
