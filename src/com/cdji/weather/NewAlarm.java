package com.cdji.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cdji.weathertool.Calendarhelp;
import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.WeatherInfo;

public class NewAlarm extends AppCompatActivity {
	private Toolbar toolbar;
	private String cityname;
	private TextView dateView;
	private TextView timeView;
	private TextView citynameView;
	private WeatherInfo wInfo = new WeatherInfo();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_newalarm);
		Intent intent = getIntent();
		cityname = intent.getExtras().getString("cityname");
		wInfo.setCityname(cityname);
		Initcompont();
	}

	private void Initcompont() {
		// TODO Auto-generated method stub
		dateView = (TextView) findViewById(R.id.dateset);
		timeView = (TextView) findViewById(R.id.timeset);
		citynameView = (TextView) findViewById(R.id.newalarmcityname);
		citynameView.setText(cityname);
		toolbar = (Toolbar) findViewById(R.id.id_newalarmtoolbar);
		toolbar.setTitle("新建提醒");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		dateView.setOnClickListener(datepicklistener);
		timeView.setOnClickListener(timePickerlistener);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		getMenuInflater().inflate(R.menu.menu_newalarm, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			finish();
		}

		if (item.getItemId() == R.id.action_save) {
			long time = 0;
			// 保存数据，注意判断是否是第一个。
			String dateytimeString = dateView.getTag().toString() + " "
					+ timeView.getTag().toString();
			wInfo.setDate(dateView.getTag().toString());
			wInfo.setPubdate(timeView.getTag().toString());

			SimpleDateFormat newDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm");
			try {
				time = newDateFormat.parse(dateytimeString).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long current = System.currentTimeMillis();

			if (time < current) {
				Toast.makeText(this, "設置時間不行比當前時間早！", Toast.LENGTH_SHORT)
						.show();

				return false;
			} else {
				DataBaseHelper dbhelper = new DataBaseHelper(this);
				SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
				Cursor cursor = searchdb
						.rawQuery(
								"select * from seachedcity where subscribe=? and cityname=?",
								new String[] { "1", cityname });
				if (cursor.moveToNext()) {
					cursor.close();
					Cursor cursor2 = searchdb
							.rawQuery(
									"select * from seachedcity where date=? and cityname=?",
									new String[] { time + "", cityname });
					if (!cursor2.moveToNext()) {
						cursor2.close();
						searchdb.execSQL(
								"insert into seachedcity (id_citiy,cityname,date,isalarm) values(?,?,?,?);",
								new String[] { "1", cityname, time + "", "1" });
						registeralarm(wInfo, time);

						Toast.makeText(this, "已有城市，提醒保存成功！", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(this, "已设置过该时刻的提醒！", Toast.LENGTH_SHORT)
								.show();
						searchdb.close();
						return false;
					}
				} else {
					searchdb.execSQL(
							"insert into seachedcity (id_citiy,cityname,subscribe) values(?,?,?);",
							new String[] { "1", cityname, "1" });
					searchdb.execSQL(
							"insert into seachedcity (id_citiy,cityname,date,isalarm) values(?,?,?,?);",
							new String[] { "1", cityname, time + "", "1" });
					registeralarm(wInfo, time);
					Toast.makeText(this, "新添加城市，提醒保存成功！", Toast.LENGTH_SHORT)
							.show();
				}

				searchdb.close();

			}
		}
		return super.onOptionsItemSelected(item);
	}

	private OnClickListener timePickerlistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			TimePickerDialog timePicker = new TimePickerDialog(NewAlarm.this,
					timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
			timePicker.setTitle("请选择时间:");
			timePicker.setMessage("选择适合您的时间");
			timePicker.setIcon(R.drawable.ic_launcher);
			timePicker.show();
		}

	};
	private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// TODO Auto-generated method stub
			String string;
			if (view.getCurrentMinute() < 10) {
				string = view.getCurrentHour() + ":0" + view.getCurrentMinute();
			} else {
				string = view.getCurrentHour() + ":" + view.getCurrentMinute();
			}

			timeView.setText(string);
			timeView.setTag(string);
		}
	};

	private OnClickListener datepicklistener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			DatePickerDialog datePickerDialog = new DatePickerDialog(
					NewAlarm.this, dateSetListener, calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
			datePickerDialog.setTitle("选择日期");
			datePickerDialog.setMessage("请选择合适的日期");
			datePickerDialog.setIcon(R.drawable.ic_launcher);
			datePickerDialog.setCancelable(false);
			datePickerDialog.show();
		}
	};

	private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			String string = view.getYear() + "-" + (view.getMonth() + 1) + "-"
					+ view.getDayOfMonth();
			String str = Calendarhelp.formatDateTime(string);
			dateView.setText(str);
			dateView.setTag(string);
		}
	};

	//注册闹钟时间
	private void registeralarm(WeatherInfo wInfo2, long time) {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.cdji.MY_ALARM");
		// intent.setClass(NewAlarm.this, AlarmSever.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("WeatherInfo", wInfo2);
		System.out.println("registeralarm" + wInfo2.getCityname()
				+ wInfo2.getDate() + wInfo2.getPubdate());
		intent.putExtras(bundle);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 

		// 3.1以后的版本直接设置Intent.FLAG_INCLUDE_STOPPED_PACKAGES的value：32

		if (android.os.Build.VERSION.SDK_INT >= 12) {

		    intent.setFlags(32);

		}
		
		//使用闹钟在数据表中的唯一ID作为pendingIntent的ID，避免pendingIntent更新	
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb
				.rawQuery(
						"select * from seachedcity where cityname=? and date=? and isalarm=? order by date asc;",
						new String[] { wInfo2.getCityname(), time + "", "1" });
		if (cursor.moveToNext()) {
			PendingIntent pendingIntent = PendingIntent
					.getBroadcast(this, cursor.getInt(0), intent,
							PendingIntent.FLAG_UPDATE_CURRENT);
			
			
			AlarmManager aManager;
			aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			long time2=time-System.currentTimeMillis();
			aManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
			
//			aManager.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()+time2, pendingIntent);
			Log.i("TAG", "注册了一个闹钟事件 " + wInfo2.getCityname() +" "+ wInfo2.getDate()
					+ wInfo2.getPubdate());
  
		} else {
			Log.i("TAG", "没有存入数据库，注册失败！");
		}
	}

}
