package com.cn.jmantiLost.view;


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;

import com.cn.jmantiLost.R;

public class SelectPicPopupWindow extends PopupWindow {


	private Button btn_take_photo, btn_pick_photo, btn_cancel,btn_delete_device;
	private View mMenuView;

	public SelectPicPopupWindow(Activity context,OnClickListener itemsOnClick,int type) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.alert_dialog, null);
		btn_take_photo = (Button) mMenuView.findViewById(R.id.btn_take_photo);
		btn_pick_photo = (Button) mMenuView.findViewById(R.id.btn_pick_photo);
		btn_cancel = (Button) mMenuView.findViewById(R.id.btn_cancel);
		btn_delete_device = (Button)mMenuView.findViewById(R.id.btn_delete);
		btn_cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				dismiss();
			}
		});
		btn_delete_device.setOnClickListener(itemsOnClick);
		btn_pick_photo.setOnClickListener(itemsOnClick);
		btn_take_photo.setOnClickListener(itemsOnClick);
		if(type == 1){
			btn_pick_photo.setVisibility(View.GONE);
			btn_take_photo.setVisibility(View.GONE);
		}else{
			btn_delete_device.setVisibility(View.GONE);
		}
		this.setContentView(mMenuView);
		this.setWidth(LayoutParams.FILL_PARENT);
		this.setHeight(LayoutParams.WRAP_CONTENT);
		this.setFocusable(true);
		this.setAnimationStyle(R.style.AnimBottom);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		this.setBackgroundDrawable(dw);
		mMenuView.setOnTouchListener(new OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y=(int) event.getY();
				if(event.getAction()==MotionEvent.ACTION_UP){
					if(y<height){
						dismiss();
					}
				}				
				return true;
			}
		});

	}

}
