package com.cdji.weather;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
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
	private void initdate() {
		// TODO Auto-generated method stub
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where cityname=?",
				new String[] { "北京" });
		list.clear();
		while (cursor.moveToNext()) {
			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setCityname(cursor.getString(2));
			wInfo.setIsalarm(cursor.getString(11));
			wInfo.setIsrecode(cursor.getString(10));
			wInfo.setIssubscribe(cursor.getString(12));
			
			long date=Long.valueOf(cursor.getString(3)) ;
			System.out.println(date);
			
			SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
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
				LayoutInflater lInflater=(LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View view =lInflater.inflate(R.layout.alarm_listview_item, null);
				TextView daTextView=(TextView) view.findViewById(R.id.weatherdate);
				TextView timeTextView=(TextView) view.findViewById(R.id.weathertime);
				TextView weekTextView=(TextView) view.findViewById(R.id.weekday);
				Button deletebtn=(Button) view.findViewById(R.id.delete_city_btn);
				
				SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
				
				
				
				
				daTextView.setText(item.getDate());
				timeTextView.setText(item.getPubdate());
				weekTextView.setText(item.getweek());
				
				
				
				
				
				
				
				return view;
			}
			
		};
		listView.setAdapter(dateistAdapter);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
