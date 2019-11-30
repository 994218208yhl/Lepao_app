package com.liuzozo.stepdemo.fragment;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.liuzozo.stepdemo.BuildConfig;
import com.liuzozo.stepdemo.OtherFunction.CreateUserDialog;
import com.liuzozo.stepdemo.OtherFunction.MyApplication;
import com.liuzozo.stepdemo.PlanSetting_Activity;
import com.liuzozo.stepdemo.R;
import com.liuzozo.stepdemo.TuLinTalk_Activity;
import com.liuzozo.stepdemo.WeekRecord_Activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;
/*import static android.support.v7.widget.StaggeredGridLayoutManager.TAG;*/

/**
 * 个人信息页面
 *  当用户首次点击跑步的时侯，需要检测SharedPreference 里面是否存储了 运动标语， 身高、体重，并计算BMI，
 *   没有存储这些值的时侯，不能让他跳转到跑步页面
 *
 *   点击头像区域，设置头像
 *   点击头像左边区域，设置运动标语， 身高，体重 ， 保存SharedPreference 里面后，计算BMI 值显示到界面
 *
 *   点击跑步设置，一周记录，悦聊 分别跳转到对应页面
 */

public class Account_Fragment extends Fragment implements View.OnClickListener {

    // 三个Layout
    LinearLayout planSettingLayout;
    LinearLayout weekRecordLayout;
    LinearLayout talkLayout;
    LinearLayout ll_account_slogan, ll_account_data;

   TextView accountSlogan;
    TextView accountHeight;
    TextView accountWeight;
    TextView accountBMI;
    ImageView accountPicture;

    Button btn_save;
    Context context;
    CreateUserDialog  createUserDialog;
    Dialog dialog;
   /* boolean isResetData = false;
    boolean isResetPicture = false;*/
    String cameraPath;
    String height = "0 ",weight = "0";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container,
                false);


        context = MyApplication.getContext();
        initView(view);


        return view;
    }

    private void initView(View view) {
        planSettingLayout = view.findViewById(R.id.plan_setting_id);
        weekRecordLayout = view.findViewById(R.id.week_record_id);
        talkLayout = view.findViewById(R.id.talk_id);
        // 设置对应的监听事件
        planSettingLayout.setOnClickListener(this);
        weekRecordLayout.setOnClickListener(this);
        talkLayout.setOnClickListener(this);

        accountSlogan = view.findViewById(R.id.account_slogan);
        accountSlogan.setOnClickListener(this);
        accountHeight = view.findViewById(R.id.account_height);
        accountHeight.setOnClickListener(this);
        accountWeight = view.findViewById(R.id.account_weight);
        accountWeight.setOnClickListener(this);
        accountBMI = view.findViewById(R.id.account_BMI);
        accountBMI.setOnClickListener(this);
        accountPicture = view.findViewById(R.id.account_picture);
        accountPicture.setOnClickListener(this);
        ll_account_slogan = view.findViewById(R.id.ll_account_slogan);
        ll_account_slogan.setOnClickListener(this);
        ll_account_data = view.findViewById(R.id.ll_account_data);
        ll_account_data.setOnClickListener(this);

        SharedPreferences.Editor editor = context.getSharedPreferences("personal_data",MODE_PRIVATE).edit();
        Log.d("TAG","1");
        editor.putString("state","create");
        editor.apply();

        SharedPreferences read = context.getSharedPreferences("personal_data", MODE_PRIVATE);
        Log.d("TAG",read.getString("state",null));

        if(read.getString("weight",null) != null && read.getString("height",null) != null){
        accountWeight.setText(read.getString("weight","") + "kg");
        accountHeight.setText(read.getString("height","" ) + "cm");
        accountSlogan.setText(read.getString("slogan",""));
        accountBMI.setText(read.getString("BMI",""));
        }
        if(read.getString("picture_path",null) != null){
            String file_path = read.getString("picture_path", null);
            Bitmap bitmap = BitmapFactory.decodeFile(file_path);
            accountPicture.setImageBitmap(bitmap);
        }
      /*  btn_save = (Button) view.findViewById(R.id.text_save);

     *//*   为按钮绑定点击事件监听器*//*
        btn_save.setOnClickListener(this);*/


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.plan_setting_id:
                Intent goPlan = new Intent(getContext(), PlanSetting_Activity.class);
                startActivity(goPlan);
                break;
            case R.id.week_record_id:
                Intent goWeek = new Intent(getContext(), WeekRecord_Activity.class);
                startActivity(goWeek);
                break;
            case R.id.talk_id:
                Intent goTalk = new Intent(getContext(), TuLinTalk_Activity.class);
                startActivity(goTalk);
                break;
            case R.id.ll_account_slogan:
                showDialogSlogan();

                break;
            case R.id.ll_account_data:
                showDialogData();
                break;

            case R.id.account_picture:
                setPicture();






            default:
                break;
        }
    }


    private void showDialogData(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_number_picker,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();

        Button btn_save = view.findViewById(R.id.text_save);
        Button btn_cancel = view.findViewById(R.id.text_cancel);
        final NumberPicker height_int = view.findViewById(R.id.height_int_picker);
        numberPickerSetValue(height_int,0,250);
        height_int.setValue(160);
        final NumberPicker height_dec = view.findViewById(R.id.height_decimal_picker);
        numberPickerSetValue(height_dec,0,9);
        final NumberPicker weight_int = view.findViewById(R.id.weight_int_picker);
        numberPickerSetValue(weight_int,0,125);
        weight_int.setValue(50);
        final NumberPicker weight_dec = view.findViewById(R.id.height_decimal_picker);
        numberPickerSetValue(weight_dec,0,9);

        /*final EditText text_weight = (EditText) view.findViewById(R.id.text_weight);
        final EditText text_height = (EditText) view.findViewById(R.id.text_height);
        final EditText text_slogan = view.findViewById(R.id.text_slogan);
*/

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 height = height_int.getValue() + "." + height_dec.getValue() ;
                 weight = weight_int.getValue() + "." + weight_dec.getValue() ;
                SharedPreferences.Editor editor = context.getSharedPreferences("personal_data", MODE_PRIVATE).edit();
                editor.putString("height",height);
                editor.putString("weight",weight);
                /*editor.putString("slogan", text_slogan.getText().toString());*/

                //捕获异常输入不能为空 否则计算错误
                try {
                    double height_num = Double.parseDouble(height)/ 100;
                    double weight_num = Double.parseDouble(weight);
                    double BMI;
                    if(height_num!=0.0) {
                         BMI = weight_num / (height_num * height_num);
                    }
                    else
                        throw new NumberFormatException ("height is 0");

                    if(weight_num == 0.0 )
                        throw new NumberFormatException ("weight is 0");

                    editor.putString("BMI",String.format("%.1f",BMI));
                    editor.apply();
                    SharedPreferences read = context.getSharedPreferences("personal_data", MODE_PRIVATE);

                    Log.d("TAG","BMI:" + read.getString("height","空值"));
                    Log.d("TAG","height:" + height);
                    accountWeight.setText(read.getString("weight","") + " kg");
                    accountHeight.setText(read.getString("height","" ) + " cm");
                    accountSlogan.setText(read.getString("slogan",""));
                    accountBMI.setText(read.getString("BMI",""));
                   /* isResetData = true;*/


                }
                catch (NullPointerException ex){
                    Toast.makeText(context,"身高和体重不能为空",Toast.LENGTH_LONG).show();
                }

                catch (NumberFormatException ex){
                    Toast.makeText(context,"无法保存!\n身高和体重为必填项,请重新填写",Toast.LENGTH_LONG).show();
                }


                //... To-do*/
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //... To-do
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        /*dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3),LinearLayout.LayoutParams.WRAP_CONTENT);*/
    }

    private void showDialogSlogan(){
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_personal_info,null,false);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity()).setView(view).create();

        Button btn_save = view.findViewById(R.id.text_save);
        Button btn_cancel = view.findViewById(R.id.text_cancel);

        final EditText text_slogan = view.findViewById(R.id.text_slogan);


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = context.getSharedPreferences("personal_data", MODE_PRIVATE).edit();

                editor.putString("slogan", text_slogan.getText().toString());
                editor.apply();
                SharedPreferences read = context.getSharedPreferences("personal_data", MODE_PRIVATE);
                accountSlogan.setText(text_slogan.getText().toString());

                //... To-do*/
                dialog.dismiss();
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //... To-do
                dialog.dismiss();
            }
        });

        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        /*dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3),LinearLayout.LayoutParams.WRAP_CONTENT);*/
    }


    private void setPicture(){

        Intent intent1 = new Intent(Intent.ACTION_PICK);
        intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

        Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File file = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                System.currentTimeMillis() + ".jpg");
        cameraPath = file.getAbsolutePath();
      /*  Uri uri = Uri.fromFile(file);*/
        /*Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID ,".fileProvider", file);*/

        Uri uri= FileProvider.getUriForFile(context,"com.liuzozo.stepdemo.fileProvider", file);//这里进行替换uri的获得方式
        intent2.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent2.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        Intent chooser = Intent.createChooser(intent1, "选择头像");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
        startActivityForResult(chooser, 101);// --------------->101
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                if (requestCode == 101) {// 获得未裁剪的照片 --------------->101
                    String filePath;
                    if (data != null) {
                        /*Uri uri = data.getData();
                        filePath = uri.getPath();*/
                        filePath = getPath(context,data);
                    } else {
                        // 相机拍照
                        filePath = cameraPath;
                    }
                    crop(filePath);// 裁剪
                }
                if (requestCode == 102) {// 裁剪点击确定后执行 --------------->102
                    // 获得了系统截图程序返回的截取后的图片
                    Log.e("TAG", "set0");
                    final Bitmap bitmap = data.getParcelableExtra("data");

                    // 上传前，要将bitmap保存到SD卡
                    // 获得保存路径后，再上传
                    Log.e("TAG", "set1");
                    final File file = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            System.currentTimeMillis() + ".jpg");
                    OutputStream stream = new FileOutputStream(file);
                    Log.e("TAG", "set4");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    Log.e("TAG", "se5t");
                    accountPicture.setImageBitmap(bitmap);
                    Log.e("TAG", "se6t");
                    String filepath = file.getPath();
                    Log.e("TAG", "se7t");
                    SharedPreferences.Editor editor = context.getSharedPreferences("personal_data",MODE_PRIVATE).edit();
                    editor.putString("picture_path", filepath);
                    editor.apply();
                    // 上传头像数据并显示头像操作
                    // showProgressDialog();
                    // uploading();
                    //Glide.with(SetActivity.this).load(file.getPath()).into(icon);
                   // closeProgressDialog();
                }

                else{
                    /*// 获得了系统截图程序返回的截取后的图片
                    Log.e("TAG", "set0");
                    final Bitmap bitmap = data.getParcelableExtra("data");

                    // 上传前，要将bitmap保存到SD卡
                    // 获得保存路径后，再上传
                    Log.e("TAG", "set1");
                    final File file = new File(
                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                            2 + ".jpg");
                    OutputStream stream = new FileOutputStream(file);
                    Log.e("TAG", "set");
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 8, stream);
                    Log.e("TAG", "set");
                    accountPicture.setImageBitmap(bitmap);
                    *//*String filepath = file.getPath();
                    SharedPreferences.Editor editor = context.getSharedPreferences("personal_data",MODE_PRIVATE).edit();
                    editor.putString("picture_path", filepath);
                    editor.apply();*/
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


}

/*    @param filePath
     *         用户选取的头像在SD上的地址*/

    private void crop(String filePath) {
        // 隐式intent
        Intent intent = new Intent("com.android.camera.action.CROP");
       /* Uri data = Uri.fromFile(new File(filePath));*/
        Uri data= FileProvider.getUriForFile(context,"com.liuzozo.stepdemo.fileProvider", new File(filePath));

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        // 设置剪裁数据 150*150
        intent.setDataAndType(data, "image/*");
        intent.putExtra("crop", true);
        intent.putExtra("return-data", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        startActivityForResult(intent, 102);
        Log.e("TAG", "corp");// --------------->102
    }


    public static String getPath(Context context,Intent data){
        Uri selectedImage = data.getData();
        Log.e("TAG", selectedImage.toString());
        if(selectedImage!=null){
            String uriStr=selectedImage.toString();
            String path=uriStr.substring(10,uriStr.length());
            if(path.startsWith("com.sec.android.gallery3d")){
                Log.e("TAG", "It's auto backup pic path:"+selectedImage.toString());
                return null;
            }
        }
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        return picturePath;
    }

    private void numberPickerSetValue(NumberPicker numberPicker,int min ,int max){
        numberPicker.setMaxValue(max);
        numberPicker.setMinValue(min);

    }

}
