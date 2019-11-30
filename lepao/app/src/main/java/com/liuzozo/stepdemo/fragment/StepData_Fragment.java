package com.liuzozo.stepdemo.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;
import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;
import com.liuzozo.stepdemo.MainActivity;
import com.liuzozo.stepdemo.OtherFunction.MyApplication;
import com.liuzozo.stepdemo.OtherFunction.MyDatabaseHelper;
import com.liuzozo.stepdemo.OtherFunction.StepUtils;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.SportRecordDetails_Activity;
import com.liuzozo.stepdemo.adapter.SportCalendarAdapter;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.calendarview.custom.FullyLinearLayoutManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * sport data  page
 * 1. 根据日期 查询数据库， 在日历上标记，下方显示 日历某一天的跑步记录
 * 2. 点击某一条记录，跳转到跑步详情页面
 */
public class StepData_Fragment extends AppCompatDialogFragment {


    TextView mTextYear;
    TextView mTextMongthDay;
    TextView mTextLunar;

    TextView mTextCurrentDay;

    // 日历控件
    CalendarView mCalendarView;
    RecyclerView mRecycleView;

    CalendarLayout calendarLayout;

    private SportCalendarAdapter sportCalendarAdapter;

    LinearLayout sport_record_listLayout;

    private int mYear;
    private List<PathRecord> sportList = new ArrayList<>(0);
    private ArrayList<String> mDateTagList = new ArrayList<String>();
    MyDatabaseHelper dbHelper;
    Context context;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stepdata, container,
                false);

        initView(view);
        context = MyApplication.getContext();
        return view;
    }

    private void initView(View view) {
        mCalendarView = view.findViewById(R.id.calendarView);
        mYear = mCalendarView.getCurYear();

        mTextYear = view.findViewById(R.id.tv_year);
        mTextYear.setText(String.valueOf(mYear));

        String monthAndDay = mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日";
        mTextMongthDay = view.findViewById(R.id.tv_month_day);
        mTextMongthDay.setText(monthAndDay);

        mTextLunar = view.findViewById(R.id.tv_lunar);
        mTextLunar.setText("今日");

        mTextCurrentDay = view.findViewById(R.id.tv_current_day);
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        sport_record_listLayout = view.findViewById(R.id.sport_record_list);

        mCalendarView.setWeekStarWithSun();

        mRecycleView = view.findViewById(R.id.recyclerView);

        mRecycleView.setLayoutManager(new FullyLinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mRecycleView.addItemDecoration(new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.line)));


        sportCalendarAdapter = new SportCalendarAdapter(R.layout.item_sport_calendar, sportList);
        mRecycleView.setAdapter(sportCalendarAdapter);


        // 加载数据
        loadSportData();

        // 每一条跑步记录的点击事件
        sportCalendarAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent();
                intent.setClass(getContext(), SportRecordDetails_Activity.class);
                intent.putExtra("SPORT_DATA",sportList.get(position));
                Log.d("TAG","点击的item位置： " + position );

                startActivity(intent);
            }
        });
        //跑步记录长按删除事件
        sportCalendarAdapter.setOnItemLongClickListener(new BaseQuickAdapter.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(BaseQuickAdapter adapter, View view, final int position) {
                /*boolean isLongClick = true;*/
                Log.d("TAG", "onItemLongClick: ");
               /* Toast.makeText(context, "onItemLongClick" + position, Toast.LENGTH_SHORT).show();*/
                Log.d("TAG","onItemSwiped");
                Log.d("TAG","onItemSwipeMoving");
                Log.d("TAG","onItemSwipeStart");
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("");
                dialog.setMessage("是否删除该数据，此次删除不可复原");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PathRecord deleteItem = sportList.get(position);
                        Long itemId = deleteItem.getId();
                        Log.d("TAG", "数据id:  " + itemId );
                       /* String date = deleteItem.getDateTag();
                        Double distance = deleteItem.getDistance();*/
                        dbHelper =  new MyDatabaseHelper(getContext(), "SportData.db",null,1);
                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        Cursor cursor = db.query("SportRecord", new String[] {"id"}, null, null, null,null,null);
                        db.delete("SportRecord","id = ?",new String[]{itemId+""});
                        if (cursor.moveToFirst()) {
                            do {
                                /* String date = cursor.getString(cursor.getColumnIndex("mDateTag"));*/
                             String dis = cursor.getString(0);
                                Log.d("TAG", "数据库dis   " + dis);


                            } while (cursor.moveToNext());

                        }

                        sportList.remove(position);
                        sportCalendarAdapter.notifyDataSetChanged();


                    }
                });
                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //
                    }
                });
                dialog.show();

                sportCalendarAdapter.enableSwipeItem();

                return false;
            }
        });
       /* OnItemDragListener onItemDragListener = new OnItemDragListener() {
            @Override
            public void onItemDragStart(RecyclerView.ViewHolder viewHolder, int pos){}
            @Override
            public void onItemDragMoving(RecyclerView.ViewHolder source, int from, RecyclerView.ViewHolder target, int to) {}
            @Override
            public void onItemDragEnd(RecyclerView.ViewHolder viewHolder, int pos) {}
        };

        //滑动监听
        OnItemSwipeListener onItemSwipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d("TAG","onItemSwiped");
                Log.d("TAG","onItemSwipeMoving");
                Log.d("TAG","onItemSwipeStart");
              *//*  AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("");
                dialog.setMessage("滑动删除运动数据不可复原,是否删除");
                dialog.setCancelable(false);
                dialog.setPositiveButton("确认删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //
                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //
                    }
                });


                dialog.show();
*//*

            }
            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int pos) {
                Log.d("TAG","clearView");
                Toast.makeText(context, "数据已删除" , Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int pos) {
                final int index = pos;




            }
            @Override
            public void onItemSwipeMoving(Canvas a, RecyclerView.ViewHolder b, float c, float d, boolean e){


            }
        };


        //长按运动记录的事件
        ItemDragAndSwipeCallback itemDragAndSwipeCallback = new ItemDragAndSwipeCallback(sportCalendarAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemDragAndSwipeCallback);
        itemTouchHelper.attachToRecyclerView(mRecycleView);

        // 开启拖拽
        sportCalendarAdapter.enableDragItem(itemTouchHelper, R.id.item_sport_record, true);
        sportCalendarAdapter.setOnItemDragListener(onItemDragListener);

// 开启滑动删除
       *//* sportCalendarAdapter.enableSwipeItem();*//*
        sportCalendarAdapter.setOnItemSwipeListener(onItemSwipeListener);
        //设置拖拽监听回调

*/
        // 日历点击事件
        mCalendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {

            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                // 点击时加载数据
                int year = calendar.getYear();
                int month = calendar.getMonth();
               int day =  calendar.getDay();

                loadSportData(year,month,day);
            }
        });


    }

    /**
     * 查询数据库里当天的跑步记录
     * 1. 查询所有跑步数据，设置日历标记
     * 2  根据年月日参数，查询某一天的数据，设置recycleview 的条目
     *
     */
    private void loadSportData() {

        setCalendarSportData();
        setRecycleViewSportData(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());
    }
    private void loadSportData(int year, int month, int day) {

        /*setCalendarSportData();*/
        setRecycleViewSportData(year, month, day);
      /*  setRecycleViewSportData(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), mCalendarView.getCurDay());*/
    }

    public void setCalendarSportData() {
        // 给日历做有记录的标记, 需要查询出有跑步记录的年月日
        dbHelper =  new MyDatabaseHelper(getContext(), "SportData.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("SportRecord", new String[] {"mDateTag"}, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
               /* String date = cursor.getString(cursor.getColumnIndex("mDateTag"));*/
                String date = cursor.getString(0);
                Log.d("TAG", "数据库DateTag:  " + date );
                if(!mDateTagList.contains(date))
                       mDateTagList.add(date);


            } while (cursor.moveToNext());

        }
        Map<String, Calendar> map = new HashMap<>();

        for(int i = 0 ; i < mDateTagList.size();i++) {
            String dateTag = mDateTagList.get(i);//2019Jun04Tue
            int year = Integer.parseInt(dateTag.substring(0,4));
            StepUtils stepUtils = new StepUtils();
            int month = stepUtils.getMonth(dateTag.substring(4,7));
            int day = Integer.parseInt(dateTag.substring(7,9));

            map.put(getSchemeCalendar(year, month, day, 0xFF0E6C67, "Run").toString(),
                    getSchemeCalendar(year, month, day, 0xFF0E6C67, "Run"));
        }
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);
    }

    public void setRecycleViewSportData(int year, int month, int day) {
        // todo 本应该查询数据库
        sportList.clear(); // 清除之前的数据
        sportCalendarAdapter.notifyDataSetChanged();// 更新显示
        StepUtils stepUtils = new StepUtils();
        String daystr = (day < 10 ? "0" + day : day + "");

        String date = year + stepUtils.getMonthString(month)+ daystr;
        dbHelper =  new MyDatabaseHelper(getContext(), "SportData.db",null,1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query("SportRecord", new String[]{"id","Distance","Duration","PathLinePoints","StartPoint",
                "EndPointLat","StartTime","EndTime","Calorie","Speed","mDistribution","mDistribution","mDateTag"}, "mDateTag like ?",
                new String[]{"%" + date + "%"}, null, null, "Distance desc", null);
        if (cursor.moveToFirst()) {
            do {
                PathRecord pathRecord = new PathRecord();
                /*pathRecord.setId(1L);*/
                pathRecord.setId(cursor.getLong(0));
                pathRecord.setDistance(cursor.getDouble(1));
                pathRecord.setDuration(cursor.getLong(2));
                pathRecord.setPathline(stepUtils.getPathline(cursor.getString(3)));
                pathRecord.setStartpoint(stepUtils.parseStringToLatlng(cursor.getString(4)));
                pathRecord.setEndpoint(stepUtils.parseStringToLatlng(cursor.getString(5)));
                pathRecord.setStartTime(Long.parseLong(cursor.getString(6)));
                pathRecord.setEndTime(Long.parseLong(cursor.getString(7)));
                pathRecord.setCalorie(Double.parseDouble(cursor.getString(8)));
                pathRecord.setSpeed(Double.parseDouble(cursor.getString(9)));
                pathRecord.setDistribution(Double.parseDouble(cursor.getString(10)));
                pathRecord.setDateTag(cursor.getString(11));
                sportList.add(pathRecord);


            } while (cursor.moveToNext());

        }

        if (sportList.size() > 0) {

            sport_record_listLayout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 对日历进行设置，当然也可以操作其它属性
     *
     * @param year
     * @param month
     * @param day
     * @param color
     * @param text
     * @return
     */
    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, text);
        return calendar;
    }


    //recyclerView设置间距
    protected class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int mSpace;

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.right = mSpace;
            outRect.left = mSpace;
            outRect.bottom = mSpace;
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            } else {
                outRect.top = 0;
            }
        }

        SpaceItemDecoration(int space) {
            this.mSpace = space;
        }
    }


}
