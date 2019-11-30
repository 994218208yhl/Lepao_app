package com.liuzozo.stepdemo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.liuzozo.stepdemo.OtherFunction.AlarmService;
import com.liuzozo.stepdemo.OtherFunction.PickUtils;
import com.liuzozo.stepdemo.ui.UIHelperUtil;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 *  运动计划设置页面
 *  弹框  设置每天跑步目标公里， 是否提醒， 提醒时间
 *
 */
public class PlanSetting_Activity extends AppCompatActivity implements View.OnClickListener {
    private static Context sContext = null;
    private TextView planDistance, planCal, planRice, planHam, planChicken,planWeight,planTime;
    private SwitchButton informBtn;
    private Button btnSave;
    private PickUtils pickUtils;
    private String mTime;
    private LinearLayout ll_plan_time;
    private SeekBar progressDistance, progressWeight;
    private boolean isInform = false;
    private double calorie ,accountWeight = 0;




    public static Context getContext() {
        return sContext;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paln_setting);
        pickUtils = new PickUtils(this);
        initView();
        init();
        setBar();
        setBtn();
    }

    private void initView() {
        planDistance = findViewById(R.id.plan_distance);
        planCal = findViewById(R.id.plan_cal);
        planRice = findViewById(R.id.plan_rice);
        planChicken =findViewById(R.id.plan_chicken);
        planHam = findViewById(R.id.plan_ham);
        planTime = findViewById(R.id.plan_time);
        ll_plan_time = findViewById(R.id.ll_plan_time);
        planWeight = findViewById(R.id.plan_weight);
        btnSave = findViewById(R.id.plan_save);
        informBtn = findViewById(R.id.switchButton);
        progressDistance = findViewById(R.id.progress_distance);
        progressWeight = findViewById(R.id.progress_weight);
        ll_plan_time.setVisibility(View.INVISIBLE);
      /*  planRunGoal = findViewById(R.id.plan_rungoal);
        planWeiGoal = findViewById(R.id.plan_weightgoal);*/
        SharedPreferences read = getSharedPreferences("plan_data", MODE_PRIVATE);
        SharedPreferences account = getSharedPreferences("personal_data", MODE_PRIVATE);
        if((account.getString("weight",null)) != null)
        accountWeight = Double.parseDouble(account.getString("weight",null));
       /* Log.d("TAG",read.getString("state",null));*/
        Log.d("TAG",read.getString("planWeight","没有储存体重"));
            planWeight.setText(read.getString("planWeight","0"));
            progressWeight.setProgress((int)(Double.parseDouble(read.getString("planWeight","0"))));
            planDistance.setText(read.getString("planDistance","0"));
            progressDistance.setProgress((int)(Double.parseDouble(read.getString("planDistance","0"))));
            planTime.setText(read.getString("planTime","00:00"));
            planRice.setText(read.getString("planRice","0"));
            planChicken.setText(read.getString("planChicken","0"));
            planHam.setText(read.getString("planHam","0"));
            planCal.setText(read.getString("planCal","0"));



    }

    private void init() {
        //date.setOnClickListener(this);
        //time.setOnClickListener(this);
        ll_plan_time.setOnClickListener(this);
        informBtn.setOnClickListener(this);
        btnSave.setOnClickListener(this);
      /*  progressWeight.setOnClickListener(this);
        progressDistance.setOnClickListener(this);*/
    }
      private void setBtn(){
          informBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

              @Override
              public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                  isInform = true;
                  ll_plan_time.setVisibility(View.VISIBLE);

              }
          });
      }
      private void setBar() {
          progressDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  Log.i("TAG","onProgressChanged=" +progress);
                  Log.d("TAG","jindu" + progress);
                  String dis = progress + "";
                  planDistance.setText(dis);
                  calorie = accountWeight * Double.parseDouble(planDistance.getText().toString()) * 1.036;
                  planRice.setText(String.format("%.1f",calorie/116)+ "碗");
                  planCal.setText("约" + String.format("%.1f",calorie ) + "千卡");
                  planHam.setText(String.format("%.1f",calorie/450 ) + "个");
                  planChicken.setText(String.format("%.1f",calorie/214 ) + "只");

                  Log.d("TAG","设置距离为"+ planDistance.getText().toString());

                  ;

              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {
              }

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {
              }
          });

          progressWeight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
              @Override
              public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                  Log.i("TAG","onProgressChanged=" +progress);
                  String wei = progress + "";
                  planWeight.setText(wei);


              }

              @Override
              public void onStartTrackingTouch(SeekBar seekBar) {
              }

              @Override
              public void onStopTrackingTouch(SeekBar seekBar) {
              }
          });


      }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.ll_plan_time:
                pickUtils.setTime(planTime);

                break;
            case R.id.plan_save:
               /* mDistance = distance.getText().toString();*/
                double distance = Double.parseDouble(planDistance.getText().toString());
                double weight = Double.parseDouble(planWeight.getText().toString());
                SharedPreferences.Editor editor = getSharedPreferences("plan_data", MODE_PRIVATE).edit();
                editor.putString("planDistance",distance+"");
                editor.putString("planWeight",weight+"");
                editor.putString("planRice",planRice.getText().toString());
                editor.putString("planCal",planCal.getText().toString());
                editor.putString("planHam",planHam.getText().toString());
                editor.putString("planChicken",planChicken.getText().toString());
                editor.putString("planTime",planTime.getText().toString());
                Log.d("TAG","设置体重"+ distance);
                editor.apply();
                Toast.makeText(this, "计划保存成功 " , Toast.LENGTH_SHORT).show();
                if(isInform) {
                    Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    String mDate = year + "-" + month + "-" + day + "";
                    mTime = planTime.getText().toString() + ":00";
                    Log.e("xx", "日期= " + mDate + "   时间= " + mTime);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date;
                    long value = 0 ;
                    String str_date = mDate + " " + mTime;
                    try {
                        date = sdf.parse(str_date);
                        value = date.getTime();
                        System.out.println("当前设置时间:" + value);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.e("str_time=", str_date);
                    Log.e("value=", value + "");
                    long value2 = System.currentTimeMillis();
                    if (value <= value2) {
                        value = value +  86400000;
                        /*Toast.makeText(getApplicationContext(), "选择时间不能小于当前系统时间", Toast.LENGTH_LONG).show();*/
                    }
                    long delaytime = (long) (value - value2);
                    Intent start = new Intent(this, AlarmService.class);
                    start.putExtra("delay", delaytime);
                    start.putExtra("ditance",distance);
                    start.putExtra("time",mTime);
                    start.putExtra("weight",weight);
                    startService(start);

                }

                break;

        }
    }


}