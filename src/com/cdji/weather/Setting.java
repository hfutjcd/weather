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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	private void initComponet() {
		toolbar = (Toolbar) findViewById(R.id.toolbar);
		listView = (ListView) findViewById(R.id.subscribelist);

		toolbar.setTitle(" ’ªıµÿ÷∑");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		initlistview();
	}

	private void initlistview() {
		// TODO Auto-generated method stub
		list = new ArrayList<WeatherInfo>();
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where subscribe=?",
				new String[] { "1" });
		while (cursor.moveToNext()) {
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
				textView.setOnClickListener(new ItemClickedListener(context,
						item, position));
				return view;
			}

		};
		
		listView.setAdapter(citylistAdapter);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getItemId()==android.R.id.home)
		{
			finish();
		}
		if(item.getItemId()==R.id.action_settings)
		{
			Intent intent=new Intent();
			intent.setClass(this, SelectAddedCiyt.class);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}

	public class ItemClickedListener implements OnClickListener {
		private WeatherInfo weatherInfo;
		private int position;
		private Context context;
		private Dialog dialog;
		private TextView deletebtn,settingalarmbtn;

		public ItemClickedListener(Context context, WeatherInfo weatherInfo,
				int position) {
			this.context = context;
			this.weatherInfo = weatherInfo;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			dialog = new Dialog(context);
			View view = LayoutInflater.from(context).inflate(
					R.layout.settingdialog, null);
			deletebtn=(TextView) view.findViewById(R.id.delete_btn);
			settingalarmbtn=(TextView) view.findViewById(R.id.settingalarm_btn);
			deletebtn.setOnClickListener(new OnClickListener() {
					
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					String string=weatherInfo.getCityname();
					String issubscribe=weatherInfo.getIssubscribe();
					DataBaseHelper dbhelper = new DataBaseHelper(context);
					SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
					searchdb.execSQL(
							"delete from seachedcity where cityname=? and subscribe=?",
							new String[] {string,issubscribe});
					searchdb.close();
					System.out.println(string+"  "+issubscribe);
					dialog.dismiss();
					list.remove(position);
					Setting.this.citylistAdapter.notifyDataSetInvalidated();
				}
			});
			
			settingalarmbtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent();
					Bundle bundle=new Bundle();
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

		}

	}
}
