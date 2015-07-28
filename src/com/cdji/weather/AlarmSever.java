package com.cdji.weather;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import com.cdji.weathertool.DataBaseHelper;
import com.cdji.weathertool.Nettool;
import com.cdji.weathertool.WeatherInfo;

public class AlarmSever extends BroadcastReceiver {

	private Bitmap icon;
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		WeatherInfo wInfo = (WeatherInfo) intent.getExtras().getSerializable("WeatherInfo");
		System.out.println("newalarmservices "+wInfo.getCityname()+wInfo.getDate()+wInfo.getPubdate());
		this.context=context;
		GetWeatherinfo getweatherinfo=new GetWeatherinfo();
		getweatherinfo.execute(wInfo.getCityname());
	}

public void shownotification(WeatherInfo wInfo){
	
	Intent intent=new Intent();
	
	Bundle bundle=new Bundle();
	bundle.putString("cityname", wInfo.getCityname());
	intent.putExtras(bundle);
	intent.setClass(context, Weather.class);
	System.out.println("intent city name "+wInfo.getCityname());
	PendingIntent pendingIntent=PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	
	NotificationManager mm=(NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
		Notification noti = new Notification.Builder(context)
        .setContentTitle(wInfo.getCityname())
        .setContentText(wInfo.getWeathinfo()+""+wInfo.getPresentemp()+"¡æ  "+wInfo.getDate())
        .setContentIntent(pendingIntent)
        .setSmallIcon(R.drawable.ic_launcher)
        .build();
		noti.flags = Notification.FLAG_AUTO_CANCEL;
		mm.notify(0, noti);
		
}	
	

public class GetWeatherinfo extends AsyncTask<String, String, WeatherInfo> {

	@Override
	protected WeatherInfo doInBackground(String... params) {
		// TODO Auto-generated method stub

		System.out.println("do inbackground "+params[0]);
		WeatherInfo resultInfo = Nettool.getInfo(params[0]);
		return resultInfo;
	}

	@Override
	protected void onPostExecute(WeatherInfo result) {
		// TODO Auto-generated method stub
		shownotification(result);
		super.onPostExecute(result);
	}
	

}


}

