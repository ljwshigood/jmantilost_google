package com.cn.jmantiLost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.cn.jmantiLost.R;

public class WelcomeActivity extends BaseActivity {
	
	private AlphaAnimation start_anima;
	
	ImageView view;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		initView();
		initData();
	}
	
	private void initData() {
		start_anima = new AlphaAnimation(0.5f, 1.0f);
		start_anima.setDuration(500);
		view.startAnimation(start_anima);
		start_anima.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				redirectTo();
			}
		});
	}
	
	private void initView() {
		view = (ImageView)findViewById(R.id.iv_welcome);
	}

	private void redirectTo() {
		startActivity(new Intent(getApplicationContext(), MainActivity.class));
		finish();
	}
}
