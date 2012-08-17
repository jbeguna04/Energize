package com.halcyonwaves.apps.energize.fragments;

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.view.LayoutInflater;
import android.view.View;

public class UnifiedPreferenceFragment extends PreferenceFragment {

	private final static String TAG = "UnifiedPreferenceFragment";
	private Preference whatsNewPreference = null;
	private Preference sendDatabasePreference = null;
	private Messenger monitorService = null;
	private final Messenger monitorServiceMessanger = new Messenger( new IncomingHandler() );

	class IncomingHandler extends Handler {

		@Override
		public void handleMessage( Message msg ) {
			switch( msg.what ) {
				case MonitorBatteryStateService.MSG_CLEAR_STATISTICS:
					AlertDialog.Builder builder = new AlertDialog.Builder( UnifiedPreferenceFragment.this.getActivity() );

					builder.setTitle( R.string.dialog_title_cleardb_successfull ).setMessage( R.string.dialog_text_cleardb_successfull ).setPositiveButton( android.R.string.ok, new OnClickListener() {

						public void onClick( DialogInterface dialog, int which ) {
							dialog.dismiss();

						}
					} );
					builder.create().show();
					// TODO: show notification
					break;
				default:
					super.handleMessage( msg );
			}
		}
	}

	private ServiceConnection monitorServiceConnection = new ServiceConnection() {

		public void onServiceConnected( ComponentName className, IBinder service ) {
			UnifiedPreferenceFragment.this.monitorService = new Messenger( service );
			try {
				Log.d( UnifiedPreferenceFragment.TAG, "Trying to connect to the battery monitoring service..." );
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_REGISTER_CLIENT );
				msg.replyTo = UnifiedPreferenceFragment.this.monitorServiceMessanger;
				UnifiedPreferenceFragment.this.monitorService.send( msg );
			} catch( RemoteException e ) {
				Log.e( UnifiedPreferenceFragment.TAG, "Failed to connect to the battery monitoring service!" );
			}
		}

		public void onServiceDisconnected( ComponentName className ) {
			UnifiedPreferenceFragment.this.monitorService = null;
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
	public void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_unified );

		this.sendDatabasePreference = this.findPreference( "batstatistics.cleardb" );
		this.sendDatabasePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				Log.v( UnifiedPreferenceFragment.TAG, "Clearing battery statistics database..." );
				try {
					Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_CLEAR_STATISTICS );
					msg.replyTo = UnifiedPreferenceFragment.this.monitorServiceMessanger;
					UnifiedPreferenceFragment.this.monitorService.send( msg );
				} catch( RemoteException e ) {
					Log.e( UnifiedPreferenceFragment.TAG, "Failed to clear the battery statistics database!" );
				}
				return false;
			}
		} );

		this.doBindService();
	}
}
