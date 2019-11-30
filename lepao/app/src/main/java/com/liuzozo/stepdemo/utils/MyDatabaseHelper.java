package com.liuzozo.stepdemo.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

/**
 * 数据库类
 * 参考这个写你们自己的  跑步的记录的类 的数据库和表
 */
public class MyDatabaseHelper extends SQLiteOpenHelper{

	public static final String CREATE_BOOK_TABLE = 
	"create table book ("+
	"id integer primary key autoincrement," +
	"author text, "+
	"pages integer)";
	
	private Context mContext;
	
	public MyDatabaseHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		mContext = context;
	}
	// 如果数据库不存在、则会执行，否者不会执行
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_BOOK_TABLE);
		// create other table
		Toast.makeText(mContext, "create successed", Toast.LENGTH_SHORT).show();
	}

	// 创建数据库不会执行，增大版本号才会执行
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// 在这里可以把旧的表drop掉，从而创建新的表
	}
}
