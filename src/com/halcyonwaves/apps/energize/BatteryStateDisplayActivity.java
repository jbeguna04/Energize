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

package com.halcyonwaves.apps.energize;

import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * This class defines the behavior of the first activity the user sees after he
 * or she started the application through the launcher entry or by clicking the
 * item in the status bar of the device.
 * 
 * @author Tim Huetz
 */
public class BatteryStateDisplayActivity extends Activity {

	private TextView batteryPercentage = null;
	private Messenger monitorService = null;
	private final Messenger monitorServiceMessanger = new Messenger( new IncomingHandler() );

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				case MonitorBatteryStateService.MSG_REQUEST_LAST_CHARGING_PCT:
					BatteryStateDisplayActivity.this.batteryPercentage.setText( String.valueOf( msg.arg1 ) );
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

	private ServiceConnection monitorServiceConnection = new ServiceConnection() {

		public void onServiceConnected( ComponentName className, IBinder service ) {
			BatteryStateDisplayActivity.this.monitorService = new Messenger( service );
			try {
				Log.d( "BatteryStateDisplayActivity", "Trying to connect to the battery monitoring service..." );
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_REGISTER_CLIENT );
				msg.replyTo = BatteryStateDisplayActivity.this.monitorServiceMessanger;
				BatteryStateDisplayActivity.this.monitorService.send( msg );
			} catch( RemoteException e ) {
				Log.e( "BatteryStateDisplayActivity", "Failed to connect to the battery monitoring service!" );
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			BatteryStateDisplayActivity.this.monitorService = null;
		}
	};

	private void doBindService() {
		this.bindService( new Intent( this, MonitorBatteryStateService.class ), this.monitorServiceConnection, Context.BIND_AUTO_CREATE );
	}

	private void doUnbindService() {
		if( this.monitorService != null ) {
			try {
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_UNREGISTER_CLIENT );
				msg.replyTo = this.monitorServiceMessanger;
				this.monitorService.send( msg );
			} catch( RemoteException e ) {
			}
		}
		this.unbindService( this.monitorServiceConnection );
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		this.doUnbindService();
	}

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		this.setTheme( ApplicationCore.getSelectedThemeId( this.getApplicationContext() ) );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// check if the service is running, if not start it
		if( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		// get the handles to controls we want to modify
		this.batteryPercentage = (TextView) this.findViewById( R.id.tv_battery_pct );

		//
		this.doBindService();
	}

	@Override
	public boolean onCreateOptionsMenu( final Menu menu ) {
		this.getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onOptionsItemSelected( final MenuItem item ) {
		switch( item.getItemId() ) {
			case R.id.menu_preferences:
				final Intent myIntent = new Intent( BatteryStateDisplayActivity.this, SettingsActivity.class );
				BatteryStateDisplayActivity.this.startActivity( myIntent );
				return true;
			default:
				return false;
		}
	}
}
