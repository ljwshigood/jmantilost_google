package com.cn.jmantiLost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.jmantiLost.R;

public class FunctionAdapter extends BaseAdapter {

	private Context mContext;

	private LayoutInflater mLayoutInflater;

	private int[] mRes;

	private String[] info;

	public FunctionAdapter(Context context,int[] res, String[] info) {
		mLayoutInflater = LayoutInflater.from(context);
		this.mRes = res ;
		this.info = info ;
	}

	@Override
	public int getCount() {
		return mRes == null ? 0 : mRes.length;
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

		ViewHolder viewHolder = null;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.item_gv_function,
					null);
			viewHolder.mIvIcon = (ImageView) convertView
					.findViewById(R.id.iv_function);
			viewHolder.mTvInfo = (TextView) convertView
					.findViewById(R.id.tv_function);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.mIvIcon.setImageResource(mRes[position]);
		viewHolder.mTvInfo.setText(info[position]);

		return convertView;
	}

	class ViewHolder {
		TextView mTvInfo;
		ImageView mIvIcon;
	}
}
