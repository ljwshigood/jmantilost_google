package com.cn.jmantiLost.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.jmantiLost.R;
import com.cn.jmantiLost.activity.FunctionDetailActivity;
import com.cn.jmantiLost.activity.MainActivity;
import com.cn.jmantiLost.bean.KeySetBean;
import com.cn.jmantiLost.util.AppManager;

public class KeySetAdapter extends BaseAdapter implements OnItemClickListener{

	private Context mContext;

	private List<KeySetBean> mKeySetList;

	private LayoutInflater mLayoutInflater;

	public KeySetAdapter(Context context, List<KeySetBean> list) {
		this.mContext = context;
		this.mKeySetList = list;
		this.mLayoutInflater = LayoutInflater.from(context);
	}
	
	public void notifyDataSetKey(List<KeySetBean> list){
		this.mKeySetList = list ;
		notifyDataSetChanged();
		
	}
	
	public void notifyKeyDataSetChange(List<KeySetBean> list){
		this.mKeySetList = list;
		notifyDataSetChanged() ;
	}

	private boolean mIsConnected = false ; 
	
	public void notifyKeySetStatusChange(List<KeySetBean> list,boolean isConnnected){
		mIsConnected = isConnnected ;
		this.mKeySetList = list ;
		notifyDataSetChanged();
	}
	
	
	
	@Override
	public int getCount() {
		return mKeySetList == null ? 0 : mKeySetList.size();
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
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.item_key_set,parent, false);
			viewHolder = new ViewHolder();
			viewHolder.mIvImage = (ImageView) convertView.findViewById(R.id.iv_set_pic);
			viewHolder.mTvKeySetMain = (TextView) convertView.findViewById(R.id.tv_key_main);
			viewHolder.mTvKeySetSub = (TextView) convertView.findViewById(R.id.tv_key_sub);
			viewHolder.mViewDot = (View)convertView.findViewById(R.id.include_dot);
			viewHolder.mTvSubTitle = (TextView)convertView.findViewById(R.id.tv_key_sub);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final KeySetBean bean = mKeySetList.get(position);
		
		switch (bean.getCount()) {
		case 1:
			viewHolder.mTvKeySetSub.setVisibility(View.VISIBLE);
			viewHolder.mIvImage.setImageResource(R.drawable.ic_antilost_small_nomal);
			viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
			
			if(mIsConnected == true){
				viewHolder.mTvKeySetSub.setText(mContext.getString(R.string.connected_device));
			}else{
				viewHolder.mTvKeySetSub.setText(mContext.getString(R.string.un_connect_device));
			}
			
			viewHolder.mViewDot.findViewById(R.id.iv_dot_one).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_two).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_three).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_four).setVisibility(View.VISIBLE);
			break;
		case 2:
			viewHolder.mTvKeySetSub.setVisibility(View.GONE);
			if(bean.getKeySetDetail() == null || bean.getKeySetDetail().equals("null")){
				viewHolder.mTvKeySetMain.setText(mContext.getString(R.string.key_set_value));
				viewHolder.mIvImage.setImageResource(R.drawable.ic_add_key_set);
			}else if(bean.getType() == 1){
				if(bean.getBitmapString() != null && !bean.getBitmapString().equals("null")){
					viewHolder.mIvImage.setImageDrawable(AppManager.getProgramBitmapByPackageName(mContext, bean.getBitmapString()));
					viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
				}
			
			}else if(bean.getType() == 0){
				viewHolder.mIvImage.setImageResource(Integer.valueOf(bean.getBitmapString()));
				viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
			}
			viewHolder.mViewDot.findViewById(R.id.iv_dot_one).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_two).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_three).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_four).setVisibility(View.VISIBLE);
			break;
		case 3:
			viewHolder.mTvKeySetSub.setVisibility(View.GONE);
			if(bean.getKeySetDetail() == null || bean.getKeySetDetail().equals("null")){
				viewHolder.mTvKeySetMain.setText(mContext.getString(R.string.key_set_value));
				viewHolder.mIvImage.setImageResource(R.drawable.ic_add_key_set);
			}else if(bean.getType() == 1){
				if(bean.getBitmapString() != null && !bean.getBitmapString().equals("null")){
					viewHolder.mIvImage.setImageDrawable(AppManager.getProgramBitmapByPackageName(mContext, bean.getBitmapString()));
					viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
				}
			
			}else if(bean.getType() == 0) {
				viewHolder.mIvImage.setImageResource(Integer.valueOf(bean.getBitmapString()));
				viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
			}
			viewHolder.mViewDot.findViewById(R.id.iv_dot_one).setVisibility(View.GONE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_two).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_three).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_four).setVisibility(View.VISIBLE);
			break;
		case 4:
			viewHolder.mTvKeySetSub.setVisibility(View.GONE);
			if(bean.getKeySetDetail() == null || bean.getKeySetDetail().equals("null")){
				viewHolder.mTvKeySetMain.setText(mContext.getString(R.string.key_set_value));
				viewHolder.mIvImage.setImageResource(R.drawable.ic_add_key_set);
			}else if(bean.getType() == 1){
				if(bean.getBitmapString() != null && !bean.getBitmapString().equals("null")){
					viewHolder.mIvImage.setImageDrawable(AppManager.getProgramBitmapByPackageName(mContext, bean.getBitmapString()));
					viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
				}
			
			}else if(bean.getType() == 0) {
				viewHolder.mIvImage.setImageResource(Integer.valueOf(bean.getBitmapString()));
				viewHolder.mTvKeySetMain.setText(bean.getKeySetDetail());
			}
			viewHolder.mViewDot.findViewById(R.id.iv_dot_one).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_two).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_three).setVisibility(View.VISIBLE);
			viewHolder.mViewDot.findViewById(R.id.iv_dot_four).setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

		return convertView;
	}
	
	private IKeyRemove mIKeyRemove ;
	

	public IKeyRemove getmIKeyRemove() {
		return mIKeyRemove;
	}

	public void setmIKeyRemove(IKeyRemove mIKeyRemove) {
		this.mIKeyRemove = mIKeyRemove;
	}

	public interface IKeyRemove{
		
		public void remove(int count);
	}

	class ViewHolder {
		LinearLayout mLLFront ;
		Button mBtnDelete ;
		View hideView ;
		View showView ;
		ImageView mIvImage;
		TextView mTvKeySetMain;
		TextView mTvKeySetSub;
		ImageView mIvCount;
		TextView mTvSubTitle ;
		View mViewDot ;
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		if(position == 0){
			Intent intent = new Intent(mContext, MainActivity.class);
			intent.putExtra("count", position + 1);
			mContext.startActivity(intent);
		}else{
			Intent intent = new Intent(mContext, FunctionDetailActivity.class);
			intent.putExtra("count", position + 1);
			((Activity) mContext).startActivityForResult(intent,1212);
		}
	}

}
