package com.luka.giffero.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

public class BaseActivity extends Activity {
	public void showErrorMessage(String sMsg) {
		new AlertDialog.Builder(this)
				.setTitle("Error")
				.setMessage(sMsg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	public void showMessage(String sTitle, String sMsg) {
		new AlertDialog.Builder(this)
				.setTitle(sTitle)
				.setMessage(sMsg)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
	}

	public void showToast(int messageID) {
		Toast.makeText(this, getResources().getString(messageID), Toast.LENGTH_SHORT).show();
	}

	public void showToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}

	public void showErrorToast(int messageID) {
		Toast.makeText(this, getResources().getString(messageID), Toast.LENGTH_LONG).show();
	}

	public void showErrorToast(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	ProgressDialog progressDialog = null;
	public void showProgress() {
		hideProgress();

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("One moment please...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void showProgress(String strMessage) {
		if (progressDialog != null) {
			progressDialog.setMessage(strMessage);
			return;
		}

		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(strMessage);
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
	}

	public void hideProgress() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void hideSoftKeyboard() {
		if(getCurrentFocus()!=null) {
			InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
		}
	}

	public void showSoftKeyboard(View view) {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		view.requestFocus();
		inputMethodManager.showSoftInput(view, 0);
	}

	private boolean isImmersiveAvailable() {
		return android.os.Build.VERSION.SDK_INT >= 19;
	}

	// This snippet hides the system bars.
	void hideSystemUI() {
		if (!isImmersiveAvailable())
			return;

		// Set the IMMERSIVE flag.
		// Set the content to appear under the system bars so that the content
		// doesn't resize when the system bars hide and show.
		this.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
						| View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
						| View.SYSTEM_UI_FLAG_IMMERSIVE);
	}

	// This snippet shows the system bars. It does this by removing all the flags
	// except for the ones that make the content appear under the system bars.
	void showSystemUI() {
		if (!isImmersiveAvailable())
			return;

		this.getWindow().getDecorView().setSystemUiVisibility(
				View.SYSTEM_UI_FLAG_LAYOUT_STABLE
						| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
	}

	void hideSystemUIPermanently() {
		hideSystemUI();

		getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
			@Override
			public void onSystemUiVisibilityChange(int visibility) {
				hideSystemUI();
			}
		});
	}
}
