package com.luka.giffero.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.luka.giffero.R;
import com.luka.giffero.engine.GifMaker;
import com.luka.giffero.engine.SettingManager;
import com.luka.giffero.engine.Utility;

import java.io.File;
import java.util.ArrayList;

public class PreviewActivity extends BaseActivity {

	public static ArrayList<String> arrCapturedPath = null;

	ImageView photoView = null;
	ImageView frameImageView = null;
	boolean isAnimationRunning = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		hideSystemUIPermanently();

		setContentView(R.layout.activity_preview);

		initUI();

		runPreview();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void finish() {
		isAnimationRunning = false;

		super.finish();
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SettingManager.REQUEST_FRAME_SELECTION) {
			if (resultCode == RESULT_OK) {
				Integer frameIndex = data.getIntExtra(FrameSelectionActivity.SelectedFrameID, 0);
				if (frameIndex > 0) {
					frameImageView.setBackgroundResource(frameIndex);
					frameImageView.setTag(frameIndex);
				}
			}
		}
	}

	public void OnBackBtnClicked(View view) {
		clearCash();
		finish();
	}

	public void OnSaveBtnClicked(View view) {
		GifMaker.sharedInstance().make(arrCapturedPath, (Integer) frameImageView.getTag(), null);

		finish();
	}

	public void OnFrameBtnClicked(View view) {
		Intent intent = new Intent(this, FrameSelectionActivity.class);
		startActivityForResult(intent, SettingManager.REQUEST_FRAME_SELECTION);
	}

	void initUI() {
		photoView = findViewById(R.id.photoView);

		Bitmap bitmap = Utility.bitmapFromFile(arrCapturedPath.get(0));
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(bitmap.getWidth(), bitmap.getHeight());
		photoView.setLayoutParams(layoutParams);
		photoView.requestLayout();

		frameImageView = findViewById(R.id.frameImageView);
		frameImageView.setTag(R.drawable.frame_0);
	}

	void runPreview() {
		/*
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int index = 0;
				while (isAnimationRunning) {
					if (arrCapturedPath == null || arrCapturedPath.size() <= 0)
						break;

					if (index >= arrCapturedPath.size())
						index = 0;

					final Bitmap bitmap = Utility.bitmapFromFile(arrCapturedPath.get(index));
					if (bitmap == null)
						continue;

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							photoView.setImageBitmap(bitmap);
						}
					});

					try {
						Thread.sleep(SettingManager.CAPTURE_INTERVAL);
//						Thread.sleep(50);
					} catch (Exception e) {
						e.printStackTrace();
					}

					index++;
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
		*/

		final AnimationDrawable animationDrawable = new AnimationDrawable();
		animationDrawable.setOneShot(false);
		for (String path : arrCapturedPath) {
			Bitmap bitmap = Utility.bitmapFromFile(path);
			animationDrawable.addFrame(new BitmapDrawable(bitmap), (int) SettingManager.CAPTURE_INTERVAL);
		}

		photoView.setBackgroundDrawable(animationDrawable);
		animationDrawable.start();
	}

	public void clearCash() {
		for (String path : arrCapturedPath) {
			new File(path).delete();
		}
	}
}
