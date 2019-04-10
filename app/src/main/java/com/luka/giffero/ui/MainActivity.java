package com.luka.giffero.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.luka.giffero.R;
import com.luka.giffero.engine.ImageCapturer;
import com.luka.giffero.engine.SettingManager;
import com.luka.giffero.engine.Utility;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends BaseFragmentActivity {

	View flashView = null;
	RelativeLayout countDownContainer = null;
	TextView countDownTextView = null;
	ImageButton btnTakePhoto = null;

	RelativeLayout textureContainer = null;
	AutoFitTextureView textureView = null;

	RelativeLayout headerContainer = null;
	RelativeLayout controlContainer = null;

	ArrayList<String> arrCapturedPath = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		hideSystemUIPermanently();

		setContentView(R.layout.activity_main);

		initUI();

		ImageCapturer.sharedInstance().init(this, textureView);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		if (requestCode == SettingManager.REQUEST_PERMISSION) {
			for (Integer grantResult : grantResults) {
				if (grantResult != PackageManager.PERMISSION_GRANTED) {
					showErrorToast(R.string.request_permission);
					finish();
					break;
				}
			}
		} else {
			super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		ImageCapturer.sharedInstance().resume();
	}

	@Override
	protected void onPause() {
		ImageCapturer.sharedInstance().pause();

		super.onPause();
	}

	public void OnTakePhotoBtnClicked(View view) {
		btnTakePhoto.setEnabled(false);

		countDownAnimation();
	}

	void initUI() {
		textureContainer = findViewById(R.id.textureContainer);
		textureView = findViewById(R.id.texture);

		flashView = findViewById(R.id.flashView);
		countDownContainer = findViewById(R.id.countDownContainer);
		countDownTextView = findViewById(R.id.countDownTextView);
		btnTakePhoto = findViewById(R.id.btnTakePhoto);

		headerContainer = findViewById(R.id.header);
		controlContainer = findViewById(R.id.control);
	}

	private void countDownAnimation() {
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				int index = SettingManager.FREEZE_SECONDS;
				while (index >= 0) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}

					final int finalIndex = index;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							if (finalIndex > 0) {
								countDownContainer.setVisibility(View.VISIBLE);
								countDownTextView.setText(String.valueOf(finalIndex));
							} else {
								countDownContainer.setVisibility(View.INVISIBLE);
								startCapture();
							}
						}
					});

					index--;
				}
			}
		};

		Thread thread = new Thread(runnable);
		thread.start();
	}

	private void flashAnimation() {
		final long duration = SettingManager.CAPTURE_INTERVAL / 4;

		flashView.setVisibility(View.VISIBLE);

		ObjectAnimator fadeOut = ObjectAnimator.ofFloat(flashView, "alpha",  0f, 1f);
		fadeOut.setDuration(duration);
		ObjectAnimator fadeIn = ObjectAnimator.ofFloat(flashView, "alpha", 1f, 0f);
		fadeIn.setDuration(duration);

		AnimatorSet animationSet = new AnimatorSet();
		animationSet.play(fadeIn).after(fadeOut);
		animationSet.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				super.onAnimationEnd(animation);

				flashView.setVisibility(View.GONE);
			}
		});
		animationSet.start();
	}

	private void startCapture() {
		arrCapturedPath = new ArrayList<>();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 0; i < SettingManager.FRAME_COUNT; i++) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							flashAnimation();

							capturePhoto();
						}
					});

					try {
						Thread.sleep(SettingManager.CAPTURE_INTERVAL);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		thread.start();
	}

	private void capturePhoto() {
		Bitmap bitmap = textureView.getBitmap();
		bitmap = Bitmap.createBitmap(bitmap, 0, headerContainer.getHeight(), bitmap.getWidth(), bitmap.getHeight()-headerContainer.getHeight()-controlContainer.getHeight());;

		String fileName = Utility.newFileName() + String.format("_%d.jpg", arrCapturedPath.size());
		final String filePath = getExternalFilesDir(null).getPath() + File.separator + fileName;

		ImageCapturer.sharedInstance().writeBitmapToFile(bitmap, filePath, new ImageCapturer.Callback() {
			@Override
			public void OnSuccess() {
				arrCapturedPath.add(filePath);
				if (arrCapturedPath.size() >= SettingManager.FRAME_COUNT) {
					Log.v(MainActivity.class.toString(), String.format("%d photos have been captured", arrCapturedPath.size()));

					PreviewActivity.arrCapturedPath = arrCapturedPath;
					Intent intent = new Intent(MainActivity.this, PreviewActivity.class);
					startActivity(intent);

					btnTakePhoto.setEnabled(true);
				}
			}

			@Override
			public void OnFailure(String message) {

			}
		});
	}
}
