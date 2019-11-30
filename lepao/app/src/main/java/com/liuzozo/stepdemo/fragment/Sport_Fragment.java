package com.liuzozo.stepdemo.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.DragAndDropPermissions;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.liuzozo.stepdemo.OtherFunction.CompletedView;

import com.liuzozo.stepdemo.OtherFunction.CompletedView;
import com.liuzozo.stepdemo.OtherFunction.MyApplication;
import com.liuzozo.stepdemo.OtherFunction.MyDatabaseHelper;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.SportMap_Activity;

import java.util.Date;

/**
 *  点击开发运动的 界面
 *  需要 查询数据库，查询出所有的跑步次数，跑步总公里，总时间
 */
public class Sport_Fragment extends Fragment {

    // 开始运动按钮
    Button startBtn;
    Context context;
    CompletedView circleViewDistance, circleViewTime;
    double[] datalist;
    View view;
    TextView frequency;
    TextView notesText1, notesText2;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       view = inflater
                .inflate(R.layout.fragment_sport, container, false);

        initView(view);
        context = MyApplication.getContext();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle saveInstanceState){
        super.onActivityCreated(saveInstanceState);
        datalist = getData();
        circleViewDistance = (CompletedView) view.findViewById(R.id.total_distance);
        circleViewDistance.setmTotalProgress(30.0);
        circleViewDistance.setUnit("Km");
        circleViewDistance.setProgress(datalist[0]);
        circleViewTime = (CompletedView) view.findViewById(R.id.total_time);
        circleViewTime.setmTotalProgress(240);
        circleViewTime.setUnit("Min");
        circleViewTime.setProgress(datalist[1]);
        frequency.setText((int)datalist[2]+"");
        notesText1 = view.findViewById(R.id.notes_text1);
        if((int)datalist[2] == 0)
            notesText1.setText("1.今天还没有运动,点击下面按钮,进入跑步模式吧");
        else
            notesText1.setText("1.明天也要像今天一样运动哦");




    }

    public void initView(View view) {
        startBtn = (Button) view.findViewById(R.id.btnStart);
        frequency = view.findViewById(R.id.today_fre);


        startBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SharedPreferences read = context.getSharedPreferences("personal_data", Context.MODE_PRIVATE);
                String weight = read.getString("weight", "");
                Log.d("TAG", "体重" + weight);
                String height = read.getString("height", "");
                if (weight.equals("") || height.equals("")|| weight.equals("0.0")|| height.equals("0.0")) {
                    //弹出对话框告知必须设置身高体重
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                    dialog.setTitle("Tips");
                    dialog.setMessage("请先到个人账户界面设置身高体重，否则无法开启跑步模式");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialog.show();
                } else {
                    Intent goSportMapIntent = new Intent(getActivity(),
                            SportMap_Activity.class);
                    startActivity(goSportMapIntent);
                }
            }
        });
    }

    private double[] getData() {
        double[] data = new double[3];
        Date date = new Date();
        String datestr = date.toString();
        String[] array = datestr.split(" ");
        String dateSearch = array[1] + array[2];
        double distance = 0, mintues = 0, count = 0;
        MyDatabaseHelper dbHelper = new MyDatabaseHelper(context, "SportData.db", null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.query("SportRecord", new String[]{"Distance", "Duration", "PathLinePoints", "StartPoint",
                        "EndPointLat", "StartTime", "EndTime", "Calorie", "Speed", "mDistribution", "mDateTag"}, "mDateTag like ?",
                new String[]{"%" + dateSearch + "%"}, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                distance += Double.parseDouble(cursor.getString(0));

                mintues += (double) Integer.parseInt(cursor.getString(1)) / 60000;

                count += 1;

                data[0] = distance;
                data[1] = mintues;
                data[2] = count;


            } while (cursor.moveToNext());



        }

        return data;
    }


}
