package com.halcyonwaves.apps.energize;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Iterator;
import java.util.List;

public class ApplicationCore extends Application {

	private static final String TAG = "ApplicationCore";

	public static boolean isServiceRunning(final Context ctx, final String serviceName) {
		Log.v(ApplicationCore.TAG, "Checking if the monitoring service is running or not...");
		boolean serviceRunning = false;
		final ActivityManager am = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
		final List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
		final Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
		while (i.hasNext()) {
			final ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

			if (runningServiceInfo.service.getClassName().equals(serviceName) && runningServiceInfo.started) {
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// set the default preferences
		PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);
		PreferenceManager.setDefaultValues(this, R.xml.pref_notifications, false);
		PreferenceManager.setDefaultValues(this, R.xml.pref_about, false);
	}
}
