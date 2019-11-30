package com.liuzozo.stepdemo.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.utils.StepUtils;

import java.text.DecimalFormat;
import java.util.List;

/*public class SportCalendarAdapter extends BaseQuickAdapter<PathRecord, BaseViewHolder> {

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");

    public SportCalendarAdapter(int layoutResId, @Nullable List<PathRecord> data) {
        super(layoutResId, data);
    }*/

public class SportCalendarAdapter extends BaseItemDraggableAdapter<PathRecord, BaseViewHolder> {

    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    private DecimalFormat intFormat = new DecimalFormat("#");
    public SportCalendarAdapter(int layoutResid, @Nullable List<PathRecord> data) {
        super(layoutResid, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PathRecord item) {
        // 子item 布局上的三个控件
        helper.setText(R.id.distance, decimalFormat.format(item.getDistance() ));
        helper.setText(R.id.duration, StepUtils.formatseconds((long)(item.getDuration()/ 1000)));
        helper.setText(R.id.calorie, intFormat.format(item.getCalorie()));
    }
}
