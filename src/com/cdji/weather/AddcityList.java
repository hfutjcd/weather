package com.cdji.weather;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class AddcityList extends AppCompatActivity {
	private EditText city_text;
	private Button search_btn;
	private Button delete_btn;
	private Button speeh_btn;
	private Toolbar toolbar;
	private static String TAG = AddcityList.class.getSimpleName();
	// �������������������壩��
	private SpeechUnderstander mSpeechUnderstander;
	private Toast mToast;

	private SharedPreferences mSharedPreferences;

	// private TableLayout hot_city_tab;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
		SpeechUtility.createUtility(this, SpeechConstant.APPID + "=55af0dd2");
		mSpeechUnderstander = SpeechUnderstander.createUnderstander(
				AddcityList.this, speechUnderstanderListener);

		mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

		mSharedPreferences = getSharedPreferences("com.iflytek.setting",
				Activity.MODE_PRIVATE);
	}
	
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId()==android.R.id.home)
		{
			finish();
		}
		return super.onOptionsItemSelected(item);
	}



	/**
	 * ��ʼ�������������������壩��
	 */

	private InitListener speechUnderstanderListener = new InitListener() {
		@Override
		public void onInit(int code) {
			Log.d(TAG, "speechUnderstanderListener init() code = " + code);
			if (code != ErrorCode.SUCCESS) {
				showTip("��ʼ��ʧ��,�����룺" + code);
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
						// ��ʾ
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
								AddcityList.this.seachweather(str4);
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
					} else {
						showTip("ʶ��������ȷ��");
					}
				}
			});
		}

		@Override
		public void onVolumeChanged(int v) {
			showTip("onVolumeChanged��" + v);
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
			showTip("onError Code��" + error.getErrorCode());
		}

		@Override
		public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
			// TODO Auto-generated method stub

		}
	};


	@Override
	protected void onDestroy() {
		super.onDestroy();
		// �˳�ʱ�ͷ�����
		mSpeechUnderstander.cancel();
		mSpeechUnderstander.destroy();
	}

	/**
	 * ��������
	 * 
	 * @param param
	 * @return
	 */
	public void setParam() {
		String lag = mSharedPreferences.getString(
				"understander_language_preference", "mandarin");
		if (lag.equals("en_us")) {
			// ��������
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// ��������
			mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// ������������
			mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, lag);
		}
		// ��������ǰ�˵�:������ʱʱ�䣬���û��೤ʱ�䲻˵��������ʱ����
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("understander_vadbos_preference",
						"4000"));

		// ����������˵�:��˵㾲�����ʱ�䣬���û�ֹͣ˵���೤ʱ���ڼ���Ϊ�������룬 �Զ�ֹͣ¼��
		mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("understander_vadeos_preference",
						"1000"));

		// ���ñ����ţ�Ĭ�ϣ�1���б�㣩
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("understander_punc_preference",
						"1"));

		// ������Ƶ����·����������Ƶ��ʽ��Ϊpcm������·��Ϊsd����ע��WRITE_EXTERNAL_STORAGEȨ��
		mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				Environment.getExternalStorageDirectory()
						+ "/iflytek/wavaudio.pcm");
	}

	// ��ʼ����ť��Ӧ�¼�
	private void init() {
		// TODO Auto-generated method stub
		city_text = (EditText) findViewById(R.id.city_text);
		search_btn = (Button) findViewById(R.id.search);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		speeh_btn = (Button) findViewById(R.id.speeh_btn);
		toolbar=(Toolbar) findViewById(R.id.maintoolbar);
		
		toolbar.setTitle("ѡ���ѯ����");
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
				DataBaseHelper dbhelper = new DataBaseHelper(AddcityList.this);
				SQLiteDatabase searchdb = dbhelper.getReadableDatabase();
				searchdb.execSQL("delete from seachedcity where isrecode=\"1\";");
				// searchdb.delete("seachedcity", "isrecode", new
				// String[]{"1"});
				// searchdb.execSQL("delete from seachedcity; select * from seachedcity;update seachedcity set seq=0 where name=seachedcity; ");
				searchdb.close();
			}
		});

		speeh_btn.setOnClickListener(new OnClickListener() {
			int ret = 0;// �������÷���ֵ

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setParam();
				if (mSpeechUnderstander.isUnderstanding()) {// ��ʼǰ���״̬
					mSpeechUnderstander.stopUnderstanding();
					showTip("ֹͣ¼��");
				} else {
					ret = mSpeechUnderstander
							.startUnderstanding(mRecognizerListener);
					if (ret != 0) {
						showTip("�������ʧ��,������:" + ret);
					} else {
						showTip(getString(R.string.text_begin));
					}
				}
			}
		});

		// hot_city_tab=(TableLayout) findViewById(R.id.tablegrid);
		// ����������ų��е���Ӧ�¼�
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
		intent.setClass(this, NewAlarm.class);
		startActivity(intent);
		finish();
	}
	
}
