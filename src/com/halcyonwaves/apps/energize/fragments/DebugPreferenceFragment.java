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

package com.halcyonwaves.apps.energize.fragments;

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
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class DebugPreferenceFragment extends PreferenceFragment {
	
	private Preference sendDatabasePreference = null;
	private Messenger monitorService = null;
	private final Messenger monitorServiceMessanger = new Messenger( new IncomingHandler() );

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				default:
					super.handleMessage( msg );
			}
		}
	}

	private ServiceConnection monitorServiceConnection = new ServiceConnection() {

		public void onServiceConnected( ComponentName className, IBinder service ) {
			DebugPreferenceFragment.this.monitorService = new Messenger( service );
			try {
				Log.d( "DebugPreferenceFragment", "Trying to connect to the battery monitoring service..." );
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_REGISTER_CLIENT );
				msg.replyTo = DebugPreferenceFragment.this.monitorServiceMessanger;
				DebugPreferenceFragment.this.monitorService.send( msg );
			} catch( RemoteException e ) {
				Log.e( "DebugPreferenceFragment", "Failed to connect to the battery monitoring service!" );
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			DebugPreferenceFragment.this.monitorService = null;
		}
	};

	private void doBindService() {
		this.getActivity().bindService( new Intent( this.getActivity(), MonitorBatteryStateService.class ), this.monitorServiceConnection, Context.BIND_AUTO_CREATE );
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
		this.getActivity().unbindService( this.monitorServiceConnection );
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		this.doUnbindService();
	}

	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_debug );
		
		this.sendDatabasePreference = this.findPreference( "debug.send_batterystats_db" );
		this.sendDatabasePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick( Preference preference ) {
				Log.v( "DebugPreferenceFragment", "Prepare battery statistics database for sending via mail..." );
				try {
					DebugPreferenceFragment.this.monitorServiceMessanger.send(Message.obtain(null, MonitorBatteryStateService.MSG_STOP_MONITORING, 0, 0));
					// TODO: get the path and copy the file
					DebugPreferenceFragment.this.monitorServiceMessanger.send(Message.obtain(null, MonitorBatteryStateService.MSG_START_MONITORING, 0, 0));
				} catch( RemoteException e ) {
					Log.e( "DebugPreferenceFragment", "Failed to prepare battery statistics database for sending via mail!" );
				}
				return false;
			}
		} );
		
		this.doBindService();
	}
}
