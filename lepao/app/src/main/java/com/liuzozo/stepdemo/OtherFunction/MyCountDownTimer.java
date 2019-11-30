package com.liuzozo.stepdemo.OtherFunction;

import android.animation.ValueAnimator;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.view.animation.AlphaAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Animation;


import com.liuzozo.stepdemo.SportMap_Activity;
import com.liuzozo.stepdemo.utils.*;

public class MyCountDownTimer  extends CountDownTimer {
    public static final int TIME_COUNT = 4000;
    private TextView btn;
    private String endStrRid;
    private Window window;

    public MyCountDownTimer(long millisInFuture, long countDownInterval,TextView btn, String endStrRid,Window window) {
        super(millisInFuture, countDownInterval);
        this.btn = btn;
        this.endStrRid = endStrRid;
        this.window = window;
    }

    public MyCountDownTimer(TextView btn,Window window) {
        super(TIME_COUNT, 1000);
        this.btn = btn;

    }

    public Window getWindow(){
        return window;
    }
    public TextView getTextview() {
        return btn;

    }



    public void onFinish(){

        btn.setText(endStrRid);
        btn.setEnabled(true);
        btn.setVisibility(View.INVISIBLE);
        delay(500);
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = 1.0f;
        window.setAttributes(params);


    }


    public void onTick(long millisUntilFinished) {

       /* if(millisUntilFinished / 1000  == 3)
           dimBackground(1.0f,3.0f);*/
        btn.setEnabled(false);
        //每隔一秒修改一次UI
        btn.setText(millisUntilFinished / 1000 + "");

        // 设置透明度渐变动画
        final AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        //设置动画持续时间
        alphaAnimation.setDuration(500);
        btn.startAnimation(alphaAnimation);

        // 设置缩放渐变动画
        final ScaleAnimation scaleAnimation = new ScaleAnimation(0.5f, 2f, 0.5f, 2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(1000);
        btn.startAnimation(scaleAnimation);


    }
    private void delay(int ms){
        try {
            Thread.currentThread();
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
