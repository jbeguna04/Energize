package com.halcyonwaves.apps.energize;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
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

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		this.setTheme( ApplicationCore.getSelectedThemeId( this.getApplicationContext() ) );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// get the handles to controls we want to modify
		this.batteryPercentage = (TextView) this.findViewById( R.id.tv_battery_pct );

		// now we can update the battery information for displaying them
		this.updateBatteryInformation();
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

	private void updateBatteryInformation() {
		final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive( final Context context, final Intent intent ) {
				context.unregisterReceiver( this );
				final int rawlevel = intent.getIntExtra( BatteryManager.EXTRA_LEVEL, -1 );
				final int scale = intent.getIntExtra( BatteryManager.EXTRA_SCALE, -1 );
				int level = -1;
				if( (rawlevel >= 0) && (scale > 0) ) {
					level = (rawlevel * 100) / scale;
				}
				BatteryStateDisplayActivity.this.batteryPercentage.setText( level + "" );
			}
		};
		final IntentFilter batteryLevelFilter = new IntentFilter( Intent.ACTION_BATTERY_CHANGED );
		this.registerReceiver( batteryLevelReceiver, batteryLevelFilter );
	}
}
