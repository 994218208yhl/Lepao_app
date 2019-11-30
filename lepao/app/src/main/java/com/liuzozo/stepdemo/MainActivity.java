package com.liuzozo.stepdemo;

import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.liuzozo.stepdemo.fragment.Account_Fragment;
import com.liuzozo.stepdemo.fragment.Sport_Fragment;
import com.liuzozo.stepdemo.fragment.StepData_Fragment;

/**
 *  app 主界面
 *  用于放三个Fragment 子布局
 */
public class MainActivity extends AppCompatActivity {

    // 定义一个FragmentTabHost对象
    private FragmentTabHost mTabHost;
    // 定义一个布局DefaultPassword
    private LayoutInflater layoutInflater;
    // 定义数组来存放3个菜单的 Fragment界面
    private Class fragmentArray[] = { Sport_Fragment.class,
            StepData_Fragment.class, Account_Fragment.class };
    // 定义数组来存放导航图标
    private int imageViewArray[] = { R.drawable.sport_change_icon,
            R.drawable.data_change_icon, R.drawable.account_change_icon };
    // Tab 选项卡的文字
    private String textViewArray[] = { "sports", "data", "account" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化组件
        initView();

    }

    private void initView() {
        layoutInflater = LayoutInflater.from(this);
        // 实例化TabHost对象,得到Tabhost
        mTabHost =   findViewById(R.id.id_tabhost);
        mTabHost.setup(this, getSupportFragmentManager(),
                R.id.id_nav_table_content);

        // 得到fragment的个数
        int count = fragmentArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(textViewArray[i])
                    .setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
    }

    // 给Tab 按钮设置图标和文字
    private View getTabItemView(int index) {
        View view = layoutInflater.inflate(R.layout.item_bottom_nav, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.nav_icon_iv);
        imageView.setImageResource(imageViewArray[index]);

        TextView textView = (TextView) view.findViewById(R.id.nav_text_tv);
        textView.setText(textViewArray[index]);
        return view;
    }
}
