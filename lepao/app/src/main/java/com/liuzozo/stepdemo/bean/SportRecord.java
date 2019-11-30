package com.liuzozo.stepdemo.bean;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.liuzozo.stepdemo.OtherFunction.MyDatabaseHelper;

import java.io.Serializable;

public class SportRecord implements Serializable {

    /**
     * 表示该字段是主键
     * <p>
     * 字段类型必须是字符串（String）或整数（byte，short，int或long）
     * 以及它们的包装类型（Byte,Short, Integer, 或 Long）。不可以存在多个主键，
     * 使用字符串字段作为主键意味着字段被索引（注释@PrimaryKey隐式地设置注释@Index）。
     */
    // 主键
    private Long id;

    //user ID  固定的一个值
    private int userId = 165;

    //运动距离
    private Double distance;

    //运动时长
    private Long duration;


    //运动轨迹
    // 因为数据库只能保存基本数据类型，所以大家需要报经纬度类的两个值转成以一个string 保存
    // 查询出来的时侯， 大家在给它两个经纬度值还原成一个经纬度类
    // 保存String 可以参考 StepUtils 工具类的 parseLatLngLocation（） 方法， 把一个经纬度的两个值 以逗号分割，经纬度之间以；分割
    private String pathLine;

    //运动开始点
    private String stratPoint;  // 起点经纬度类的俩个值的string

    //运动结束点
    private String endPoint;

    //运动开始时间
    private Long mStartTime;

    //运动结束时间
    private Long mEndTime;

    //消耗卡路里
    private Double calorie;

    //平均时速(公里/小时)
    private Double speed;

    //平均配速(分钟/公里)
    private Double distribution;

    //日期标记
    private String dateTag;

    private MyDatabaseHelper dbhelper;

    private String userName = "yhl";//预留字段
    private String str2;//预留字段
    private String str3;//预留字段

    public SportRecord(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getPathLine() {
        return pathLine;
    }

    public void setPathLine(String pathLine) {
        this.pathLine = pathLine;
    }

    public String getStratPoint() {
        return stratPoint;
    }

    public void setStratPoint(String stratPoint) {
        this.stratPoint = stratPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public Long getmStartTime() {
        return mStartTime;
    }

    public void setmStartTime(Long mStartTime) {
        this.mStartTime = mStartTime;
    }

    public Long getmEndTime() {
        return mEndTime;
    }

    public void setmEndTime(Long mEndTime) {
        this.mEndTime = mEndTime;
    }

    public Double getCalorie() {
        return calorie;
    }

    public void setCalorie(Double calorie) {
        this.calorie = calorie;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getDistribution() {
        return distribution;
    }

    public void setDistribution(Double distribution) {
        this.distribution = distribution;
    }

    public String getDateTag() {
        return dateTag;
    }

    public void setDateTag(String dateTag) {
        this.dateTag = dateTag;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String str1) {
        this.userName = str1;
    }

    public String getStr2() {
        return str2;
    }

    public void setStr2(String str2) {
        this.str2 = str2;
    }

    public String getStr3() {
        return str3;
    }

    public void setStr3(String str3) {
        this.str3 = str3;
    }

    public void saveInSQL(Context context){
        dbhelper = new MyDatabaseHelper(context, "SportData.db",null,1);
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("userId",userId + "");
        values.put("Distance",distance + "");
        values.put("Duration",duration + "");
        values.put("PathLinePoints",pathLine);
        values.put("StartPoint",stratPoint);
        values.put("EndPointLat",endPoint);
        values.put("StartTime", mStartTime + "");
        values.put("EndTime", mEndTime + "");
        values.put("Calorie",calorie + "");
        values.put("Speed",speed + "");
        values.put("mDistribution",distribution + "");
        values.put("mDateTag",dateTag );
        db.insert("SportRecord",null,values);
        Log.d("TAG", "数据库表格SportRecord已创建" );// values.put("userName",userName);一开始没有创建该字段

    }



}
