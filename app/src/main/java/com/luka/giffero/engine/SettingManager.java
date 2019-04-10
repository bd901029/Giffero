package com.luka.giffero.engine;

import android.content.Context;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;
import com.luka.giffero.GifferoApplication;

public class SettingManager {
	private static SettingManager instance = null;
	public static SettingManager sharedInstance() {
		if (instance == null)
			instance = new SettingManager();
		return instance;
	}

	public static final int REQUEST_PERMISSION = 1;
	public static final int REQUEST_FRAME_SELECTION = 2;

	public static long CAPTURE_INTERVAL = 100;
	public static int FRAME_COUNT = 16;
	public static int FREEZE_SECONDS = 3;

	SharedPreferences preferences = null;

	SettingManager() {
		Context context = GifferoApplication.sharedInstance();
		preferences = context.getSharedPreferences(context.getPackageName(), MODE_PRIVATE);
	}

	public boolean isFirstRun() {
		if (preferences.getBoolean("firstrun", true)) {
			preferences.edit().putBoolean("firstrun", false).commit();
			return true;
		}

		return false;
	}
}