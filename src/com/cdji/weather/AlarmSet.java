package com.cdji.weather;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
	private TextView citynameView;
	private int count;
	private Toolbar toolbar;
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_alarm, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home)
		{
			finish();
		}
		if(item.getItemId()==R.id.action_new)
		{
			Intent intent=new Intent();
			Bundle bundle=new Bundle();
			bundle.putString("cityname", cityname);
			intent.putExtras(bundle);
			intent.setClass(this, NewAlarm.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}




	private void initComponet() {
		// TODO Auto-generated method stub
		listView=(ListView) findViewById(R.id.alarm_listview);
		toolbar=(Toolbar) findViewById(R.id.alarmtoolbar);
		citynameView=(TextView) findViewById(R.id.alarmnameview);
		citynameView.setText(cityname);
		toolbar.setTitle("添加提醒");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		list=new ArrayList<WeatherInfo>();
		
	}
	
	//初始化本activity数据
	private void initdate() {
		// TODO Auto-generated method stub
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where cityname=? and isalarm=? order by date asc;",
				new String[] { cityname,"1" });
		list.clear();
		count=0;
		while (cursor.moveToNext()) {
			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setCityname(cursor.getString(2));
			wInfo.setIsalarm(cursor.getString(11));
			wInfo.setIsrecode(cursor.getString(10));
			wInfo.setIssubscribe(cursor.getString(12));
			//时间转换
			long date=Long.valueOf(cursor.getString(3)) ;
			long current=System.currentTimeMillis();
			if(date<current){
				//删除过时的提醒
				searchdb.execSQL("delete from seachedcity where cityname=? and date=?",new String[]{cursor.getString(2),cursor.getString(3)});
				//删除过时提醒后，为空？取消订阅
			if(	cursor.isLast())
			{
				searchdb.execSQL("delete from seachedcity where cityname=? and subscribe=? ;",new String []{cursor.getString(2),"1"});
			}
				
				continue;
			}
			SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
			
			System.out.println(date);
			String string=sFormat.format(date);
			String[] string2=string.split(" ");
			System.out.println(string2[0]+" "+string2[1]);
			wInfo.setDate(string2[0]);
			wInfo.setPubdate(string2[1]);
			
			list.add(wInfo);
			count++;
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
			public View init(Context context, final int position, WeatherInfo item) {
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
						searchdb.execSQL("delete from seachedcity where cityname=? and date=?",new String[]{weatherInfo.getCityname(),String.valueOf(time)});
						list.remove(position);
						dateistAdapter.notifyDataSetInvalidated();
						unregisteralarm(weatherInfo, time);
						count--;
						if(list.isEmpty()){
							//检测是否删除了所有提醒，当提醒为空是，取消标志位。
							searchdb.execSQL("delete from seachedcity where cityname=? and subscribe=? ;",new String []{weatherInfo.getCityname(),"1"});
						}
						searchdb.close();
						
						
						
					}
				});
				
				
				
				
				
				return view;
			}
			
		};
		listView.setAdapter(dateistAdapter);
		
		
	}
	
	public void  unregisteralarm(WeatherInfo wInfo,long time) {
		WeatherInfo weatherInfo=new WeatherInfo();
		weatherInfo.setCityname(wInfo.getCityname());
		weatherInfo.setDate(wInfo.getDate());
		weatherInfo.setPubdate(wInfo.getPubdate());
		Intent intent = new Intent("com.cdji.MY_ALARM");
//		intent.setClass(NewAlarm.this, AlarmSever.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("WeatherInfo", weatherInfo);
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, 0);
		AlarmManager aManager;
		aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		aManager.cancel(pendingIntent);
		Log.i("TAG", "取消了一个闹钟事件" + weatherInfo.getCityname() + weatherInfo.getDate()
				+ weatherInfo.getPubdate());
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
