
package com.halcyonwaves.apps.energize;

import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationCore extends Application {

	private static final String TAG = "ApplicationCore";

	public static int getSelectedThemeId( final Context context ) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		final String d = prefs.getString( "display.theme", "UnknownTheme" );
		if( d.compareTo( "DarkTheme" ) == 0 ) {
			return R.style.DarkTheme;
		} else if( d.compareTo( "LightTheme" ) == 0 ) {
			return R.style.LightTheme;
		} else {
			Log.w( ApplicationCore.TAG, "The selected theme is unknown ('" + d + "'), returning default theme!" );
			return R.style.DarkTheme;
		}
	}

	public static boolean isServiceRunning( final Context ctx, final String serviceName ) {
		Log.v( ApplicationCore.TAG, "Checking if the monitoring service is running or not..." );
		boolean serviceRunning = false;
		final ActivityManager am = (ActivityManager) ctx.getSystemService( Context.ACTIVITY_SERVICE );
		final List< ActivityManager.RunningServiceInfo > l = am.getRunningServices( 50 );
		final Iterator< ActivityManager.RunningServiceInfo > i = l.iterator();
		while( i.hasNext() ) {
			final ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

			if( runningServiceInfo.service.getClassName().equals( serviceName ) ) {
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}
}
