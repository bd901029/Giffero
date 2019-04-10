package com.luka.giffero.engine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;

import com.luka.giffero.GifferoApplication;
import com.luka.giffero.R;
import com.luka.giffero.ui.MainActivity;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utility {
	private static Utility instance = null;
	public static Utility SharedInstance() {
		if (instance == null)
			instance = new Utility();
		return instance;
	}

	Utility() {
	}

	public static boolean SDK_SUPPORTED = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;

	public static int RESULT_LOAD_IMAGE = 0x1029;

	/*
	public static boolean isDataConnectionAvailable() {
		Context context = GifferoApplication.sharedInstance();
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null)
			return false;
		return connectivityManager.getActiveNetworkInfo().isConnected();
	}
	*/

	public static void setLanguage(String localeString) {
		Locale locale = new Locale(localeString);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;

		Context context = GifferoApplication.sharedInstance();
		context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());
	}

	public static byte[] convertDrawableToData(BitmapDrawable drawable) {
		if (drawable == null)
			return null;

		try {
			Bitmap bitmap = drawable.getBitmap();
			Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, 300, 300, false);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmapResized.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			return bos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Bitmap convertByteToBitmap(byte[] bytes) {
		if (bytes == null || bytes.length <= 0)
			return null;

		Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
		return bitmap;
	}

	public static String convertDateToText(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dateFormat.format(date);
	}

	public static Date convertTextToDate(String strDate) {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return dateFormat.parse(strDate);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public static void requestStoragePermission(final Activity activity) {
		if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
			// Provide an additional rationale to the user if the permission was not granted
			// and the user would benefit from additional context for the use of the permission.
			// For example if the user has previously denied the permission.
			new AlertDialog.Builder(activity)
					.setTitle("Permission Request")
					.setMessage("Requesting Permission to Access Internet")
					.setCancelable(false)
					.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//re-request
							ActivityCompat.requestPermissions(activity,
									new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
									0);
						}
					})
					.setIcon(R.drawable.ic_action_info)
					.show();
		} else {
			// READ_PHONE_STATE permission has not been granted yet. Request it directly.
			ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
		}
	}

	public static void writeByteToFile(byte[] bytes, String filePath) {
		try {
			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(bytes);
			bos.flush();
			bos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeBitmapToFile(Bitmap bitmap, String filePath) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		byte[] bytes = stream.toByteArray();
		FileOutputStream output = null;
		try {
			File file = new File(filePath);
			output = new FileOutputStream(file);
			output.write(bytes);

			Log.v(MainActivity.class.toString(), file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != output) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static Bitmap bitmapFromFile(String picturePath) {
		if (picturePath == null || picturePath.equals(""))
			return null;

		return BitmapFactory.decodeFile(picturePath);
	}

	/*
	public static String getUniqueIMEIId() {
		Context context = SellioApplication.sharedInstance();
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
				return "";
			}

			String imei = telephonyManager.getDeviceId();
			if (imei != null && !imei.isEmpty()) {
				return imei;
			} else {
				return Build.SERIAL;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "not_found";
	}
	*/

	public static String newFileName() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String strDate = dateFormat.format(date) + String.valueOf(date.getTime());
		return strDate;
	}

	public static String newFileName(String extension) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
		Date date = new Date();
		String strDate = dateFormat.format(date) + String.valueOf(date.getTime());
		return strDate + extension;
	}

	public static Bitmap bitmapFromView(View view) {
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
		view.draw(c);
		return bitmap;
	}

	public static Bitmap overlayBitmap(Bitmap bmp1, Bitmap bmp2) {
		Bitmap bmOverlay = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
		Canvas canvas = new Canvas(bmOverlay);
		canvas.drawBitmap(bmp1, new Matrix(), null);
		canvas.drawBitmap(resizeBitmap(bmp2, bmp1.getWidth(), bmp1.getHeight()), new Matrix(), null);
		return bmOverlay;
	}

	public static Bitmap resizeBitmap(Bitmap bitmap, float width, float height) {
		Bitmap background = Bitmap.createBitmap((int)width, (int)height, Bitmap.Config.ARGB_8888);

		float originalWidth = bitmap.getWidth();
		float originalHeight = bitmap.getHeight();

		Canvas canvas = new Canvas(background);

		float scaleX = width / originalWidth;
		float scaleY = height / originalHeight;

		float xTranslation = 0.0f;
		float yTranslation = (height - originalHeight * scaleY) / 2.0f;

		Matrix transformation = new Matrix();
		transformation.postTranslate(xTranslation, yTranslation);
		transformation.preScale(scaleX, scaleY);

		Paint paint = new Paint();
		paint.setFilterBitmap(true);

		canvas.drawBitmap(bitmap, transformation, paint);

		return background;
	}

	public static void copy(String srcPath, String dstPath) throws IOException {
		copy(new File(srcPath), new File(dstPath));
	}

	public static void copy(File src, File dst) throws IOException {
		FileInputStream inStream = new FileInputStream(src);
		FileOutputStream outStream = new FileOutputStream(dst);
		FileChannel inChannel = inStream.getChannel();
		FileChannel outChannel = outStream.getChannel();
		inChannel.transferTo(0, inChannel.size(), outChannel);
		inStream.close();
		outStream.close();
	}
}
