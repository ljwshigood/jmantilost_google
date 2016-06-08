package com.cn.jmantiLost.adapter;


import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.bean.AppInfo;

public class AppAdapter extends BaseAdapter implements OnItemClickListener{

	private Context mContext;
	
	private ArrayList<AppInfo> dataList = new ArrayList<AppInfo>();
	
	private LayoutInflater mLayoutInflater ;
	
	private int mCount ;
	
	public ISelectApp mISelectApp ;
	
	public ISelectApp getmISelectApp() {
		return mISelectApp;
	}

	public void setmISelectApp(ISelectApp mISelectApp) {
		this.mISelectApp = mISelectApp;
	}

	public int getmCount() {
		return mCount;
	}

	public void setmCount(int mCount) {
		this.mCount = mCount;
	}

	public AppAdapter(Context context, ArrayList<AppInfo> inputDataList) {
		this.mContext = context;
		mLayoutInflater = LayoutInflater.from(context);
		this.dataList = inputDataList;
	}

	public void notifyAppDataSetChange(ArrayList<AppInfo> inputDataList){
		this.dataList = inputDataList;
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return dataList == null ? 0 : dataList.size();
	}

	@Override
	public Object getItem(int position) {
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null ;
		final AppInfo appUnit = dataList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder() ;
			convertView = mLayoutInflater.inflate(R.layout.item_select_app, null);
			viewHolder.tvAppName = (TextView) convertView.findViewById(R.id.tv_app_name);
			viewHolder.ivApp = (ImageView) convertView.findViewById(R.id.iv_select_app);
			viewHolder.mLLSelectApp = (LinearLayout)convertView.findViewById(R.id.ll_app_select);
			convertView.setTag(viewHolder);
		}else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.tvAppName.setText(appUnit.appName);
		viewHolder.ivApp.setImageDrawable(appUnit.appIcon); 
		
		return convertView;
	}
	
	class ViewHolder{
		LinearLayout mLLSelectApp ;
		TextView tvAppName;
		ImageView ivApp ; 
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		for(int i = 0 ;i < dataList.size() ;i++){
			if(position == i){
				dataList.get(i).setSelect(true);
				mISelectApp.selectApp(dataList.get(i));
			}else{
				dataList.get(i).setSelect(false);
			}
		}
		
		notifyDataSetChanged();
	}
	
	public interface ISelectApp{
		public void selectApp(AppInfo appUnit);
	}
	
}
