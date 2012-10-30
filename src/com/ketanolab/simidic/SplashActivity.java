package com.ketanolab.simidic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_splash);

		RelativeLayout layout = (RelativeLayout) findViewById(R.id.layoutSplash);
		Animation animationnLayout = AnimationUtils.loadAnimation(this, R.anim.anim_layout);

		animationnLayout.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {

			}

			public void onAnimationRepeat(Animation animation) {

			}

			public void onAnimationEnd(Animation animation) {
				Intent intent = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(intent);
				SplashActivity.this.finish();
			}
		});

		layout.startAnimation(animationnLayout);
	}

}
