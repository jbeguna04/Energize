/**
 * Energize - An Android battery monitor Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class MonitorBatteryStateService extends Service {

	private static final String TAG = "MonitorBatteryStateService";

	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_REQUEST_LAST_CHARGING_PCT = 3;
	public static final int MSG_START_MONITORING = 4;
	public static final int MSG_STOP_MONITORING = 5;
	public static final int MSG_REQUEST_DB_PATH = 6;
	public static final int MSG_CLEAR_STATISTICS = 7;

	private static final int MY_NOTIFICATION_ID = 1;

	private BatteryChangedReceiver batteryChangedReceiver = null;
	private BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = null;
	private SQLiteDatabase batteryStatisticsDatabase = null;
	private ArrayList< Messenger > connectedClients = new ArrayList< Messenger >();
	private int lastChargingPercentage = -1;
	private int lastRemainingMinutes = -1;
	private final Messenger serviceMessenger = new Messenger( new IncomingHandler() );
	private NotificationManager notificationManager = null;
	private Notification myNotification = null;

	public void insertPowerValue( int powerSource, int scale, int level ) {
		// if the database is not open, skip the insertion process
		if( null == this.batteryStatisticsDatabase || !this.batteryStatisticsDatabase.isOpen() ) {
			Log.e( MonitorBatteryStateService.TAG, "Tried to insert a dataset into a closed database, skipping..." );
			return;
		}

		// get the last entry we made on our database, if the entries are the same we want to insert, skip the insertion process
		Cursor lastEntryMadeCursor = this.batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL, RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " DESC" );
		if( lastEntryMadeCursor.moveToFirst() ) {
			if( level == lastEntryMadeCursor.getInt( lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL ) ) && scale == lastEntryMadeCursor.getInt( lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE ) ) ) {
				Log.d( MonitorBatteryStateService.TAG, "Tried to insert an already existing dataset, skipping..." );

				// if it is the first run of the application, the percentage would be -1 if we won't set it here
				this.lastChargingPercentage = level;

				// tell all connected clients about the current charging level and the remaining time
				MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
				this.showNewPercentageNotification( level, this.lastRemainingMinutes );

				// skip the insertion process
				return;
			}
		}
		lastEntryMadeCursor.close();

		// insert the new dataset into our database
		final long currentUnixTime = (long) (System.currentTimeMillis() / 1000);
		ContentValues values = new ContentValues();
		values.put( RawBatteryStatisicsTable.COLUMN_EVENT_TIME, currentUnixTime );
		values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_STATE, powerSource );
		values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE, scale );
		values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL, level );
		this.batteryStatisticsDatabase.insert( RawBatteryStatisicsTable.TABLE_NAME, null, values );

		// store the charging level and update the notification about the current charging level
		this.lastChargingPercentage = level;

		// calculate the remaining time in minutes
		Cursor querCursor = this.batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_EVENT_TIME }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " DESC" );
		if( querCursor.moveToFirst() ) {
			long lastEventTime = querCursor.getLong( querCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME ) );
			if( querCursor.moveToNext() ) {
				long prevEventTime = querCursor.getLong( querCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME ) );
				long diff = Math.abs( lastEventTime - prevEventTime );
				Log.v( MonitorBatteryStateService.TAG, String.format( "Calculated the time between the last two (%d, %d) events: %d", lastEventTime, prevEventTime, diff ) );
				if( RawBatteryStatisicsTable.CHARGING_STATE_UNCHARGING == powerSource ) {
					this.lastRemainingMinutes = (int) (Math.round( ((this.lastChargingPercentage) * diff) / 60.0f ));
					Log.v( MonitorBatteryStateService.TAG, String.format( "Calculated remaining battery life in minutes: %d", lastRemainingMinutes ) );
				} else {
					this.lastRemainingMinutes = (int) (Math.round( ((100.0f - this.lastChargingPercentage) * diff) / 60.0f ));
					Log.v( MonitorBatteryStateService.TAG, String.format( "Calculated remaining charging time in minutes: %d", lastRemainingMinutes ) );
				}
			}
		}
		querCursor.close();

		// tell all connected clients about the current charging level and the remaining time
		MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
		this.showNewPercentageNotification( level, this.lastRemainingMinutes );
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

	private void showNewPercentageNotification( int percentage, int remainingMinutes ) {
		// be sure that it is a valid percentage
		if( percentage < 0 || percentage > 100 ) {
			Log.e( MonitorBatteryStateService.TAG, "The application tried to show an invalid loading level." );
			return;
		}

		// calculate the estimates for the notification window
		final int remainingHours = remainingMinutes > 0 ? (int) Math.floor( remainingMinutes / 60.0 ) : 0;
		final int remainingMinutesNew = remainingMinutes - (60 * remainingHours);

		// show the notification
		this.myNotification = new Notification.Builder( this ).setContentTitle( this.getString( R.string.notification_title_remaining, percentage ) ).setContentText( this.getString( R.string.notification_text_estimate, remainingHours, remainingMinutesNew ) ).setSmallIcon( R.drawable.ic_stat_00_pct_charged + percentage ).getNotification();
		this.myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify( MY_NOTIFICATION_ID, myNotification );
	}

	@Override
	public int onStartCommand( Intent intent, int flags, int startid ) {
		//
		Log.v( MonitorBatteryStateService.TAG, "Starting service for collecting battery statistics..." );

		//
		this.notificationManager = (NotificationManager) this.getSystemService( Context.NOTIFICATION_SERVICE );

		//
		this.batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getApplicationContext() );
		this.batteryStatisticsDatabase = this.batteryDbOpenHelper.getWritableDatabase();

		//
		this.batteryChangedReceiver = new BatteryChangedReceiver( this );
		this.registerReceiver( this.batteryChangedReceiver, new IntentFilter( Intent.ACTION_BATTERY_CHANGED ) );

		//
		Log.v( MonitorBatteryStateService.TAG, "Service successfully started" );
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
					Log.d( MonitorBatteryStateService.TAG, "Registering new client to the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.add( msg.replyTo );
					MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
					break;
				case MonitorBatteryStateService.MSG_UNREGISTER_CLIENT:
					Log.d( MonitorBatteryStateService.TAG, "Unregistering client from the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.remove( msg.replyTo );
					break;
				case MonitorBatteryStateService.MSG_REQUEST_LAST_CHARGING_PCT:
					Log.d( MonitorBatteryStateService.TAG, "Received request of the charging percentage..." );
					MonitorBatteryStateService.this.sendCurrentChargingPctToClients();
					break;
				case MonitorBatteryStateService.MSG_START_MONITORING:
					Log.d( MonitorBatteryStateService.TAG, "Starting battery monitoring..." );
					MonitorBatteryStateService.this.startMonitoring();
					break;
				case MonitorBatteryStateService.MSG_STOP_MONITORING:
					Log.d( MonitorBatteryStateService.TAG, "Stopping battery monitoring..." );
					MonitorBatteryStateService.this.stopMonitoring();
					break;
				case MonitorBatteryStateService.MSG_REQUEST_DB_PATH:
					Log.d( MonitorBatteryStateService.TAG, "Database path requested, sending it back..." );
					try {
						msg.replyTo.send( Message.obtain( null, MonitorBatteryStateService.MSG_REQUEST_DB_PATH, (new ContextWrapper( MonitorBatteryStateService.this )).getDatabasePath( MonitorBatteryStateService.this.batteryDbOpenHelper.getDatabaseName() ).getAbsolutePath() ) );
					} catch( RemoteException e ) {
						Log.e( MonitorBatteryStateService.TAG, "Failed to send the databasae path!" );
					}
					break;
				case MonitorBatteryStateService.MSG_CLEAR_STATISTICS:
					Log.d( MonitorBatteryStateService.TAG, "Clearing battery statistics database..." );
					try {
						MonitorBatteryStateService.this.batteryStatisticsDatabase.delete( RawBatteryStatisicsTable.TABLE_NAME, null, null );
						msg.replyTo.send( Message.obtain( null, MonitorBatteryStateService.MSG_CLEAR_STATISTICS ) );
					} catch( RemoteException e ) {
						Log.e( MonitorBatteryStateService.TAG, "Failed to clear battery statistics database!" );
					}
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

}
