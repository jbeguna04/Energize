package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.util.Log;

import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabase;

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
		Log.v( ">>>>>>>>>>>", "Blubn" );
		
		// get some required information about the current battery state
		final int rawBatteryLevel = batteryIntent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
		final int batteryScale = batteryIntent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );

		// calculate the real battery level
		int batteryLevel = -1;
		if( (rawBatteryLevel >= 0) && (batteryScale > 0) ) {
			batteryLevel = (rawBatteryLevel * 100) / batteryScale;
			Log.d( "BatteryLevelReceiver.LevelChanged", "Batter level changed to " + batteryLevel + "%" );
		}

		// store the value inside of the database
		final BatteryStatisticsDatabase statisticDB = new BatteryStatisticsDatabase( context );
		statisticDB.storeBatteryLevelChange( rawBatteryLevel, batteryScale, batteryLevel );
	}
}
