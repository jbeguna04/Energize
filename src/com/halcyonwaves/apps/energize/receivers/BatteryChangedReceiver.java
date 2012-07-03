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

package com.halcyonwaves.apps.energize.receivers;

import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;


public class BatteryChangedReceiver extends BroadcastReceiver {
	
	private MonitorBatteryStateService service = null;
	
	public BatteryChangedReceiver( MonitorBatteryStateService service ) {
		this.service = service;		
	}

	@Override
	public void onReceive( Context context, Intent intent ) {
		int level = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
		int scale = intent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
		int powerSource = intent.getIntExtra( BatteryManager.EXTRA_PLUGGED, -1 );
		this.service.insertPowerValue( powerSource, scale, level );
	}

}
