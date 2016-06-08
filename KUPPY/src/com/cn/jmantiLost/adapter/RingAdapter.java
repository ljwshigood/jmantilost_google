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
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.application.AppContext;
import com.cn.jmantiLost.bean.SoundInfo;
import com.cn.jmantiLost.bean.alarmInfo;
import com.cn.jmantiLost.db.DatabaseManager;
import com.cn.jmantiLost.util.PlayMedia;

public class RingAdapter extends BaseAdapter implements OnItemClickListener {

	private LayoutInflater mInflator;
	private Context mContext;

	private DatabaseManager mDatabaseManger ;
	
	private String mAddress ;
	
	private ArrayList<alarmInfo> mAlarmList ;
	
	
	public RingAdapter(Context context, ArrayList<alarmInfo> list) {
		this.mContext = context;
		mInflator = LayoutInflater.from(context);
		mDatabaseManger = DatabaseManager.getInstance(mContext);
		this.mAlarmList = list ;
		//initData();
	}
	
	SoundInfo mSoundInfo ;
	private void initData(){
		ArrayList<SoundInfo> infoList = mDatabaseManger.selectSoundInfo(AppContext.mDeviceAddress);
		if(infoList.size() > 0 ){
			mSoundInfo = infoList.get(0);
		}
		for(int i = 0 ;i < mAlarmList.size();i++){
			alarmInfo info = mAlarmList.get(i);
			if(info.getRes() == mSoundInfo.getRingId()){
				info.setSelect(true);
			}else{
				info.setSelect(false);
			}
		}
	}

	@Override
	public int getCount() {
		return mAlarmList == null ? 0 : mAlarmList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		alarmInfo info = mAlarmList.get(position);
		if (convertView == null) {
			convertView = mInflator.inflate(R.layout.item_ring_set, null);
			viewHolder = new ViewHolder();
			viewHolder.mTvRing = (TextView) convertView
					.findViewById(R.id.tv_ring_set);
			viewHolder.mIvSelect = (ImageView)convertView.findViewById(R.id.iv_select);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if(info.isSelect()){
			viewHolder.mIvSelect.setVisibility(View.VISIBLE);
		}else{
			viewHolder.mIvSelect.setVisibility(View.GONE);
		}
		viewHolder.mTvRing.setText(mAlarmList.get(position).getName());
		return convertView;
	}

	class ViewHolder {
		ImageView mIvRingIcon;
		TextView mTvRing;
		ImageView mIvSelect ;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position,
			long arg3) {
		PlayMedia.getInstance(mContext).playMusicMedia(mAlarmList.get(position).getRes());
		for(int i= 0;i < mAlarmList.size();i++){
			if(i == position){
				mAlarmList.get(i).setSelect(true);
			}else{
				mAlarmList.get(i).setSelect(false);
			}
		}
		notifyDataSetChanged();
		mDatabaseManger.updateSoundInfoId(AppContext.mDeviceAddress, mAlarmList.get(position).getRes());
	}
	
}
