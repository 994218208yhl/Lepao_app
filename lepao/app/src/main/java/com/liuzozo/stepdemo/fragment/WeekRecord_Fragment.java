package com.liuzozo.stepdemo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liuzozo.stepdemo.OtherFunction.MyApplication;
import com.liuzozo.stepdemo.OtherFunction.StepUtils;
import com.liuzozo.stepdemo.R;

import java.util.ArrayList;
import java.util.List;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.LineChartView;

import static android.content.Context.MODE_PRIVATE;

public class WeekRecord_Fragment extends Fragment {

    private Context context;
    private LineChartView lineChart;
    private ColumnChartView columnChart;
    double[] mintues =new double[7];
    double[] distance = new double[7];
    double[] calorie = new double[7];
    String[] date = StepUtils.getRecentDays(7);
    ArrayList<double[]> totalData ;
    private TextView mDistance,mDuration,mKcal,mSpeed;
/*    String[] date = {"10-22","11-22","12-22","1-22","6-22","5-23","5-22","6-22","5-23","5-22"};//X轴的标注*/
    /*int[] score= {50,42,90,33,10,74,22,18,79,20};//图表的数据点*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weekrecord, container,
                false);
        context = getActivity();

        lineChart = (LineChartView)view.findViewById(R.id.week_line_chart);
        columnChart = view.findViewById(R.id.week_column_chart);
        mDistance = view.findViewById(R.id.today_mdistance);
        mDuration = view.findViewById(R.id.today_mduration);
        mKcal = view.findViewById(R.id.today_mkcal);
        mSpeed = view.findViewById(R.id.today_mspeed);


        setData();

        initLineChart();//初始化折线图
        initColumnChart();//初始化柱状图
        initEvent();//设置监听


        SharedPreferences read = context.getSharedPreferences("notification_data", MODE_PRIVATE);
       /* SharedPreferences.Editor editor = context.getSharedPreferences("notification_data", MODE_PRIVATE).edit();
        editor.putString("planDataService", "true");
        editor.apply();*/
        if(read.getString("planDataService",null)==null || read.getString("planDataService",null).equals("true")) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle("Tips");
            dialog.setMessage("可以尝试与图表交互，比如缩放，平移，且在周报中点击柱状图表数据，可以获得更多详情");
            dialog.setCancelable(false);
            dialog.setPositiveButton("                        确认并再次提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    SharedPreferences.Editor editor = context.getSharedPreferences("notification_data", MODE_PRIVATE).edit();
                    editor.putString("planDataService", "true");
                    editor.apply();
                }

            });

            dialog.setNegativeButton("不再提醒", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = context.getSharedPreferences("notification_data", MODE_PRIVATE).edit();
                    editor.putString("planDataService", "false");
                    editor.apply();


                }
            });
            dialog.show();
        }


        return view;
    }

    /**
     * 图像的监听
     *
     */

    private void initEvent() {
        columnChart.setOnValueTouchListener(new ValueTouchListener());
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            /* showToast("Selected: " + value);*/
            double mdistance = 0.0, mspeed = 0.0, mduration = 0.0, mkcal = 0.0;
            if (totalData.get(columnIndex)[4] != 0) {
                mdistance = (distance[columnIndex]) / (totalData.get(columnIndex)[4]);
                mspeed = (totalData.get(columnIndex)[3]) / (totalData.get(columnIndex)[4]);
                mduration = (mintues[columnIndex]) / (totalData.get(columnIndex)[4]);
                mkcal = (calorie[columnIndex]) /(totalData.get(columnIndex)[4]);
                mDistance.setText(String.format("%.2f",mdistance) + "km");
                mDuration.setText(String.format("%.2f",mduration) + "min");
                mKcal.setText(String.format("%.2f",mkcal) + "kcal");
                mSpeed.setText(String.format("%.2f",mspeed) + "km/h");
            }
        }
        @Override
        public void onValueDeselected() {

        }

    }





    private void setData() {
          totalData = StepUtils.getMinDisCal(context, 7);
         for (int i = 0; i < 7; i++) {
             mintues[i] = totalData.get(i)[1];
             distance[i] = totalData.get(i)[0];
             calorie[i] = totalData.get(i)[2];
         }

  }
      private void initLineChart () {
          List<PointValue> mPointValues = new ArrayList<PointValue>();
          List<AxisValue> mAxisXValues = new ArrayList<AxisValue>();
          //获取x轴的标注
          for (int i = 0; i < date.length; i++) {
              mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
          }
          //获取坐标点
          for (int i = 0; i < distance.length; i++) {
              mPointValues.add(new PointValue(i, (float) distance[i]));
          }
          Line line = new Line(mPointValues).setColor(Color.parseColor("#0E6C67"));  //折线的颜色
          List<Line> lines = new ArrayList<Line>();
          line.setShape(ValueShape.CIRCLE);//折线图上每个数据点的形状  这里是圆形 （有三种 ：ValueShape.SQUARE  ValueShape.CIRCLE  ValueShape.DIAMOND）
          line.setCubic(false);//曲线是否平滑，即是曲线还是折线
          line.setFilled(false);//是否填充曲线的面积
          /*line.setHasLabels(true);*///曲线的数据坐标是否加上备注
          line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据（设置了这个line.setHasLabels(true);就无效）
          line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
          line.setHasPoints(true);//是否显示圆点 如果为false 则没有原点只有点显示（每个数据点都是个大的圆点）
          lines.add(line);
          LineChartData data = new LineChartData();
          data.setLines(lines);

          //坐标轴
          Axis axisX = new Axis(); //X轴
          axisX.setHasTiltedLabels(true);  //X坐标轴字体是斜的显示还是直的，true是斜的显示
          axisX.setTextColor(Color.GRAY);  //设置字体颜色
          //axisX.setName("date");  //表格名称
          axisX.setHasLines(false);
          axisX.setTextSize(8);//设置字体大小
          axisX.setMaxLabelChars(10); //最多几个X轴坐标，意思就是你的缩放让X轴上数据的个数7<=x<=mAxisXValues.length
          axisX.setValues(mAxisXValues);  //填充X轴的坐标名称
          data.setAxisXBottom(axisX); //x 轴在底部
          //data.setAxisXTop(axisX);  //x 轴在顶部
          axisX.setHasLines(true); //x 轴分割线

          // Y轴是根据数据的大小自动设置Y轴上限(在下面我会给出固定Y轴数据个数的解决方案)
          Axis axisY = new Axis();  //Y轴
          axisY.setName("km");//y轴标注
          axisY.setTextSize(12);//设置字体大小
          data.setAxisYLeft(axisY);  //Y轴设置在左边
          //data.setAxisYRight(axisY);  //y轴设置在右边


          //设置行为属性，支持缩放、滑动以及平移
          lineChart.setInteractive(true);
          lineChart.setZoomType(ZoomType.HORIZONTAL);
          lineChart.setMaxZoom((float) 2);//最大方法比例
          lineChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);
          lineChart.setLineChartData(data);
          lineChart.setVisibility(View.VISIBLE);
      }
      /**注：下面的7，10只是代表一个数字去类比而已
       * 当时是为了解决X轴固定数据个数。见（http://forum.xda-developers.com/tools/programming/library-hellocharts-charting-library-t2904456/page2）;
       */
      /*  Viewport v = new Viewport(lineChart.getMaximumViewport());
        v.left = 0;
        v.right = 7;
        lineChart.setCurrentViewport(v);*/


      /**
       * 设置X 轴的显示
       */
   /* private void getAxisXLables() {
        for (int i = 0; i < date.length; i++) {
            mAxisXValues.add(new AxisValue(i).setLabel(date[i]));
        }
    }

    *//**
       * 图表的每个点的显示
       *//*
    private void getAxisPoints() {
        for (int i = 0; i < distance.length; i++) {
            mPointValues.add(new PointValue(i, (float)distance[i]));
        }
    }*/

      private void initColumnChart () {
          //底部标题
          /* List<String> title = new ArrayList<>();*/
          //颜色值
          List<Integer> color = new ArrayList<>();
          //X、Y轴值list
          List<AxisValue> axisXValues = new ArrayList<>();

          //所有的柱子
          List<Column> columns = new ArrayList<>();


          //颜色值
          color.add(Color.parseColor("#FF009688"));
          color.add(Color.parseColor("#FFCD41"));


          for (int j = 0; j < date.length; j++) {

              //附属柱子
              List<SubcolumnValue> mPointValues = new ArrayList<>();

              //显示几个小柱子 这里为3
              for (int i = 0; i < 2; i++) {
                  //值的大小、颜色

                  mPointValues.add(new SubcolumnValue((float) totalData.get(j)[i + 1], color.get(i)));
              }
              //设置X轴的柱子所对应的属性名称(底部文字)
              axisXValues.add(new AxisValue(j).setLabel(date[j]));
              //设置数据单个大柱子
              Column column = new Column();
              column.setValues(mPointValues);
              //是否显示每个柱子的标签
              column.setHasLabels(true);
              //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
              column.setHasLabelsOnlyForSelected(true);
              //将每个属性得列全部添加到List中
              columns.add(column);

          }


          //底部属性
          Axis axisBottom = new Axis(axisXValues);
          //是否显示X轴的网格线
          axisBottom.setHasLines(false);
          //分割线颜色
          axisBottom.setLineColor(Color.parseColor("#ff0000"));
          //字体颜色
          axisBottom.setTextColor(Color.GRAY);
          //字体大小
          axisBottom.setTextSize(8);
          //底部文字
          axisBottom.setName("");
          //每个柱子的便签是否倾斜着显示
          axisBottom.setHasTiltedLabels(true);
          //距离各标签之间的距离,包括离Y轴间距 (0-32之间)
          axisBottom.setMaxLabelChars(10);
          //设置是否自动生成轴对象,自动适应表格的范围(设置之后底部标题变成0-5)
          //axisBottom.setAutoGenerated(true);
          axisBottom.setHasSeparationLine(true);

          //设置Columns添加到Data中
          ColumnChartData columnData = new ColumnChartData(columns);
          //设置x轴在底部显示
          columnData.setAxisXBottom(axisBottom);

          //左边  属性与上面一致
          Axis axisLeft = new Axis();
          axisLeft.setHasLines(false);
          axisLeft.setName("min/kcal");//左边标题
          axisLeft.setHasTiltedLabels(true);
          axisLeft.setTextColor(Color.parseColor("#666666"));
          columnData.setAxisYLeft(axisLeft);

          //设置数据标签的字体大小
          /* columnData.setValueLabelTextSize(12);*/
          //设置数据标签的字体颜色
          //columnData.setValueLabelsTextColor(Color.WHITE);
          //设置数据背景是否跟随节点颜色
          // columnData.setValueLabelBackgroundAuto(true);
          //设置是否有数据背景  是否跟随columvalue的颜色变化
          //columnData.setValueLabelBackgroundEnabled(true);
          //设置坐标点旁边的文字背景(...)
          //data.setValueLabelBackgroundColor(Color.YELLOW);
          //axisBottom.setMaxLabelChars(5);
          //设置组与组之间的间隔比率,取值范围0-1,1表示组与组之间不留任何间隔
          columnData.setFillRatio(0.7f);
          columnChart.setInteractive(false);

          //最后将所有值显示在View中
          columnChart.setColumnChartData(columnData);
          //设置行为属性，支持缩放、滑动以及平移
          columnChart.setInteractive(true);
          columnChart.setZoomType(ZoomType.HORIZONTAL);
          columnChart.setMaxZoom((float) 2);//最大方法比例
          columnChart.setContainerScrollEnabled(true, ContainerScrollType.HORIZONTAL);

        Viewport v = columnChart.getMaximumViewport();
        v.top = (int)StepUtils.findMaxInDoubleArray(calorie) + 10;
       columnChart.setCurrentViewport(v);


      }


}
