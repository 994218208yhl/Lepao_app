package com.liuzozo.stepdemo.OtherFunction;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.liuzozo.stepdemo.R;

import java.util.Calendar;
import java.util.Formatter;

public class PickUtils {
    private Calendar calendar = Calendar.getInstance();;
    private int mYear, mMonth, mDay;
    private int mHour;
    private Integer mMinute;
    private Context context;

    public PickUtils(Context context) {
        this.context = context;
    }
    public void setTime(final TextView time) {
        // 点击"时间"按钮布局 设置时间
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 自定义控件
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_time_picker, null);
                final TimePicker timePicker = (TimePicker) view
                        .findViewById(R.id.time_picker);
                // 初始化时间
                calendar.setTimeInMillis(System.currentTimeMillis());
                timePicker.setIs24HourView(true);
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(Calendar.MINUTE);
                // 设置time布局
                builder.setView(view);
                builder.setTitle("设置提醒时间");
                builder.setPositiveButton("                    确  定              ",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mHour = timePicker.getHour();
                                mMinute = timePicker.getMinute();
                                // 时间小于10的数字 前面补0 如01:12:00
                                time.setText(new StringBuilder()
                                        .append(mHour < 10 ? "0" + mHour
                                                : mHour)
                                        .append(":")
                                        .append(mMinute < 10 ? "0" + mMinute
                                                : mMinute));
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

   /* public void setDistance(final TextView distance) {
        // 点击"日期"按钮布局 设置日期
        distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过自定义控件AlertDialog实现
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_number_picker, null);
                final NumberPicker intPicker =  view
                        .findViewById(R.id.int_picker);
                final NumberPicker decimalPicker =  view.findViewById(R.id.decimal_picker);
                // 设置日期简略显示 否则详细显示 包括:星期\周
                intPicker.setWrapSelectorWheel(true);
                 init(20,0,intPicker);
                 init(9,0,decimalPicker);
                // 设置date布局
                builder.setView(view);
                builder.setTitle("设置目标运动距离");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String number = "";
                                number += intPicker.getValue() + decimalPicker.getValue();
                                distance.setText(number);

                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void setHeight(final TextView height) {
        // 点击"日期"按钮布局 设置日期
        height.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过自定义控件AlertDialog实现
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_number_picker, null);
                TextView unit = view.findViewById(R.id.numberpicker_unit);
                unit.setText("CM");
                final NumberPicker intPicker =  view
                        .findViewById(R.id.int_picker);
                final NumberPicker decimalPicker =  view.findViewById(R.id.decimal_picker);

                builder.setView(view);
                builder.setTitle("设置当前身高");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String number = "";
                                number += intPicker.getValue() + decimalPicker.getValue();
                                height.setText(number);

                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void setWeight(final TextView weight) {
        // 点击"日期"按钮布局 设置日期
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过自定义控件AlertDialog实现
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_number_picker, null);
                TextView unit = view.findViewById(R.id.numberpicker_unit);
                unit.setText("KG");
                final NumberPicker intPicker =  view
                        .findViewById(R.id.int_picker);
                final NumberPicker decimalPicker =  view.findViewById(R.id.decimal_picker);

                builder.setView(view);
                builder.setTitle("设置当前体重");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String number = "";
                                number += intPicker.getValue() + decimalPicker.getValue();
                                weight.setText(number);

                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void setTargetWeight(final TextView weight) {
        // 点击"日期"按钮布局 设置日期
        weight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 通过自定义控件AlertDialog实现
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_number_picker, null);
                TextView unit = view.findViewById(R.id.numberpicker_unit);
                unit.setText("KG");
                final NumberPicker intPicker =  view
                        .findViewById(R.id.int_picker);
                final NumberPicker decimalPicker =  view.findViewById(R.id.decimal_picker);

                builder.setView(view);
                builder.setTitle("设置目标体重");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String number = "";
                                number += intPicker.getValue() + decimalPicker.getValue();
                                weight.setText(number);

                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }

    public void setTargetTime(final TextView time) {
        // 点击"时间"按钮布局 设置时间
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 自定义控件
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View view = (LinearLayout) View.inflate(context,
                        R.layout.dialog_time_picker, null);
                final TimePicker timePicker = (TimePicker) view
                        .findViewById(R.id.time_picker);
                // 初始化时间
                calendar.setTimeInMillis(System.currentTimeMillis());
                timePicker.setIs24HourView(true);
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                timePicker.setMinute(Calendar.MINUTE);
                // 设置time布局
                builder.setView(view);
                builder.setTitle("设置提醒时间");
                builder.setPositiveButton("确  定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                mHour = timePicker.getHour();
                                mMinute = timePicker.getMinute();
                                // 时间小于10的数字 前面补0 如01:12:00
                                time.setText(new StringBuilder()
                                        .append(mHour < 10 ? "0" + mHour
                                                : mHour)
                                        .append(":")
                                        .append(mMinute < 10 ? "0" + mMinute
                                                : mMinute));
                                dialog.cancel();
                            }
                        });
                builder.setNegativeButton("取  消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.cancel();
                            }
                        });
                builder.create().show();
            }
        });
    }
*/
    private void init(int max, int min, NumberPicker numberPicker) {

        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);
    }

    public String format(int value) {
        String tmpStr = String.valueOf(value);
        if (value < 10) {
            tmpStr = "0" + tmpStr;
        }
        return tmpStr;
    }
}