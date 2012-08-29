package com.halcyonwaves.apps.energize.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.halcyonwaves.apps.energize.R;

public class OverviewFragment extends Fragment {

	private static final String TAG = "OverviewFragment";

	private SharedPreferences sharedPref = null;
	private TextView textViewCurrentChargingState = null;
	private TextView textViewCurrentLoadingLevel = null;
	private TextView textViewCurrentLoadingLevelAsusDock = null;;
	private TextView textViewCurrentLoadingLevelAsusDockLabel = null;
	private TextView textViewTemp = null;

	// private boolean batteryDischarging = false;

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getActivity().getApplicationContext() );

		// inflate the static part of the view
		final View inflatedView = inflater.inflate( R.layout.fragment_maininformation, container, false );

		// get the handles to some important controls
		this.textViewCurrentLoadingLevel = (TextView) inflatedView.findViewById( R.id.textview_text_current_charginglvl );
		this.textViewCurrentLoadingLevelAsusDock = (TextView) inflatedView.findViewById( R.id.textview_text_current_charginglvl_asusdock );
		this.textViewCurrentLoadingLevelAsusDockLabel = (TextView) inflatedView.findViewById( R.id.textview_label_current_charginglvl_asusdock );
		this.textViewCurrentChargingState = (TextView) inflatedView.findViewById( R.id.textview_text_current_chargingstate );
		this.textViewTemp = (TextView) inflatedView.findViewById( R.id.textview_text_temperature );

		// check if it can be possible that there is a additional battery dock
		boolean possibleAsusDock = false;
		if( 0 == Build.BRAND.compareToIgnoreCase( "asus" ) ) {
			Log.v( OverviewFragment.TAG, "Device brand: " + Build.BRAND );
			if( Build.DEVICE.toLowerCase().startsWith( "tf700" ) ) {
				Log.v( OverviewFragment.TAG, "Device model name: " + Build.DEVICE );
				possibleAsusDock = true;
			}
		}

		// set the visibility to invisible if no dock was found
		if( !possibleAsusDock ) {
			this.textViewCurrentLoadingLevelAsusDockLabel.setVisibility( View.INVISIBLE );
			this.textViewCurrentLoadingLevelAsusDock.setVisibility( View.INVISIBLE );
		}

		// get the current battery state and show it on the main activity
		final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( final Context context, final Intent intent ) {
				try {
					// ensure that we're not updating this receiver anymore (to save battery)
					context.unregisterReceiver( this );

					// get some important values into local variables
					final int rawlevel = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
					final int scale = intent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
					final int status = intent.getIntExtra( BatteryManager.EXTRA_STATUS, -1 );
					final float temp = (intent.getIntExtra( BatteryManager.EXTRA_TEMPERATURE, -1 )) / 10.0f;

					// get the charging state and level for the keyboard dock of the ASUS Transformer Pad series
					final int dockStatus = intent.getIntExtra( "dock_status", -1 );
					final int dockLevel = intent.getIntExtra( "dock_level", -1 );

					// do a potential level scaling (most of the times not required, but to be sure)
					int level = -1;
					if( (rawlevel >= 0) && (scale > 0) ) {
						level = (rawlevel * 100) / scale;
					}

					// set the text for the state of he main battery
					switch( status ) {
						case BatteryManager.BATTERY_STATUS_CHARGING:
							OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_charging ) );
							break;
						case BatteryManager.BATTERY_STATUS_DISCHARGING:
							OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_discharging ) );
							// OverviewFragment.this.batteryDischarging = true;
							break;
						case BatteryManager.BATTERY_STATUS_FULL:
							OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_full ) );
							break;
						default:
							OverviewFragment.this.textViewCurrentChargingState.setText( OverviewFragment.this.getString( R.string.battery_state_unknown ) );
							break;
					}

					OverviewFragment.this.textViewCurrentLoadingLevel.setText( level + "" ); // TODO
					if( dockStatus == 1 ) {
						OverviewFragment.this.textViewCurrentLoadingLevelAsusDock.setText( "-" ); // undocked
					} else {
						OverviewFragment.this.textViewCurrentLoadingLevelAsusDock.setText( dockLevel + " %" ); // TODO
					}

					final String prefUsedUnit = OverviewFragment.this.sharedPref.getString( "display.temperature_unit", "Celsius" );
					if( prefUsedUnit.compareToIgnoreCase( "celsius" ) == 0 ) {
						OverviewFragment.this.textViewTemp.setText( OverviewFragment.this.getString( R.string.textview_text_temperature_celsius, temp ) );
					} else if( prefUsedUnit.compareToIgnoreCase( "fahrenheit" ) == 0 ) {
						final float newTemp = (temp * 1.8f) + 32.0f;
						OverviewFragment.this.textViewTemp.setText( OverviewFragment.this.getString( R.string.textview_text_temperature_fahrenheit, newTemp ) );
					}
				} catch( final IllegalStateException e ) {
					Log.e( OverviewFragment.TAG, "The fragment was in an illegal state while it received the battery information. This should be handled in a different (and better way), The exception message was: ", e ); // TODO
				}
			}
		};
		final IntentFilter batteryLevelFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
		this.getActivity().registerReceiver( batteryLevelReceiver, batteryLevelFilter );

		// return the inflated view
		return inflatedView;
	}
}
