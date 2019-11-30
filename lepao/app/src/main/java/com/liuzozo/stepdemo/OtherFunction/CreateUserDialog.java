package com.liuzozo.stepdemo.OtherFunction;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.liuzozo.stepdemo.R;

import java.util.Date;

public class CreateUserDialog extends Dialog {

    /**
     * 上下文对象
     **/
    Activity context;

    private Button btn_save;

    public EditText textHeight;

    public EditText textWeight;

    public EditText textSlogan;


    private View.OnClickListener mClickListener;

    public CreateUserDialog(Activity context) {
        super(context);
        this.context = context;
    }

    public CreateUserDialog(Activity context, int theme, View.OnClickListener clickListener) {
        super(context, theme);
        this.context = context;
        this.mClickListener = clickListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 指定布局
        this.setContentView(R.layout.dialog_personal_info);
/*
        textHeight = (EditText) findViewById(R.id.text_height);
        textWeight = (EditText) findViewById(R.id.text_weight);*/
        textSlogan = (EditText) findViewById(R.id.text_slogan);

        /*
         * 获取圣诞框的窗口对象及参数对象以修改对话框的布局设置, 可以直接调用getWindow(),表示获得这个Activity的Window
         * 对象,这样这可以以同样的方式改变这个Activity的属性.
         */
        Window dialogWindow = context.getWindow();

        WindowManager m = context.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        // p.height = (int) (d.getHeight() * 0.6); // 高度设置为屏幕的0.6
       /* p.width = (int) (d.getWidth() * 0.8);*/ // 宽度设置为屏幕的0.8
        dialogWindow.setAttributes(p);

        // 根据id在布局中找到控件对象
        btn_save = (Button) findViewById(R.id.text_save);

        // 为按钮绑定点击事件监听器
      /*  btn_save.setOnClickListener(mClickListener);
        btn_save = (Button) findViewById(R.id.text_save);*/

        this.setCancelable(true);
    }
   /* public void onClick(View v) {

        switch (v.getId()) {
            case R.id.text_save:
                Toast.makeText(context,"保存",Toast.LENGTH_LONG).show();
                SharedPreferences.Editor editor = context.getSharedPreferences("personal_data", Context.MODE_PRIVATE).edit();
                editor.putString("height",textHeight.getText().toString());
                editor.putString("weight",textWeight.getText().toString());
                editor.putString("slogan", textSlogan.getText().toString());
                double height = Double.parseDouble(textHeight.getText().toString())/ 100;
                double weight = Double.parseDouble(textWeight.getText().toString());
                double BMI = weight / (height * height);
                editor.putString("BMI",String.format("%.1f",BMI));
                SharedPreferences read = context.getSharedPreferences("personal_data",Context.MODE_PRIVATE);
                Log.d("TAG","身高是" + read.getString("height","无"));

                break;
                default:
                break;
        }
    }*/
}
