package com.luka.giffero;

import android.app.Application;

public class GifferoApplication extends Application {
	static GifferoApplication sharedInstance = null;
	public static GifferoApplication sharedInstance() {
		return sharedInstance;
	}

	@Override
	public void onCreate() {
		sharedInstance = this;

		super.onCreate();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}
}
