package com.luka.giffero.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.luka.giffero.GifferoApplication;
import com.luka.giffero.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class GifMaker {
	private static GifMaker instance = null;
	public static GifMaker sharedInstance() {
		if (instance == null)
			instance = new GifMaker();
		return instance;
	}

	public Callback callback = null;
	public interface Callback {
		void OnSuccess();
		void OnFailure(String message);
	}

	public void runOnSuccess() {
		Context context = GifferoApplication.sharedInstance();
		Handler handler = new Handler(context.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (callback != null)
					callback.OnSuccess();
			}
		});
	}

	public void runOnFailure(final String message) {
		Context context = GifferoApplication.sharedInstance();
		Handler handler = new Handler(context.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (callback != null) {
					callback.OnFailure(message);
				}
			}
		});
	}

	private String tempDirPath = null, realDirPath = null;

	GifMaker() {
		String strAppName = GifferoApplication.sharedInstance().getResources().getString(R.string.app_name);
		tempDirPath = GifferoApplication.sharedInstance().getExternalFilesDir(null).getPath();
		realDirPath = Environment.getExternalStorageDirectory() + File.separator + strAppName;
		Log.v("GifMaker", realDirPath);

		File directory = new File(realDirPath);
		directory.mkdirs();
	}

	public void clearCash(ArrayList<String> arrPath) {
		for (String path : arrPath) {
			new File(path).delete();
		}
	}

	public boolean make(ArrayList<String> arrPath, int frameID) {
		Context context = GifferoApplication.sharedInstance();
		Bitmap frameBitmap = BitmapFactory.decodeResource(context.getResources(), frameID);
		try {
			String fileName = Utility.newFileName() + ".gif";
			AnimatedGIFWriter writer = new AnimatedGIFWriter(false);
			File tempFile = new File(tempDirPath, fileName);
			OutputStream os = new FileOutputStream(tempFile);
			writer.prepareForWrite(os, -1, -1);
			for (String path : arrPath) {
				Bitmap frame = Utility.bitmapFromFile(path);
				if (frame == null)
					continue;

				Bitmap overlayBitmap = Utility.overlayBitmap(frame, frameBitmap);
				if (overlayBitmap == null)
					continue;

				writer.writeFrame(os, overlayBitmap, (int) SettingManager.CAPTURE_INTERVAL);

				Log.v("GifMaker", String.format("Frame has been saved: %d", arrPath.indexOf(path)));
			}
			writer.finishWrite(os);

			File realFile = new File(realDirPath, fileName);
			Utility.copy(tempFile, realFile);

			tempFile.delete();
			clearCash(arrPath);

			frameBitmap.recycle();

			Log.v("GifMaker", String.format("GIF has been created: %s", fileName));

			showToast(R.string.gif_saved);

			return true;
		} catch (Exception e) {
			e.printStackTrace();

			showToast(R.string.gif_not_saved);
		}

		return false;
	}

	public void make(final ArrayList<String> arrPath, int frameID, Callback callback) {
		this.callback = callback;

		new MakeGifAsyncTask().execute(arrPath, frameID);
	}

	private void showToast(final String text) {
		final Context context = GifferoApplication.sharedInstance();
		Handler handler = new Handler(context.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showToast(final int messageID) {
		final Context context = GifferoApplication.sharedInstance();
		showToast(context.getResources().getString(messageID));
	}

	private class MakeGifAsyncTask extends AsyncTask {
		@Override
		protected void onPreExecute() {}

		@Override
		protected Object doInBackground(Object[] params) {
			if (make((ArrayList<String>) params[0], (Integer)params[1])) {
				runOnSuccess();
			} else {
				runOnFailure(GifferoApplication.sharedInstance().getResources().getString(R.string.gif_not_saved));
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object result) {
		}
	}
}
