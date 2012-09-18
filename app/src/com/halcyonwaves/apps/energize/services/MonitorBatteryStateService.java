package com.halcyonwaves.apps.energize.services;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.halcyonwaves.apps.energize.BatteryStateDisplayActivity;
import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.halcyonwaves.apps.energize.estimators.BatteryEstimationMgr;
import com.halcyonwaves.apps.energize.estimators.EstimationResult;
import com.halcyonwaves.apps.energize.receivers.BatteryChangedReceiver;

public class MonitorBatteryStateService extends Service implements OnSharedPreferenceChangeListener {

	private class IncomingHandler extends Handler {

		@Override
		public void handleMessage( final Message msg ) {
			switch( msg.what ) {
				case MonitorBatteryStateService.MSG_REGISTER_CLIENT:
					Log.d( MonitorBatteryStateService.TAG, "Registering new client to the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.add( msg.replyTo );
					break;
				case MonitorBatteryStateService.MSG_UNREGISTER_CLIENT:
					Log.d( MonitorBatteryStateService.TAG, "Unregistering client from the battery monitoring service..." );
					MonitorBatteryStateService.this.connectedClients.remove( msg.replyTo );
					break;
				case MonitorBatteryStateService.MSG_CLEAR_STATISTICS:
					Log.d( MonitorBatteryStateService.TAG, "Clearing battery statistics database..." );
					try {
						MonitorBatteryStateService.this.batteryStatisticsDatabase.delete( RawBatteryStatisicsTable.TABLE_NAME, null, null );
						msg.replyTo.send( Message.obtain( null, MonitorBatteryStateService.MSG_CLEAR_STATISTICS ) );
					} catch( final RemoteException e ) {
						Log.e( MonitorBatteryStateService.TAG, "Failed to clear battery statistics database!" );
					}
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

	public static final int MSG_CLEAR_STATISTICS = 7;
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;

	private static final int MY_NOTIFICATION_ID = 1;

	private static final String TAG = "MonitorBatteryStateService";
	private SharedPreferences appPreferences = null;
	private BatteryChangedReceiver batteryChangedReceiver = null;
	private BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = null;
	private SQLiteDatabase batteryStatisticsDatabase = null;
	private final ArrayList< Messenger > connectedClients = new ArrayList< Messenger >();
	private Notification myNotification = null;
	private NotificationManager notificationManager = null;

	private final Messenger serviceMessenger = new Messenger( new IncomingHandler() );

	public void insertPowerSupplyChangeEvent( final boolean isChargingNow ) {

	}

	public void insertPowerValue( final int powerSource, final int scale, final int level, final int temprature ) {
		// if the database is not open, skip the insertion process
		if( (null == this.batteryStatisticsDatabase) || !this.batteryStatisticsDatabase.isOpen() ) {
			Log.e( MonitorBatteryStateService.TAG, "Tried to insert a dataset into a closed database, skipping..." );
			return;
		}

		// get the last entry we made on our database, if the entries are the same we want to insert, skip the insertion process
		final Cursor lastEntryMadeCursor = this.batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " DESC" );

		// if the level changed, we can insert the entry into our database
		if( !lastEntryMadeCursor.moveToFirst() || (level != lastEntryMadeCursor.getInt( lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL ) )) ) {
			final long currentUnixTime = System.currentTimeMillis() / 1000;
			final ContentValues values = new ContentValues();
			values.put( RawBatteryStatisicsTable.COLUMN_EVENT_TIME, currentUnixTime );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_STATE, powerSource );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE, scale );
			values.put( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL, level );
			values.put( RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE, temprature );
			this.batteryStatisticsDatabase.insert( RawBatteryStatisicsTable.TABLE_NAME, null, values );
		}

		// close the database cursor again
		lastEntryMadeCursor.close();

		// show the notification
		this.showNewPercentageNotification();
	}

	@Override
	public IBinder onBind( final Intent intent ) {
		return this.serviceMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		this.batteryDbOpenHelper.close();
		this.batteryStatisticsDatabase = null;
		super.onDestroy();
	}

	public void onSharedPreferenceChanged( final SharedPreferences sharedPreferences, final String key ) {
		if( 0 == key.compareTo( "advance.show_notification_bar" ) ) {
			final boolean showShowIcon = sharedPreferences.getBoolean( "advance.show_notification_bar", true );
			Log.v( MonitorBatteryStateService.TAG, "Notification icon setting chaanged to: " + showShowIcon );
			if( !showShowIcon ) {
				this.notificationManager.cancel( MonitorBatteryStateService.MY_NOTIFICATION_ID );
				this.myNotification = null;
			} else {
				this.showNewPercentageNotification();
			}
		}
	}

	@Override
	public int onStartCommand( final Intent intent, final int flags, final int startid ) {
		//
		Log.v( MonitorBatteryStateService.TAG, "Starting service for collecting battery statistics..." );

		//
		this.appPreferences = PreferenceManager.getDefaultSharedPreferences( this.getApplicationContext() );
		this.appPreferences.registerOnSharedPreferenceChangeListener( this );

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
		return Service.START_STICKY;
	}

	private void showNewPercentageNotification() {
		// query the current estimation values
		final EstimationResult estimation = BatteryEstimationMgr.getEstimation( this.getApplicationContext() );

		// be sure that it is a valid percentage
		if( !estimation.isValid ) {
			Log.e( MonitorBatteryStateService.TAG, "The application tried to show an invalid loading level." );
			return;
		}

		// if we should not show the notification, skip the method here

		if( !this.appPreferences.getBoolean( "advance.show_notification_bar", true ) ) {
			return;
		}

		// calculate the estimates for the notification window
		final int remainingHours = estimation.minutes > 0 ? (int) Math.floor( estimation.minutes / 60.0 ) : 0;
		final int remainingMinutesNew = estimation.minutes - (60 * remainingHours);

		// determine the correct title string for the notification
		int notificationTitleId = R.string.notification_title_discharges;
		if( estimation.charging ) {
			notificationTitleId = R.string.notification_title_charges;
		}

		// prepare the notification object
		final NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder( this.getApplicationContext() );
		notificationBuilder.setContentTitle( this.getString( notificationTitleId ) );
		notificationBuilder.setSmallIcon( R.drawable.ic_stat_00_pct_charged + estimation.level );
		notificationBuilder.setOngoing( true );
		notificationBuilder.setContentIntent( PendingIntent.getActivity( this.getApplicationContext(), 0, new Intent( this.getApplicationContext(), BatteryStateDisplayActivity.class ), 0 ) );
		notificationBuilder.setPriority( NotificationCompat.PRIORITY_LOW );

		// if the capacity reaches 15%, use a high priority
		if( estimation.level <= 15 ) {
			notificationBuilder.setPriority( NotificationCompat.PRIORITY_HIGH );
		}

		// show the notification
		if( remainingMinutesNew <= -1 ) {
			notificationBuilder.setContentText( this.getString( R.string.notification_text_estimate_na ) );
		} else {
			notificationBuilder.setContentText( this.getString( R.string.notification_text_estimate, remainingHours, remainingMinutesNew ) );
		}

		// get the created notification and show it
		this.myNotification = notificationBuilder.build();
		this.notificationManager.notify( MonitorBatteryStateService.MY_NOTIFICATION_ID, this.myNotification );
	}
}
