package com.cdji.weathertool;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper {
	private static final int vision = 1;

	public DataBaseHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, "seachedcity", null, vision);
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		arg0.execSQL("CREATE TABLE IF NOT EXISTS seachedcity (id_search  integer primary key autoincrement,id_citiy varchar(20),cityname varchar(20),date varchar(20),weathinfo varchar(20),windirection varchar(20),windforce varchar(20),presentemp varchar(20),lowesttemp varchar(20),heightesttemp varchar(20),isrecode varchar(20),isalarm integer,subscribe varchar(20));");
//		arg0.execSQL("CREATE TABLE IF NOT EXISTS person (personid integer primary key autoincrement, name varchar(20), age INTEGER)");  
		}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table if exists seachedcity;");
	}

}
