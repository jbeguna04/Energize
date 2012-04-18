package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

/**
 * This class is used as a receiver for broadcasts depending the change of the
 * battery charging level. As soon as the battery charging level changes up or
 * downwards, this receiver gets called.
 * 
 * @author Tim Huetz
 */
public class BatteryLevelReceiver extends BroadcastReceiver {

	@Override
	public void onReceive( final Context context, final Intent batteryIntent ) {
		// get the raw information about the battery level and scale
		final int batteryLevel = batteryIntent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
		final int batteryScale = batteryIntent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );

		// TODO: store the value inside of the database
	}
}
