package com.cdji.weather;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cdji.weathertool.Calendarhelp;
import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.WeatherInfo;
import com.iflytek.cloud.InitListener;
import android.R.integer;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

		dateView.setOnClickListener(new OnClickListener() {
			private Dialog dialog;
			private DatePicker datePicker;
			private Button button;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = new Dialog(NewAlarm.this);
				View view = LayoutInflater.from(NewAlarm.this).inflate(
						R.layout.datedialog, null);
				datePicker = (DatePicker) view.findViewById(R.id.datePicker1);
				button = (Button) view.findViewById(R.id.date_ok);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String string = datePicker.getYear() + "-"
								+ (datePicker.getMonth() + 1) + "-"
								+ datePicker.getDayOfMonth();
						String str = Calendarhelp.formatDateTime(string);
						dateView.setText(str);
						dateView.setTag(string);
						dialog.dismiss();
					}

				});

				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(view);
				dialog.show();

			}
		});

		timeView.setOnClickListener(new OnClickListener() {
			private Dialog dialog;
			private TimePicker timePicker;
			private Button button;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog = new Dialog(NewAlarm.this);
				View view = LayoutInflater.from(NewAlarm.this).inflate(
						R.layout.timedialog, null);
				timePicker = (TimePicker) view.findViewById(R.id.timePicker1);
				button = (Button) view.findViewById(R.id.time_ok);
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						// String
						// string=timePicker.get()+"-"+(datePicker.getMonth()+1)+"-"+datePicker.getDayOfMonth();
						String string;
						if (timePicker.getCurrentMinute() < 10) {
							string = timePicker.getCurrentHour() + ":0"
									+ timePicker.getCurrentMinute();
						} else {
							string = timePicker.getCurrentHour() + ":"
									+ timePicker.getCurrentMinute();
						}

						timeView.setText(string);
						timeView.setTag(string);
						dialog.dismiss();
					}
				});

				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				dialog.setContentView(view);
				dialog.show();

			}
		});

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

	private void registeralarm(WeatherInfo wInfo2, long time) {
		// TODO Auto-generated method stub
		Intent intent = new Intent("com.cdji.MY_ALARM");
//		intent.setClass(NewAlarm.this, AlarmSever.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("WeatherInfo", wInfo2);
		System.out.println("registeralarm"+wInfo2.getCityname() +wInfo2.getDate()+wInfo2.getPubdate());
		intent.putExtras(bundle);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager aManager;
		aManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		aManager.set(AlarmManager.RTC_WAKEUP, time,
				pendingIntent);
		Log.i("TAG", "注册了一个闹钟事件" + wInfo2.getCityname() + wInfo2.getDate()
				+ wInfo2.getPubdate());
//		sendBroadcast(intent);
	}

}
