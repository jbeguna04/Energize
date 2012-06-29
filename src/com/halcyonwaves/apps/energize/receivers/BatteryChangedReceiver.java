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
		int pct = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
		int powerSource = intent.getIntExtra( BatteryManager.EXTRA_PLUGGED, -1 );
		this.service.insertPowerValue( powerSource, pct );
	}

}
