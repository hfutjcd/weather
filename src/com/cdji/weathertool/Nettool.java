package com.cdji.weathertool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

public class Nettool {
//	private static String httpUrl="http://apis.baidu.com/apistore/weatherservice/weather?citypinyin=beijing";//"http://api.baidu.com/apistore/weatherservice/weather";
	private static String httpUrl="http://apis.baidu.com/apistore/weatherservice/cityname";
	private static String httpArg="cityname=";
	public static WeatherInfo getInfo(String name) {
		WeatherInfo weatherinfo=null;
		BufferedReader reader=null;
		String result=null;
		StringBuffer sbf=new StringBuffer();
		String Url=null;
		
			System.out.println("-1");			
			try {
				Url=httpUrl+"?"+httpArg+URLEncoder.encode(name, "utf-8");
				URL url=new URL(Url);
				System.out.println(url);
				HttpURLConnection connection=(HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
				connection.setRequestProperty("apikey", "cd63fe7470d589fe168cc08d79ae7374");
				connection.setConnectTimeout(5000);
				connection.connect();
				InputStream is=connection.getInputStream();
				reader=new BufferedReader(new InputStreamReader(is,"UTF-8"));
				String strRead=null;
				while((strRead=reader.readLine())!=null)
				{
					sbf.append(strRead);
					sbf.append("\r\n");
				}
				reader.close();
				result=sbf.toString();
				JSONObject json=new JSONObject(result);
				System.out.println(json.getString("errMsg"));
				if(json.get("errMsg").equals("success"))
				{
					weatherinfo=new WeatherInfo();
					String str=json.getString("retData");
					System.out.println(str);
					JSONObject json1=new JSONObject(str);
					weatherinfo.setCityname(json1.getString("city"));
					weatherinfo.setDate(json1.getString("date"));
					weatherinfo.setPubdate(json1.getString("time"));
					weatherinfo.setWeathinfo(json1.getString("weather"));
					weatherinfo.setId_city(json1.getString("citycode"));
					weatherinfo.setPresentemp(json1.getString("temp"));
					weatherinfo.setHeightesttemp(json1.getString("h_tmp"));
					weatherinfo.setLowesttemp(json1.getString("l_tmp"));
					weatherinfo.setWindirection(json1.getString("WD"));
					weatherinfo.setWindforce(json1.getString("WS"));	
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
		return weatherinfo;
		
	}
	
}
