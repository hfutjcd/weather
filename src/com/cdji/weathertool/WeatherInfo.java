package com.cdji.weathertool;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.R.bool;
import android.net.ParseException;

public class WeatherInfo implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//	private Date date;
	private String date,pubdate;
	
	private String cityname, weathinfo, windirection, windforce;
	private String presentemp, lowesttemp, heightesttemp;
	private String id_city;
	private String isrecode;
	private String isalarm;
	private String issubscribe;
	

	public String getIssubscribe() {
		return issubscribe;
	}

	public void setIssubscribe(String issubscribe) {
		this.issubscribe = issubscribe;
	}

	public String getIsrecode() {
		return isrecode;
	}

	public void setIsrecode(String isrecode) {
		this.isrecode = isrecode;
	}

	public String getIsalarm() {
		return isalarm;
	}

	public void setIsalarm(String isalarm) {
		this.isalarm = isalarm;
	}

	public String getId_city() {
		return id_city;
	}

	public void setId_city(String id_city) {
		this.id_city = id_city;
	}

	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}



	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getPubdate() {
		return pubdate;
	}

	public void setPubdate(String pubdate) {
		this.pubdate = pubdate;
	}

	public String getWeathinfo() {
		return weathinfo;
	}

	public void setWeathinfo(String weathinfo) {
		this.weathinfo = weathinfo;
	}

	public String getWindirection() {
		return windirection;
	}

	public void setWindirection(String windirection) {
		this.windirection = windirection;
	}

	public String getWindforce() {
		return windforce;
	}

	public void setWindforce(String windforce) {
		this.windforce = windforce;
	}

	public String getPresentemp() {
		return presentemp;
	}

	public void setPresentemp(String d) {
		this.presentemp = d;
	}

	public String getLowesttemp() {
		return lowesttemp;
	}

	public void setLowesttemp(String lowesttemp) {
		this.lowesttemp = lowesttemp;
	}

	public String getHeightesttemp() {
		return heightesttemp;
	}

	public void setHeightesttemp(String heightesttemp) {
		this.heightesttemp = heightesttemp;
	}

	public String getDateString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss ");
		if (date != null) {
			return formatter.format(date);
		} else {
			return null;
		}

	}
	
	public String getweek() {
		// TODO Auto-generated method stub
		final String dayNames[] = { "星期日", "星期一", "星期二", "星期三", "星期四", "星期五",
		"星期六" };
		String s = "20"+date+" "+pubdate;
		SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Calendar calendar = Calendar.getInstance();
		Date date = new Date();
		try {
		date = sdfInput.parse(s);
		} catch (ParseException e) {
		e.printStackTrace();
		} catch (java.text.ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		calendar.setTime(date);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)-1;
		if(dayOfWeek<0)dayOfWeek=0;
		return dayNames[dayOfWeek];
	}
	

}
