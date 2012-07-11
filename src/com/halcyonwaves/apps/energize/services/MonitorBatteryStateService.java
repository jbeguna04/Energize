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

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.halcyonwaves.apps.energize.receivers.BatteryChangedReceiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
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
	public static final int MSG_START_MONITORING = 4;
	public static final int MSG_STOP_MONITORING = 5;
	public static final int MSG_REQUEST_DB_PATH = 6;

	private static final int MY_NOTIFICATION_ID = 1;

	private BatteryChangedReceiver batteryChangedReceiver = null;
	private BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = null;
	private SQLiteDatabase batteryStatisticsDatabase = null;
	private ArrayList< Messenger > connectedClients = new ArrayList< Messenger >();
	private int lastChargingPercentage = -1;
	private final Messenger serviceMessenger = new Messenger( new IncomingHandler() );
	private NotificationManager notificationManager = null;
	private Notification myNotification = null;

	public void insertPowerValue( int powerSource, int scale, int level ) {
		long currentUnixTime = (long) (System.currentTimeMillis() / 1000);

		if( null != this.batteryStatisticsDatabase && this.batteryStatisticsDatabase.isOpen() ) {
			ContentValues values = new ContentValues();
			values.put( RawBatteryStatisicsTable.COLUMN_EVENT_TIME, currentUnixTime );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_STATE, powerSource );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE, scale );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL, level );

			this.batteryStatisticsDatabase.insert( RawBatteryStatisicsTable.TABLE_NAME, null, values );
		}

		this.lastChargingPercentage = level;
		this.showNewPercentageNotification( level );
		MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
	}

	@Override
	public void onDestroy() {
		this.batteryDbOpenHelper.close();
		this.batteryStatisticsDatabase = null;
		super.onDestroy();
	}

	private void stopMonitoring() {
		this.batteryDbOpenHelper.close();
		this.batteryStatisticsDatabase = null;
	}

	private void startMonitoring() {
		this.batteryStatisticsDatabase = this.batteryDbOpenHelper.getWritableDatabase();
	}

	private void showNewPercentageNotification( int percentage ) {
		this.myNotification = new Notification.Builder( this ).setContentTitle( this.getString( R.string.notification_title_remaining, percentage ) ).setContentText( this.getText( R.string.notification_text_estimate ) ).setSmallIcon( R.drawable.ic_stat_00_pct_charged + percentage ).getNotification();
		this.myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify( MY_NOTIFICATION_ID, myNotification );
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startid ) {
		//
		Log.v( "MonitorBatteryStateService", "Starting service for collecting battery statistics..." );

		//
		this.notificationManager = (NotificationManager) this.getSystemService( Context.NOTIFICATION_SERVICE );

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
				msg.send( Message.obtain( null, MonitorBatteryStateService.MSG_REQUEST_LAST_CHARGING_PCT, this.lastChargingPercentage, 0 ) );
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
				case MonitorBatteryStateService.MSG_START_MONITORING:
					Log.d( "MonitorBatteryStateService", "Starting battery monitoring..." );
					MonitorBatteryStateService.this.startMonitoring();
					break;
				case MonitorBatteryStateService.MSG_STOP_MONITORING:
					Log.d( "MonitorBatteryStateService", "Stopping battery monitoring..." );
					MonitorBatteryStateService.this.stopMonitoring();
					break;
				case MonitorBatteryStateService.MSG_REQUEST_DB_PATH:
					Log.d( "MonitorBatteryStateService", "Database path requested, sending it back..." );
					try {
						msg.replyTo.send( Message.obtain( null, MonitorBatteryStateService.MSG_REQUEST_DB_PATH, (new ContextWrapper( MonitorBatteryStateService.this )).getDatabasePath( MonitorBatteryStateService.this.batteryDbOpenHelper.getDatabaseName() ).getAbsolutePath() ) );
					} catch( RemoteException e ) {
						Log.e( "MonitorBatteryStateService", "Failed to send the databasae path!" );
					}
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

}
