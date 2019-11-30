package com.liuzozo.stepdemo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.liuzozo.stepdemo.bean.PathRecord;
import com.liuzozo.stepdemo.bean.TabEntity;
import com.liuzozo.stepdemo.fragment.MonthRecord_Fragment;
import com.liuzozo.stepdemo.fragment.SportRecordDetails_Fragment;
import com.liuzozo.stepdemo.fragment.SportRecordDetails_Map_Fragment;
import com.liuzozo.stepdemo.fragment.WeekRecord_Fragment;
import com.liuzozo.stepdemo.ui.AMapScrollViewPager;

import java.util.ArrayList;

/**
 *  一周跑步记录
 *  按天显示 柱状图、折线图等，这个页面大家可以自定义， 不需要非按照我的那个图片的样式
 *  （比如一条折线显示每天跑步的公里，一条显示每天跑步的时间，一条显示跑步的卡路里 等等，）
 *  要求：只要显示出数据库里跑步记录的一些分析即可
 */
public class WeekRecord_Activity extends AppCompatActivity {

    CommonTabLayout commonTabLayout;
    ViewPager mViewPager;
    ImageView lineChart;

    private PathRecord pathRecord = null;

    public static String SPORT_DATA = "SPORT_DATA";


    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private String[] mTitles = {"周报", "月报"};
    // 要显示的两个fragment
    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>() {{
        add(new WeekRecord_Fragment());
        add(new MonthRecord_Fragment());
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_record);
        initView();
    }

    private void initView() {


        mViewPager = findViewById(R.id.vp);

        commonTabLayout = findViewById(R.id.commonTabLayout);


        // 接收跳转到这个界面的pathRecord值，把这个值分发到不同的Fragment 进行显示即可
        // TODO ： pathRecord 值为从日历下列表里面传来的
       /* if (getIntent().hasExtra(SPORT_DATA)) {
            pathRecord = getIntent().getParcelableExtra(SPORT_DATA);
        }

        Bundle bundle = new Bundle();
        bundle.putParcelable(SPORT_DATA, pathRecord);*/
        for (int i = 0, size = mTitles.length; i < size; i++) {
            mTabEntities.add(new TabEntity(mTitles[i], 0, 0));
            // 设置跳转到这个Fragmnet 的值
            /*  mFragments.get(i).setArguments(bundle);*/

        }

        commonTabLayout.setTabData(mTabEntities);

        //设置缓存页面数量，默认为2，防止地图显示重载丢失已设置的数据
        mViewPager.setOffscreenPageLimit(3);

        mViewPager.setAdapter(new WeekRecord_Activity.MyPagerAdapter(getSupportFragmentManager()));

        mViewPager.setCurrentItem(0); // 显示哪一个布局

        commonTabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                commonTabLayout.setCurrentTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

    }


}
