package com.cdji.weather;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.cdji.weathertool.CitylistAdapter;
import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.WeatherInfo;

public class AlarmSet extends AppCompatActivity {
	private List<WeatherInfo>  list;
	private ListView listView;
	private CitylistAdapter<WeatherInfo> dateistAdapter;
	private String cityname;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		Intent intent=getIntent();
		cityname=(String) intent.getExtras().get("cityname");
		initComponet();
	}
	
	
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initdate();
		super.onResume();
	}




	private void initComponet() {
		// TODO Auto-generated method stub
		listView=(ListView) findViewById(R.id.alarm_listview);
		list=new ArrayList<WeatherInfo>();
		
	}
	//初始化本activity数据
	private void initdate() {
		// TODO Auto-generated method stub
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where cityname=? and isalarm=?",
				new String[] { cityname,"1" });
		list.clear();
		while (cursor.moveToNext()) {
			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setCityname(cursor.getString(2));
			wInfo.setIsalarm(cursor.getString(11));
			wInfo.setIsrecode(cursor.getString(10));
			wInfo.setIssubscribe(cursor.getString(12));
			//时间转换
			long date=Long.valueOf(cursor.getString(3)) ;
			SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			System.out.println(date);
			String string=sFormat.format(date);
			String[] string2=string.split(" ");
			System.out.println(string2[0]+" "+string2[1]);
			wInfo.setDate(string2[0]);
			wInfo.setPubdate(string2[1]);
			
			list.add(wInfo);
		}
		cursor.close();
		searchdb.close();
		if (list.isEmpty()) {
			System.out.println("没有提醒信息");
			return;
		}
		System.out.println("有提醒信息");
		dateistAdapter=new CitylistAdapter<WeatherInfo>(this,list) {
			
			@Override
			public View init(Context context, int position, WeatherInfo item) {
				// TODO Auto-generated method stub
				final WeatherInfo weatherInfo=item;
				LayoutInflater lInflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View view =lInflater.inflate(R.layout.alarm_listview_item, null);
				TextView daTextView=(TextView) view.findViewById(R.id.weatherdate);
				TextView timeTextView=(TextView) view.findViewById(R.id.weathertime);
				TextView weekTextView=(TextView) view.findViewById(R.id.weekday);
				Button deletebtn=(Button) view.findViewById(R.id.delete_city_btn);
				
				daTextView.setText(item.getDate());
				timeTextView.setText(item.getPubdate());
				weekTextView.setText(item.getweek());
				
				deletebtn.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
						long time=0;
						
						try {
							time=sFormat.parse(weatherInfo.getDate()+" "+weatherInfo.getPubdate()).getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						DataBaseHelper dbhelper = new DataBaseHelper(AlarmSet.this);
						SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
						searchdb.execSQL("delete * from seachedcity where cityname="+ weatherInfo.getCityname()+"and date= "+time+";");
						searchdb.close();
						
						
						
					}
				});
				
				
				
				
				
				return view;
			}
			
		};
		listView.setAdapter(dateistAdapter);
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
