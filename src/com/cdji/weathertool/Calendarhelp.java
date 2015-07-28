package com.cdji.weathertool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Calendarhelp {


		/**
		 * ��ʽ��ʱ��
		 * @param time
		 * @return
		 */
		public static String formatDateTime(String time) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
			if(time==null ||"".equals(time)){
				return "";
			}
			Date date = null;
			try {
				date = format.parse(time+" 12:00");
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			Calendar current = Calendar.getInstance();
			Calendar aftertorrom=Calendar.getInstance();
			Calendar tomrrow=Calendar.getInstance();
			Calendar today = Calendar.getInstance();	//����
			
			today.set(Calendar.YEAR, current.get(Calendar.YEAR));
			today.set(Calendar.MONTH, current.get(Calendar.MONTH));
			today.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH));
			//  Calendar.HOUR����12Сʱ�Ƶ�Сʱ�� Calendar.HOUR_OF_DAY����24Сʱ�Ƶ�Сʱ��
			today.set( Calendar.HOUR_OF_DAY, 0);
			today.set( Calendar.MINUTE, 0);
			today.set(Calendar.SECOND, 0);
			
			Calendar yesterday = Calendar.getInstance();	//����
			
			yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
			yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
			yesterday.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)-1);
			yesterday.set( Calendar.HOUR_OF_DAY, 0);
			yesterday.set( Calendar.MINUTE, 0);
			yesterday.set(Calendar.SECOND, 0);
			
			tomrrow.set(Calendar.YEAR, current.get(Calendar.YEAR));
			tomrrow.set(Calendar.MONTH, current.get(Calendar.MONTH));
			tomrrow.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)+1);
			tomrrow.set( Calendar.HOUR_OF_DAY, 0);
			tomrrow.set( Calendar.MINUTE, 0);
			tomrrow.set(Calendar.SECOND, 0);
			aftertorrom.set(Calendar.YEAR, current.get(Calendar.YEAR));
			aftertorrom.set(Calendar.MONTH, current.get(Calendar.MONTH));
			aftertorrom.set(Calendar.DAY_OF_MONTH,current.get(Calendar.DAY_OF_MONTH)+2);
			aftertorrom.set( Calendar.HOUR_OF_DAY, 0);
			aftertorrom.set( Calendar.MINUTE, 0);
			aftertorrom.set(Calendar.SECOND, 0);
			
			current.setTime(date);
			
			if(current.after(today)&&current.before(tomrrow)){
				return "���� ";
			}else if(current.before(today) && current.after(yesterday)){
				
				return "���� ";
			}else if(current.after(tomrrow)&&current.before(aftertorrom)){
				
				return "����";
			}else {
				return time;
			}
		}

	}
