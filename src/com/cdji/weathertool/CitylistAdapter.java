package com.cdji.weathertool;

import java.util.ArrayList;
import java.util.List;

import com.iflytek.cloud.InitListener;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CitylistAdapter<T> extends BaseAdapter {
	private List<T> list;
	private Context context;
	
	public CitylistAdapter(Context context,List<T> list)
	{
		this.context=context;
		if(list==null)
		{
			list=new ArrayList<T>();
		}
		this.list=list;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		T item=(T) getItem(position);		
		View view=init(context,position,item);
		return view;
	}
	public abstract View init(Context context, int position, T item);
}
