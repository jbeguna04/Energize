package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

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
	}
}
