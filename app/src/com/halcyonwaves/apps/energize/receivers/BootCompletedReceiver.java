package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive( final Context context, final Intent intent ) {
		final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences( context );
		if( prefs.getBoolean( "advance.start_on_boot", true ) ) {
			context.startService( new Intent( context, MonitorBatteryStateService.class ) );
		}
	}

}
