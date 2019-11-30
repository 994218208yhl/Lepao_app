package com.liuzozo.stepdemo.OtherFunction;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/*import com.liuzozo.stepdemo.utils.MyDatabaseHelper;*/
/*
数据库里面保存:
        *    当前时间的时间戳，  距离， 时长， 开始时间， 结束时间， 开始位置的经纬度， 结束位置的经纬度，路线的经纬度
        *    根据体重计算的卡路里， 平均时速（公里/小时） 平均配速（分钟/公里）
*/
public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final String Create_SportRecord = "create table SportRecord("
            +"id integer primary key autoincrement,"
            + "userId text, "
            +"Distance text, "
            +"Duration text, "
            +"PathLinePoints text, "//运动轨迹
            +"StartPoint text, "
            +"EndPointLat text, "
            +"StartTime text, "
            +"EndTime text, "
            +"Calorie text, "//消耗卡路里
            +"Speed text, " //平均时速(公里/小时)
            +"mDistribution text, "//平均配速(分钟/公里)
            +"mDateTag text)";// +"userName text, "
    private Context mContext;

    public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory , int version){
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(Create_SportRecord);
        Toast.makeText(mContext,"Sportdata Created",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldversion,int newversion){

    }




}
