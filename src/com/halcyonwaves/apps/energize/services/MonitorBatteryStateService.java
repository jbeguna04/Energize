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

import java.util.ArrayList;

import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.halcyonwaves.apps.energize.receivers.BatteryChangedReceiver;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MonitorBatteryStateService extends Service {

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_REQUEST_LAST_CHARGING_PCT = 3;

	private BatteryChangedReceiver batteryChangedReceiver = null;
	private BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = null;
	private SQLiteDatabase batteryStatisticsDatabase = null;
	private ArrayList< Messenger > connectedClients = new ArrayList< Messenger >();
	private int lastChargingPercentage = -1;
	private final Messenger serviceMessenger = new Messenger( new IncomingHandler() );

	public void insertPowerValue( int powerSource, int batteryCapacity ) {
		long currentUnixTime = (long) (System.currentTimeMillis() / 1000);

		ContentValues values = new ContentValues();
		values.put( RawBatteryStatisicsTable.COLUMN_EVENT_TIME, currentUnixTime );
		values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_STATE, powerSource );
		values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_PCT, batteryCapacity );

		this.batteryStatisticsDatabase.insert( RawBatteryStatisicsTable.TABLE_NAME, null, values );
		this.lastChargingPercentage = batteryCapacity;
		MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
	}

	@Override
	public void onDestroy() {
		this.batteryDbOpenHelper.close();
		super.onDestroy();
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startid ) {
		//
		Log.v( "MonitorBatteryStateService", "Starting service for collecting battery statistics..." );

		//
		this.batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getApplicationContext() );
		this.batteryStatisticsDatabase = this.batteryDbOpenHelper.getWritableDatabase();

		//
		this.batteryChangedReceiver = new BatteryChangedReceiver( this );
		this.registerReceiver( this.batteryChangedReceiver, new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) );

		//
		Log.v( "MonitorBatteryStateService", "Service successfully started" );
		return START_STICKY;
	}

	@Override
	public IBinder onBind( Intent intent ) {
		return this.serviceMessenger.getBinder();
	}
	
	private void sendCurrentChargingPctToClients() {
		try {
			for( Messenger msg : this.connectedClients ) {
				msg.send(Message.obtain(null, MonitorBatteryStateService.MSG_REQUEST_LAST_CHARGING_PCT, this.lastChargingPercentage, 0));
			}
		} catch( RemoteException e ) {
			// nothing
		}
	}

	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				case MonitorBatteryStateService.MSG_REGISTER_CLIENT:
					Log.d( "MonitorBatteryStateService", "Registering new client to the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.add( msg.replyTo );
					MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
					break;
				case MonitorBatteryStateService.MSG_UNREGISTER_CLIENT:
					Log.d( "MonitorBatteryStateService", "Unregistering client from the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.remove( msg.replyTo );
					break;
				case MonitorBatteryStateService.MSG_REQUEST_LAST_CHARGING_PCT:
					Log.d( "MonitorBatteryStateService", "Received request of the charging percentage..." );
					MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

}
