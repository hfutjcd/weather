package com.cdji.weather;

import java.util.ArrayList;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.cdji.weathertool.CitylistAdapter;
import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.WeatherInfo;

public class Setting extends AppCompatActivity {

	private Toolbar toolbar;
	private ListView listView;
	private List<WeatherInfo> list;
	private CitylistAdapter<WeatherInfo> citylistAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		initComponet();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		initlistview();
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_setting, menu);
		return true;
	}

	private void initComponet() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		listView = (ListView) findViewById(R.id.subscribelist);

		toolbar.setTitle("设置");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	}

	private void initlistview() {
		// TODO Auto-generated method stub
		if (list == null) {
			list = new ArrayList<WeatherInfo>();
		}
		list.clear();
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where subscribe=?",
				new String[] { "1" });
		while (cursor.moveToNext()) {
			//检测是否所有闹钟已过期
			Cursor cursor2 = searchdb
					.rawQuery(
							"select * from seachedcity where cityname=? and isalarm=? order by date desc",
							new String[] { cursor.getString(2), "1" });//按降序排列，使最晚的闹钟在第一行
			if (cursor2.moveToNext()) {
				long date = Long.valueOf(cursor2.getString(3));  
				if (date < System.currentTimeMillis()) {
					//所有闹钟已过期，删除所有闹钟，删除所有订阅。
					searchdb.execSQL("delete from seachedcity where cityname=? and subscribe=? ;",new String []{cursor2.getString(2),"1"});
					searchdb.execSQL("delete from seachedcity where cityname=? and isalarm=? ;",new String []{cursor2.getString(2),"1"});
					cursor2.close();
					continue;
				}
				cursor2.close();
				
			}

			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setCityname(cursor.getString(2));
			wInfo.setIsalarm(cursor.getString(11));
			wInfo.setIsrecode(cursor.getString(10));
			wInfo.setIssubscribe(cursor.getString(12));
			list.add(wInfo);
		}
		cursor.close();
		searchdb.close();
		if (list.isEmpty()) {
			return;
		}
		citylistAdapter = new CitylistAdapter<WeatherInfo>(this, list) {

			@Override
			public View init(Context context, int position, WeatherInfo item) {
				// TODO Auto-generated method stub
				LayoutInflater layoutInflater = (LayoutInflater) context
						.getSystemService(LAYOUT_INFLATER_SERVICE);
				View view = layoutInflater.inflate(R.layout.citylistitem, null);
				TextView textView = (TextView) view
						.findViewById(R.id.itemcityname);
				textView.setText(item.getCityname());
				textView.setOnLongClickListener(new ItemClickedListener(
						context, item, position));
				return view;
			}

		};

		listView.setAdapter(citylistAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
		}
		if (item.getItemId() == R.id.action_adding) {
			Intent intent = new Intent();
			intent.setClass(this, AddcityList.class);
			startActivity(intent);
		}

		return super.onOptionsItemSelected(item);
	}

	public class ItemClickedListener implements OnLongClickListener {
		private WeatherInfo weatherInfo;
		private int position;
		private Context context;
		private Dialog dialog;
		private TextView deletebtn, settingalarmbtn;

		public ItemClickedListener(Context context, WeatherInfo weatherInfo,
				int position) {
			this.context = context;
			this.weatherInfo = weatherInfo;
			this.position = position;
		}

		@Override
		public boolean onLongClick(View v) {
			// TODO Auto-generated method stub
			dialog = new Dialog(context);
			View view = LayoutInflater.from(context).inflate(
					R.layout.settingdialog, null);
			deletebtn = (TextView) view.findViewById(R.id.delete_btn);
			settingalarmbtn = (TextView) view
					.findViewById(R.id.settingalarm_btn);
			deletebtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String string = weatherInfo.getCityname();
					String issubscribe = weatherInfo.getIssubscribe();
					DataBaseHelper dbhelper = new DataBaseHelper(context);
					SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
					searchdb.execSQL(
							"delete from seachedcity where cityname=? and subscribe=?",
							new String[] { string, issubscribe });
					searchdb.execSQL(
							"delete from seachedcity where cityname=? and isalarm=?",
							new String[] { string, "1" });
					searchdb.close();
					System.out.println(string + "  " + issubscribe);
					dialog.dismiss();
					list.remove(position);
					Setting.this.citylistAdapter.notifyDataSetInvalidated();
				}
			});

			settingalarmbtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putString("cityname", weatherInfo.getCityname());
					intent.putExtras(bundle);
					intent.setClass(Setting.this, AlarmSet.class);
					startActivity(intent);
					dialog.dismiss();
				}
			});
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			dialog.setContentView(view);
			dialog.show();

			return false;
		}

	}
}
