package com.halcyonwaves.apps.energize;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class ApplicationCore extends Application {

	public static int getSelectedThemeId( final Context context ) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		final String d = prefs.getString( "display.theme", "UnknownTheme" );
		if( d.compareTo( "DarkTheme" ) == 0 ) {
			return R.style.DarkTheme;
		} else if( d.compareTo( "LightTheme" ) == 0 ) {
			return R.style.LightTheme;
		} else {
			Log.w( "BatteryStateDisplayActivity.Theme", "The selected theme is unknown ('" + d + "'), returning default theme!" );
			return R.style.DarkTheme;
		}
	}
}
