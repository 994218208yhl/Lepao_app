package com.liuzozo.stepdemo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.utils.StepUtils;

import java.text.DecimalFormat;

/***
 *  运动记录详情页 --- 不带地图的页面
 *  1. 根据传来的PathRecord 值的显示一些信息，见PPT 该页面
 */
public class SportRecordDetails_Fragment extends Fragment {
    private PathRecord pathRecord;
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");
    private TextView tvDistance, tvDuration,tvSpeed,tvDistribution,tvCalorie;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sport_details, container,
                false);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvDuration = view.findViewById(R.id.tvDuration);
        tvSpeed = view.findViewById(R.id.tvSpeed);
        tvDistribution = view.findViewById(R.id.tvDistribution);
        tvCalorie = view.findViewById(R.id.tvCalorie);

         initSendData();
         initSportData();
        return view;
    }

    private void initSendData() {

        // 得到转到这个 fragmnet 的值
        Bundle receiverBundle = getArguments();
        if (receiverBundle != null) {
            pathRecord = receiverBundle.getParcelable("SPORT_DATA");
        }
    }

    private  void initSportData(){


        tvDistance.setText(decimalFormat.format(pathRecord.getDistance()));
        tvDuration.setText(StepUtils.formatseconds((long)(pathRecord.getDuration()/ 1000)));
        tvSpeed.setText(decimalFormat.format(pathRecord.getSpeed()));
        tvDistribution.setText(decimalFormat.format(pathRecord.getDistribution()));
        tvCalorie.setText(intFormat.format(pathRecord.getCalorie()));

    }
}
