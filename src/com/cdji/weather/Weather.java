package com.cdji.weather;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.Nettool;
import com.cdji.weathertool.Pageguider;
import com.cdji.weathertool.WeatherInfo;

public class Weather extends Activity {
	private String cityname;
	private WeatherInfo weathinfo;
	private TextView weatherinfoTextView;
	private TextView weatherdateTextView;
	private ViewPager viewPager;
	private List<View> list = new ArrayList<View>();
	private List<WeatherInfo> listweathInfos = new ArrayList<WeatherInfo>();
	private WpagerAdapter wAdapter = null;
	private Pageguider pageguider;
	private LinearLayout linearLayout;
	private Button setting_btn;
	public static ExecutorService threadpool = Executors.newFixedThreadPool(5);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_weathershow);
		try {
			cityname =getIntent().getExtras().getString("cityname");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			cityname = "石家庄";
			e.printStackTrace();
		}

		System.out.println(cityname);
		getWeatherinfo(cityname);
		init();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		
		
		super.onResume();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		// TODO Auto-generated method stub
		weatherinfoTextView = (TextView) findViewById(R.id.weatherinfo_detail);
		viewPager = (ViewPager) findViewById(R.id.pager);
		linearLayout=(LinearLayout) findViewById(R.id.guider);
		setting_btn=(Button) findViewById(R.id.setting_btn);
		setting_btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent();
				intent.setClass(Weather.this, Setting.class);
				startActivity(intent);
				finish();
			}
		});
		pageguider=new Pageguider(this);
		pageguider.setLinearlayout(linearLayout);
		randerlist();
		pageguider.setcount(list.size());
		pageguider.setcusor(0);
		preloadweader();
		wAdapter = new WpagerAdapter(this, list);
		viewPager.setAdapter(wAdapter);
		viewPager.setOnPageChangeListener(listener);

	}

	private void preloadweader() {
		// TODO Auto-generated method stub
		for (int i = 0; i < listweathInfos.size(); i++) {
			Preload prelaod = new Preload();
			System.out.println("preload "+listweathInfos.get(i).getCityname());
			prelaod.executeOnExecutor(threadpool,listweathInfos.get(i).getCityname());
		}
	}

	private void randerlist() {
		// TODO Auto-generated method stub
		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout1 = inflater.inflate(R.layout.viewpageritem, null);
		TextView citynameTextView1 = (TextView) layout1
				.findViewById(R.id.weather_cityname_text);
		TextView citydaTextView1 = (TextView) layout1
				.findViewById(R.id.weather_date_text);
		getWeatherinfo(cityname);
		weatherdateTextView = citydaTextView1;
		citynameTextView1.setText(cityname);
		list.add(layout1);
		WeatherInfo wInfo1 = new WeatherInfo();
		wInfo1.setCityname(cityname);
		listweathInfos.add(wInfo1);
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where isrecode=? and cityname!=?",
				new String[] { "1", cityname });
		while (cursor.moveToNext()) {
			View layout = inflater.inflate(R.layout.viewpageritem, null);
			TextView citynameTextView = (TextView) layout
					.findViewById(R.id.weather_cityname_text);
			// TextView citydaTextView=(TextView)
			// layout.findViewById(R.id.weather_date_text);
			citynameTextView.setText(cursor.getString(2));
			list.add(layout);
			WeatherInfo wInfo = new WeatherInfo();
			wInfo.setCityname(cursor.getString(2));
			listweathInfos.add(wInfo);
		}
		cursor.close();
		searchdb.close();
	}

	// 获取天气信息
	private void getWeatherinfo(String cityname) {
		Asyweatherget wget = new Asyweatherget();
		wget.execute(cityname);
	}

	// 显示天气信息，如果不在查询历史列表中，则加入到列表。
	public void showweather(WeatherInfo weatherInfo) {
		weatherdateTextView.setText(weatherInfo.getDate()
				+ weatherInfo.getweek());
		weatherinfoTextView.setText("天气状况："+weatherInfo.getWeathinfo() + "\n\n"
				+"现在温度："+ weatherInfo.getPresentemp() + "\n"
				+"最高温度："+ weatherInfo.getHeightesttemp() + "\n"
				+"最低温度："+ weatherInfo.getLowesttemp() + "\n"
				+"风向："+weatherInfo.getWindirection()+"\n"
				+"风力："+weatherInfo.getWindforce()+"\n");

		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where cityname=? and isrecode=?",
				new String[] { weatherInfo.getCityname(),"1"});
		if (!cursor.moveToFirst()) {
			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,isrecode) values(?,?,?);",new String[]{weatherInfo.getId_city(),weatherInfo.getCityname(),"1"});
		}
		cursor.close();
		searchdb.close();
	}
	// 获取天气信息
	public class Asyweatherget extends AsyncTask<String, String, WeatherInfo> {

		@Override
		protected WeatherInfo doInBackground(String... params) {
			// TODO Auto-generated method stub
			System.out.println("do in back ground" + params[0]);
			WeatherInfo resultInfo = Nettool.getInfo(params[0]);
			return resultInfo;
		}

		@Override
		protected void onPostExecute(WeatherInfo result) {
			// TODO Auto-generated method stub
			if (result != null) {
				Weather.this.showweather(result);
				System.out.println("联网成功");
				// 保存预加载结果
				for (int i = 0; i < listweathInfos.size(); i++) {
					if (listweathInfos.get(i).getCityname() .equals(result.getCityname())) {
						listweathInfos.remove(i);
						listweathInfos.add(i,result);
						System.out.println(result.getWeathinfo());
					}
				}
				
			} else {
				System.out.println("联网失败");
				Toast.makeText(Weather.this, "查询失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	public class Preload extends AsyncTask<String, String, WeatherInfo> {

		@Override
		protected WeatherInfo doInBackground(String... params) {
			// TODO Auto-generated method stub
			WeatherInfo resultInfo = Nettool.getInfo(params[0]);
			return resultInfo;
		}

		@Override
		protected void onPostExecute(WeatherInfo result) {
			// TODO Auto-generated method stub
			if (result != null) {
				// 保存预加载结果
				for (int i = 0; i < listweathInfos.size(); i++) {
					if (listweathInfos.get(i).getCityname() .equals(result.getCityname())) {
						listweathInfos.remove(i);
						listweathInfos.add(i,result);
						System.out.println(result.getWeathinfo());
					}
				}
			} else {
				System.out.println("联网失败");
				Toast.makeText(Weather.this, "查询失败", Toast.LENGTH_SHORT).show();
			}
		}
	}

	// viewpager 适配器
	public class WpagerAdapter extends PagerAdapter {

		private List<View> list;
		private Context context;

		WpagerAdapter(Context context, List<View> list) {
			this.list = list;
			this.context = context;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(list.get(position));// 删除页卡
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			// TODO Auto-generated method stub
			View view = list.get(position);
			container.addView(view);
			return view;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}

	}

	// viewpager 监听器
	private OnPageChangeListener listener = new OnPageChangeListener() {

		@Override
		public void onPageSelected(int arg0) {
			// indicator.setCurrentPosition(arg0%5);
			System.out.println("listener" + arg0);
			View view = list.get(arg0);
			pageguider.setcusor(arg0);
			Weather.this.weatherdateTextView = (TextView) view
					.findViewById(R.id.weather_date_text);
			System.out.println(listweathInfos.get(arg0).getWeathinfo());
			if(listweathInfos.get(arg0).getWeathinfo()!=null)
			{
				System.out.println("show preload result"+listweathInfos.get(arg0).getCityname());
				System.out.println("already loaded");
				Weather.this.showweather(listweathInfos.get(arg0));
				
			}
			else{
				System.out.println("show downloading result"+listweathInfos.get(arg0).getCityname());
						Weather.this.getWeatherinfo(((TextView) view
					.findViewById(R.id.weather_cityname_text)).getText()
					.toString());
			Weather.this.weatherinfoTextView.setText("");
			System.out
					.println("listener "
							+ ((TextView) view
									.findViewById(R.id.weather_cityname_text))
									.getText().toString());	
			}

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};
}
