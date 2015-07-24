package com.cdji.weathertool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioButton;

public class Pageguider {
	List<RadioButton> list=new ArrayList<RadioButton>();
	private int num;
	private int index;
	private Context context;
	private LinearLayout linearLayout;
	private RadioButton  radioButton;
	
	public Pageguider(Context context){
		this.context=context;
	}
	public void setLinearlayout(LinearLayout linearLayout)
	{
		this.linearLayout=linearLayout;
	}
	public void  setcount(int num) {
		this.num=num;
		for (int i=0;i<num;i++)
		{
		radioButton=new RadioButton(context);
		radioButton.setChecked(false);
		linearLayout.addView(radioButton);
		list.add(radioButton);
		}
	}
	public void setcusor(int index){
		radioButton=list.get(this.index);
		radioButton.setChecked(false);
		radioButton=list.get(index);
		radioButton.setChecked(true);
		this.index=index;
	}
		
}
