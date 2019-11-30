package com.liuzozo.stepdemo.OtherFunction;

import android.annotation.TargetApi;
import android.app.*;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.WelcomeActivity;

import java.util.Timer;
import java.util.TimerTask;

public class AlarmService extends Service {
    private static final String TAG="AlarmService";
    private static final String ID="channel_1";
    private static final String NAME="前台服务";
    private  Timer timer;
    private final long period = 86400000;

    public AlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d (TAG,"onBind");

        throw new UnsupportedOperationException ("Not yet implemented");
    }

    @Override
    public void onCreate(){
        super.onCreate ();
        Log.d (TAG,"onCreate");
        if(Build.VERSION.SDK_INT>=26){
            /*setForeground();*/
        }else{

        }
    }

    @Override
    public void onDestroy(){
        stopForeground(true);
        super.onDestroy ();
        Log.d (TAG,"onDestroy");
    }

    @Override
    @TargetApi (26)
    public int onStartCommand(final Intent intent, int flags, int startId){
        Log.d(TAG,"onStartCommand" + "yes");
        long delay = intent.getLongExtra("delay",0);
        double distance = intent.getDoubleExtra("distance",0);
        double weight = intent.getDoubleExtra("weight",0);
        String time = intent.getStringExtra("time");
        String[] array = time.split(":");
        final String contentText = "记得完成今天" + array[0] + "点" + array[1] + "分" + "的运动计划哦" + "\n每天一点的努力就会达到" + weight + "kg的目标";
        if (null == timer) {
            timer = new Timer();
        }

        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                NotificationManager manager=(NotificationManager)getSystemService (NOTIFICATION_SERVICE);
                NotificationChannel channel=new NotificationChannel (ID,NAME,NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel (channel);
                /*   Intent start = */
                Intent notificationIntent = new Intent(AlarmService.this,
                        WelcomeActivity.class);// 点击跳转位置
                PendingIntent contentIntent = PendingIntent.getActivity(
                        AlarmService.this, 0, notificationIntent, 0);
                Notification notification=new Notification.Builder (AlarmService.this,ID)
                        .setContentIntent(contentIntent)
                        .setContentTitle ("运动通知")
                        .setContentText(contentText)// 下拉通知啦内容*/
                        .setAutoCancel(true)//用户触摸时，自动关闭
                        .setVibrate(new long[] { 0, 2000, 1000, 4000 })// 震动需要真机测试-延迟0秒震动2秒延迟1秒震动4秒
                        .setSmallIcon (R.mipmap.ic_launcher)
                        .setLargeIcon (BitmapFactory.decodeResource (getResources (), R.mipmap.app))
                        .build ();
                manager.notify((int) System.currentTimeMillis(), notification);
               /* startForeground (1,notification);*/
            }
        }, delay,period);
        return super.onStartCommand (intent,flags,startId);
    }

/*    @TargetApi (26)
    private void setForeground(){

        Log.d(TAG,"onStartForegroud" + "yes");
    }*/
}
