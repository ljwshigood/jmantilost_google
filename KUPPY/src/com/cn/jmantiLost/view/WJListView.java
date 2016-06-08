package com.cn.jmantiLost.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class WJListView extends ListView {

	public WJListView(Context context) {
		super(context);
	}

	public WJListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public WJListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);  
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	
	
	
}
