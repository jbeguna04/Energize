package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class PowerSupplyPulledOffReceiver extends BroadcastReceiver {

	private MonitorBatteryStateService service = null;

	public PowerSupplyPulledOffReceiver( final MonitorBatteryStateService service ) {
		this.service = service;
	}

	@Override
	public void onReceive( final Context context, final Intent intent ) {
		this.service.insertPowerSupplyChangeEvent( false );
	}

}
