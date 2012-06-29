/**
 * Energize - An Android battery monitor
 * Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.halcyonwaves.apps.energize.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.IBinder;
import android.util.Log;

public class MonitorBatteryStateService extends Service {

	private final BroadcastReceiver powerStateChangedReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive( Context context, Intent intent ) {
			Log.v( "MonitorBatteryStateService", "Value: " + intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 ) );
		}

	};

	@Override
	public int onStartCommand( Intent intent, int flags, int startid ) {
		//
		Log.v( "MonitorBatteryStateService", "Starting service for collecting battery statistics..." );

		//
		this.registerReceiver( this.powerStateChangedReceiver, new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) );

		//
		Log.v( "MonitorBatteryStateService", "Service successfully started" );
		return START_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent ) {
		return null;
	}

}
