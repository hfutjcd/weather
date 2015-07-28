package com.cdji.weather;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cdji.weathertool.DataBaseHelper;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.UnderstanderResult;


public class MainActivity extends AppCompatActivity {
	private EditText city_text;
	private Button search_btn;
	private Button delete_btn;
	private Button speeh_btn;
	private Toolbar toolbar;
	private static String TAG = MainActivity.class.getSimpleName();
	// 语义理解对象（语音到语义）。
	private SpeechUnderstander mSpeechUnderstander;
	private Toast mToast;

	private SharedPreferences mSharedPreferences;

	// private TableLayout hot_city_tab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		hasSearcedhistory();
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=55af0dd2");
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(
				MainActivity.this, speechUnderstanderListener);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mSharedPreferences = getSharedPreferences("com.iflytek.setting",
				Activity.MODE_PRIVATE);
	}

	/**
	 * 初始化监听器（语音到语义）。
	 */

	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "speechUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("初始化失败,错误码：" + code);
			}
		}
	};

	private void showTip(final String str) {
		mToast.setText(str);
		mToast.show();
	}

	private SpeechUnderstanderListener mRecognizerListener = new SpeechUnderstanderListener() {

		@Override
		public void onResult(final UnderstanderResult result) {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (null != result) {
						// 显示
						String text = result.getResultString();
						if (!TextUtils.isEmpty(text)) {
							System.out.println(text);
							try {
								JSONObject json1 = new JSONObject(text);
								String str1 = json1.getString("semantic");
								JSONObject json2 = new JSONObject(str1);
								String str2 = json2.getString("slots");
								JSONObject json3 = new JSONObject(str2);
								String str3 = json3.getString("location");
								JSONObject json4 = new JSONObject(str3);
								String str4 = json4.getString("cityAddr");
								System.out.println(str4);
								MainActivity.this.seachweather(str4);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else {
						showTip("识别结果不正确。");
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int v) {
			showTip("onVolumeChanged：" + v);
		}

		@Override
		public void onEndOfSpeech() {
			showTip("onEndOfSpeech");
		}

		@Override
		public void onBeginOfSpeech() {
			showTip("onBeginOfSpeech");
		}

		@Override
		public void onError(SpeechError error) {
			showTip("onError Code：" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// TODO Auto-generated method stub

		}
	};

	private void hasSearcedhistory() {
		// TODO Auto-generated method stub
		DataBaseHelper dbhelper = new DataBaseHelper(this);
		SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
		// searchdb.execSQL("drop table if exists seachedcity;");
		//添加的测试城市
		SimpleDateFormat sFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		long time=0;
		
		try {
			time=sFormat.parse("2015-7-27 11:22").getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(sFormat.format(time));	
		
		if (time!=0) {
			System.out.println(time);
		
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isrecode) values(\"1\",\"合肥\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,subscribe) values(\"1\",\"合肥\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isrecode) values(\"1\",\"北京\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,subscribe) values(\"1\",\"北京\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isrecode) values(\"1\",\"上海\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,subscribe) values(\"1\",\"上海\","+time+",\"1\");");
//			
//			
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"合肥\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"北京\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"上海\","+time+",\"1\");");
//			
//			try {
//				time=sFormat.parse("2015-7-28 11:22").getTime();
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"合肥\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"北京\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"上海\","+time+",\"1\");");
//			
//			
//			try {
//				time=sFormat.parse("2015-7-29 12:32").getTime();
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"北京\","+time+",\"1\");");
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"1\",\"上海\","+time+",\"1\");");
//				
//			
//			
//			try {
//				time=sFormat.parse("2015-7-30 10:22").getTime();
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"2\",\"上海\","+time+",\"1\");");
//			
//			try {
//				time=sFormat.parse("2015-7-31 13:22").getTime();
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			searchdb.execSQL("insert into seachedcity (id_citiy,cityname,date,isalarm) values(\"3\",\"上海\","+time+",\"1\");");		
		}
		System.out.println("添加模拟天气成功");
		
		
		///////////////
		
		Cursor cursor = searchdb.rawQuery(
				"select * from seachedcity where isrecode=?;",
				new String[]{"1"});
		
		if (cursor.moveToFirst()) {
			// cursor.moveToNext();
			System.out.println("有订阅天气");
			String strnameString = cursor.getString(2);
			System.out.println(cursor.getString(2));
			while (cursor.moveToNext()) {
				System.out.println(cursor.getString(2));
			}
			seachweather(strnameString);
		}
		cursor.close();
		searchdb.close();
		dbhelper.close();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 退出时释放连接
		mSpeechUnderstander.cancel();
		mSpeechUnderstander.destroy();
	}

	/**
	 * 参数设置
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		String lag = mSharedPreferences.getString(
				"understander_language_preference", "mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lag);
		}
		// 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("understander_vadbos_preference",
						"4000"));

		// 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("understander_vadeos_preference",
						"1000"));

		// 设置标点符号，默认：1（有标点）
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("understander_punc_preference",
						"1"));

		// 设置音频保存路径，保存音频格式仅为pcm，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
	}

	// 初始化按钮响应事件
	private void init() {
		// TODO Auto-generated method stub
		city_text = (EditText) findViewById(R.id.city_text);
		search_btn = (Button) findViewById(R.id.search);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		speeh_btn = (Button) findViewById(R.id.speeh_btn);
		toolbar=(Toolbar) findViewById(R.id.maintoolbar);
		
		toolbar.setTitle("选择查询城市");
		setSupportActionBar(toolbar);
		

		search_btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// // TODO Auto-generated method stub
				seachweather(city_text.getText().toString());
			}
		});

		delete_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				DataBaseHelper dbhelper = new DataBaseHelper(MainActivity.this);
				SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
				searchdb.execSQL("delete from seachedcity where isrecode=\"1\";");
				// searchdb.delete("seachedcity", "isrecode", new
				// String[]{"1"});
				// searchdb.execSQL("delete from seachedcity; select * from seachedcity;update seachedcity set seq=0 where name=seachedcity; ");
				searchdb.close();
			}
		});

		speeh_btn.setOnClickListener(new OnClickListener() {
			int ret = 0;// 函数调用返回值

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setParam();
				if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
					mSpeechUnderstander.stopUnderstanding();
					showTip("停止录音");
				} else {
					ret = mSpeechUnderstander
							.startUnderstanding(mRecognizerListener);
					if (ret != 0) {
						showTip("语义理解失败,错误码:" + ret);
					} else {
						showTip(getString(R.string.text_begin));
					}
				}
			}
		});

		// hot_city_tab=(TableLayout) findViewById(R.id.tablegrid);
		// 添加所有热门城市的响应事件
		List<TextView> textview_list = new ArrayList<TextView>();
		textview_list.add((TextView) findViewById(R.id.beijing));
		textview_list.add((TextView) findViewById(R.id.tianjing));
		textview_list.add((TextView) findViewById(R.id.shanghai));
		textview_list.add((TextView) findViewById(R.id.congqing));
		textview_list.add((TextView) findViewById(R.id.shengyang));
		textview_list.add((TextView) findViewById(R.id.dalian));
		textview_list.add((TextView) findViewById(R.id.changcun));
		textview_list.add((TextView) findViewById(R.id.haerbin));
		textview_list.add((TextView) findViewById(R.id.zhengzou));
		textview_list.add((TextView) findViewById(R.id.wuhan));
		textview_list.add((TextView) findViewById(R.id.changsha));
		textview_list.add((TextView) findViewById(R.id.guangzou));
		textview_list.add((TextView) findViewById(R.id.hefei));
		textview_list.add((TextView) findViewById(R.id.shenzen));
		textview_list.add((TextView) findViewById(R.id.nanjing));
		textview_list.add((TextView) findViewById(R.id.xian));
		textview_list.add((TextView) findViewById(R.id.wuxi));
		textview_list.add((TextView) findViewById(R.id.changzou));
		textview_list.add((TextView) findViewById(R.id.shuzou));
		textview_list.add((TextView) findViewById(R.id.hangzou));
		textview_list.add((TextView) findViewById(R.id.ningbo));
		textview_list.add((TextView) findViewById(R.id.jinan));
		textview_list.add((TextView) findViewById(R.id.qingdao));
		textview_list.add((TextView) findViewById(R.id.fuzou));
		textview_list.add((TextView) findViewById(R.id.xiamen));
		textview_list.add((TextView) findViewById(R.id.chengdu));
		textview_list.add((TextView) findViewById(R.id.kunming));
		textview_list.add((TextView) findViewById(R.id.taian));
		TableListener hclistener = new TableListener();
		for (int i = 0; i < textview_list.size() - 1; i++) {
			textview_list.get(i).setOnClickListener(hclistener);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public class TableListener implements OnClickListener {
		// private EditText editText;
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			seachweather(((TextView) v).getText().toString());
		}

	}

	public void seachweather(String cityname) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putString("cityname", cityname);
		intent.putExtras(bundle);
		intent.setClass(this, Weather.class);
		startActivity(intent);
	}
	public static Resources getResource()
	{
		return getResource();
	}

}
