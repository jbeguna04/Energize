package com.halcyonwaves.apps.energize.fragments;

import com.halcyonwaves.apps.energize.R;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class OverviewFragment extends Fragment {
	
	private TextView textViewCurrentLoadingLevel = null;
	private TextView textViewCurrentChargingState = null;
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		// inflate the static part of the view
		View inflatedView = inflater.inflate( R.layout.fragment_maininformation, container, false );
		
		// get the handles to some important controls
		this.textViewCurrentLoadingLevel = (TextView) inflatedView.findViewById( R.id.textview_text_current_charginglvl );
		this.textViewCurrentChargingState = (TextView) inflatedView.findViewById( R.id.textview_text_current_chargingstate );
		
		// get the current battery state and show it on the main activity
		BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

			public void onReceive( Context context, Intent intent ) {
				context.unregisterReceiver( this );
				int rawlevel = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
				int scale = intent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
				int status = intent.getIntExtra( BatteryManager.EXTRA_STATUS, -1 );
				int level = -1;
				if( rawlevel >= 0 && scale > 0 ) {
					level = (rawlevel * 100) / scale;
				}
				switch( status ) {
					case BatteryManager.BATTERY_STATUS_CHARGING:
						OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_charging ) );
						break;
					case BatteryManager.BATTERY_STATUS_DISCHARGING:
						OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_discharging ) );
						break;
					case BatteryManager.BATTERY_STATUS_FULL:
						OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_full ) );
						break;
					default:
						OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_unknown ) );
						break;
				}

				OverviewFragment.this.textViewCurrentLoadingLevel.setText( level + "" ); // TODO
			}
		};
		IntentFilter batteryLevelFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
		this.getActivity().registerReceiver( batteryLevelReceiver, batteryLevelFilter );
		
		// return the inflated view
		return inflatedView;
	}
}
