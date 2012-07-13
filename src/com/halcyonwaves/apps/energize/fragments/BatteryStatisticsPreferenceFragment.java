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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;

public class BatteryStatisticsPreferenceFragment extends PreferenceFragment {
	private Preference sendDatabasePreference = null;
	private Messenger monitorService = null;
	private final Messenger monitorServiceMessanger = new Messenger( new IncomingHandler() );

	class IncomingHandler extends Handler {
	
		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				case MonitorBatteryStateService.MSG_CLEAR_STATISTICS:
					AlertDialog.Builder builder         = new AlertDialog.Builder(BatteryStatisticsPreferenceFragment.this.getActivity());
					 
			        builder.setTitle("Whats New").setMessage("TODO")
			        .setPositiveButton("OK", new DialogInterface() {
						
			            @Override
			            public void onClick(DialogInterface dialog, int which) {
			                dialog.dismiss();
			            }
			        });
					// TODO: show notification
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

	private ServiceConnection monitorServiceConnection = new ServiceConnection() {

		public void onServiceConnected( ComponentName className, IBinder service ) {
			BatteryStatisticsPreferenceFragment.this.monitorService = new Messenger( service );
			try {
				Log.d( "BatteryStatisticsPreferenceFragment", "Trying to connect to the battery monitoring service..." );
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_REGISTER_CLIENT );
				msg.replyTo = BatteryStatisticsPreferenceFragment.this.monitorServiceMessanger;
				BatteryStatisticsPreferenceFragment.this.monitorService.send( msg );
			} catch( RemoteException e ) {
				Log.e( "BatteryStatisticsPreferenceFragment", "Failed to connect to the battery monitoring service!" );
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			BatteryStatisticsPreferenceFragment.this.monitorService = null;
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
		this.addPreferencesFromResource( R.xml.pref_batterystatistics );
		
		this.sendDatabasePreference = this.findPreference( "batstatistics.cleardb" );
		this.sendDatabasePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {
			
//			@Override
			public boolean onPreferenceClick( Preference preference ) {
				Log.v( "BatteryStatisticsPreferenceFragment", "Clearing battery statistics database..." );
				try {			
					Message msg = Message.obtain(null, MonitorBatteryStateService.MSG_CLEAR_STATISTICS);
					msg.replyTo = BatteryStatisticsPreferenceFragment.this.monitorServiceMessanger;
					BatteryStatisticsPreferenceFragment.this.monitorService.send( msg );
				} catch( RemoteException e ) {
					Log.e( "BatteryStatisticsPreferenceFragment", "Failed to clear the battery statistics database!" );
				}
				return false;
			}
		} );
		
		this.doBindService();
	}
}
