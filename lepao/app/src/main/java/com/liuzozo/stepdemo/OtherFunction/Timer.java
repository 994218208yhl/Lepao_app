package com.liuzozo.stepdemo.OtherFunction;

import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.TextView;

import com.liuzozo.stepdemo.R;

import java.math.BigDecimal;

public class Timer {
    private Chronometer recordChronometer;
    private long recordingTime = 0 , duration = 0;// 记录下来的总时间
    private Window window;
    private  TextView countdown;
    private long chronometerSeconds;


    public Timer(Chronometer recordChronometer, Window window, TextView countdown) {
        this.recordChronometer = recordChronometer;
        this.window = window;
        this.countdown = countdown;
        Log.d("TAG", "chronometer 创建成功");
    }

    public Chronometer getRecordChronometer(){
        return recordChronometer;
    }

    public String getTimeString(){
        return  recordChronometer.getText().toString();

    }

    public long getDuration(){

        return duration;
    }


    public void onRecordFirstStart() {
        final int Update_TEXT = 1;
        countdown();
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Update_TEXT:
                        BigDecimal elapsedTime = new BigDecimal(SystemClock.elapsedRealtime() - recordChronometer.getBase());
                        BigDecimal divisor = new BigDecimal(60000);
                      /*  int hour = elapsedTime.divide(divisor,4,BigDecimal.ROUND_HALF_UP).intValue();*/
                        long totalSeconds = StepUtils.getChronometerSeconds(recordChronometer);
                        long hour = totalSeconds /3600 ;
                        /*int hour = (int) ((SystemClock.elapsedRealtime() - recordChronometer.getBase()) / 1000 / 60);*/
                        recordChronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);
                        String hourStr = ((hour >= 10)? hour + "": "0" + hour);
                        recordChronometer.setFormat(hourStr + ":%s");
                       /* recordChronometer.setFormat("0" + String.valueOf(hour) + ":%s");*/
                        recordChronometer.start();
                        break;
                    default:
                        break;

                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                long base = SystemClock.elapsedRealtime();
                do {

                } while (SystemClock.elapsedRealtime() - base <= 3000);
                Message message = new Message();
                message.what = Update_TEXT;
                handler.sendMessage(message);

            }
        }).start();


}


    public void onRecordContinue() {
        // 跳过已经记录了的时间，起到继续计时的作用
        recordChronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);
        BigDecimal elapsedTime = new BigDecimal(SystemClock.elapsedRealtime() - recordChronometer.getBase());
        BigDecimal divisor = new BigDecimal(60000);
        long totalSeconds = StepUtils.getChronometerSeconds(recordChronometer);
        long hour = totalSeconds /3600 ;
//        int hour = elapsedTime.divide(divisor,4,BigDecimal.ROUND_HALF_UP).intValue();
       /* int hour = (int) (((SystemClock.elapsedRealtime() - recordChronometer.getBase()) /(1000 * 3600)));*/
        String hourStr = ((hour >= 10)? hour + "": "0" + hour);
        recordChronometer.setFormat(hourStr + ":%s");
        recordChronometer.start();

        //* Log.d("TAG", "chronometer 开始计时");*/

    }
    public void onRecordPause() {
        recordChronometer.stop();
        recordingTime = SystemClock.elapsedRealtime()
                - recordChronometer.getBase();// 保存这次记录了的时间

    }

    public void onRecordStop() {
        duration = SystemClock.elapsedRealtime()
                - recordChronometer.getBase();
        recordingTime = 0;
        recordChronometer.stop();
        /*recordChronometer.setBase(SystemClock.elapsedRealtime());*/
    }

    private void countdown (){
      /*  final Window window = myCountDownTimer.getWindow();*/
        /*TextView countdown = myCountDownTimer.getTextview();*/
        countdown.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        MyCountDownTimer myCountTimer = new MyCountDownTimer(4000,
                1000, countdown, "", window);
        dimBackground(1.0f, 0.4f);
        myCountTimer.start();
    }
    //背景变暗
    private void dimBackground(final float from, final float to) {
       /* final Window window = myCountDownTimer.getWindow();*/
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = to;
        window.setAttributes(params);
        Log.d("TAG", "cuurent value is " + "zhixing");


    }

    public  long  getChronometerSeconds() {
        long totalss = 0;
        String string = recordChronometer.getText().toString();
        String[] split = string.split(":");
        String string2 = split[0];
        int hour = Integer.parseInt(string2);
        int Hours = hour * 3600;
        String string3 = split[1];
        int min = Integer.parseInt(string3);
        int Mins = min * 60;
        int SS = Integer.parseInt(split[2]);
        totalss = Hours + Mins + SS;
        chronometerSeconds = totalss * 1000;
        return chronometerSeconds;
    }

}
