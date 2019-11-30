package com.liuzozo.stepdemo.ui;

import android.graphics.drawable.Drawable;

import android.widget.TextView;


/**
 *  ui 相关的常用方法工具类
 */
public class UIHelperUtil {


    /**
     * 设置textView 左边的小图标
     * @param textView
     * @param drawable
     */
    public static void setLeftDrawable(TextView textView, Drawable drawable) {
        try {
            assert drawable != null;
            drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            textView.setCompoundDrawables(drawable, null, null, null);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}
