package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class is used as a receiver for broadcasts depending the change of the
 * power connections. It gets called if the user changes the way the device is
 * powered (e.g. the user plugs or unplugs the device from a USB or charging
 * device.
 * 
 * @author Tim Huetz
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive( final Context arg0, final Intent arg1 ) {
		// TODO: implement this
	}
}
