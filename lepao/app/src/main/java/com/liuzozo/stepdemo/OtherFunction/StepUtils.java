package com.liuzozo.stepdemo.OtherFunction;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Chronometer;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.SportRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class StepUtils {

    public static String parseLatLngLocation(LatLng latlng) {
        String str = "";
        str = String.valueOf(latlng.latitude) + "," + String.valueOf(latlng.longitude);
        return str;

    }

    public static LatLng parseStringToLatlng(String latlng){
        String[] list = latlng.split(",");
        return new LatLng(Double.parseDouble(list[0]),Double.parseDouble(list[1]));
    }

    public static  List<LatLng> getPathline(String pathLinePoint){

        List<LatLng> latLngArraylist = new ArrayList<>();

        String[] latlngList = pathLinePoint.split("/");

        for(int i = 0 ; i < latlngList.length ; i++){
            if(!latlngList[i].equals("")){
            String[] latlngSplit = latlngList[i].split(",");
            LatLng latLng = new LatLng(Double.parseDouble(latlngSplit[0]),Double.parseDouble(latlngSplit[1]));
            latLngArraylist.add(latLng);
            }
        }

        return latLngArraylist;


    }

    public static SportRecord pathRecordToSportRecord(PathRecord pathRecord) {
        SportRecord sportRecord = new SportRecord();
        List<LatLng> pathline = pathRecord.getPathline();
        long size = pathline.size();
        String PathLine = "";
        for (int i = 0; i < size; i++) {
            PathLine += parseLatLngLocation(pathline.get(i)) + "/";

        }

        sportRecord.setUserName(PathRecord.getUserName());
        sportRecord.setUserId(165);
        sportRecord.setDistance(pathRecord.getDistance());
        sportRecord.setDuration(pathRecord.getDuration());
        sportRecord.setPathLine(PathLine);
        sportRecord.setStratPoint(parseLatLngLocation(pathRecord.getStartpoint()));
        sportRecord.setEndPoint(parseLatLngLocation(pathRecord.getEndpoint()));
        sportRecord.setmStartTime(pathRecord.getStartTime());
        sportRecord.setmEndTime(pathRecord.getEndTime());
        sportRecord.setCalorie(pathRecord.getCalorie());
        sportRecord.setSpeed(pathRecord.getSpeed());
        sportRecord.setDistribution(pathRecord.getDistribution());
        sportRecord.setDateTag(pathRecord.getDateTag());

        return sportRecord;
    }

    public static  String timeFormat(long passtime) {
        long seconds, minutes;
        long second, minute, hour;

        String sec, min, hor;
        //当前秒数
        seconds = passtime / 1000;
        second = seconds % 60 - 3;
        //当前分钟数
        minutes = seconds / 60;
        minute = minutes % 60;
        //当前小时数
        hour = minutes / 60;
        if (second < 0)
            sec = "00";
        else if (second < 10)
            sec = "0" + second;
        else
            sec = "" + second;

        if (minute <= 0)
            min = "00";
        else if (minute < 10)
            min = "0" + minute;
        else
            min = "" + minute;

        if (hour <= 0)
            hor = "00";
        else if (hour < 10)
            hor = "0" + hour;
        else
            hor = hour + "";

        return hor + ":" + min + ":" + sec;

    }

    public static long getTimeInterval(Date startDate, Date endDate) {
        long startTime = startDate.getTime();
        long endTime = endDate.getTime();
        return endTime - startTime;

    }

    public static long getChronometerSeconds(Chronometer cmt) {
        long totalss = 0;
        String string = cmt.getText().toString();
        if (string.length() == 7) {

            String[] split = string.split(":");
            String string2 = split[0];
            int hour = Integer.parseInt(string2);
            int Hours = hour * 3600;
            String string3 = split[1];
            int min = Integer.parseInt(string3);
            int Mins = min * 60;
            int SS = Integer.parseInt(split[2]);
            totalss = Hours + Mins + SS;
            return totalss;
        } else if (string.length() == 5) {

            String[] split = string.split(":");
            String string3 = split[0];
            int min = Integer.parseInt(string3);
            int Mins = min * 60;
            int SS = Integer.parseInt(split[1]);

            totalss = Mins + SS;
            return totalss;
        }
        return totalss;


    }

    public int getMonth(String month){
        int num = 0;
        switch (month){
            case "Jan":
                num = 1;
                break;
            case "Feb":
                num = 2;
                break;
            case "Mar":
                num = 3;
                break;
            case "Apr":
                num = 4;
                break;
            case"May":
                num = 5;
                break;
            case"Jun":
                num= 6;
                break;
            case"Jul":
                num= 7;
                break;
            case "Aug":
                num = 8 ;
                break;
            case "Sep":
                num = 9 ;
                break;
            case "Oct":
                num = 10;
                break;
            case"Nov":
                num = 11;
                break;
            case "Dec":
                num = 12;
                break;
        }

        return  num;
    }

    public String getMonthString(int month){
        String num ="";
        switch (month){
            case 1:
                num = "Jan";
                break;
            case 2 :
                num = "Feb";
                break;
            case 3:
                num = "Feb";
                break;
            case 4:
                num = "Apr";
                break;
            case 5 :
                num = "May";
                break;
            case 6:
                num= "Jun";
                break;
            case 7:
                num= "Jul";
                break;
            case 8:
                num = "Aug";
                break;
            case 9:
                num = "Sep";
                break;
            case 10:
                num = "Oct";
                break;
            case 11:
                num = "Nov";
                break;
            case 12:
                num = "Dec";
                break;
        }

        return  num;
    }


    public static  String[] getRecentDays(int days){
        String[] recentDays = new String[days];
        final int period = 86400;
        Date today = new Date();
        Long todaySeconds = today.getTime()/1000;


        for(int i = 0 ; i < days; i++ ){
            Long lastdaySeconds = todaySeconds - period * i;
            Date lastday = new Date(lastdaySeconds * 1000);
            String dateStr = lastday.toString();
            String[] array = dateStr.split(" ");
            recentDays[i] = array[1] + array[2];

        }
       return recentDays;
    }

    public static ArrayList<double[]> getMinDisCal(Context context,int days){
        ArrayList<double[]> data = new ArrayList();
        String[] date = getRecentDays(days);
        for(int i = 0 ;i < days ; i++){
            int count = 0;
            double[] searchDateData = new double[5];
            String searchDate = date[i];
            double distance = 0, mintues = 0,calorie = 0, speed = 0 ;
            MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "SportData.db", null, 1);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            Cursor cursor = db.query("SportRecord", new String[]{"Distance", "Duration",  "Calorie", "mDateTag","Speed"}, "mDateTag like ?",
                    new String[]{"%" + searchDate + "%"}, null, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    distance += Double.parseDouble(cursor.getString(0));

                    mintues += (double) Integer.parseInt(cursor.getString(1)) / 60000;

                    calorie += Double.parseDouble(cursor.getString(2));

                    speed += Double.parseDouble(cursor.getString(4));

                    count++;


                } while (cursor.moveToNext());

                searchDateData[0] = distance;
                searchDateData[1] = mintues;
                searchDateData[2] = calorie;
                searchDateData[3] = speed ;
                searchDateData[4] = (double)count;
            }
            data.add(i,searchDateData);
        }

        return data;
    }

    public static double findMaxInDoubleArray(double[] array0){
        double[] array = new double[array0.length] ;
        System.arraycopy(array0,0,array,0,array0.length);
        Arrays.sort(array);
        return array[array.length - 1];

    }
}





