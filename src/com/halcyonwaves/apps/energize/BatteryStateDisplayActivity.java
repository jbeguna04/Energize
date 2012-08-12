/**
 * Energize - An Android battery monitor Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.halcyonwaves.apps.energize;

import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/**
 * This class defines the behavior of the first activity the user sees after he
 * or she started the application through the launcher entry or by clicking the
 * item in the status bar of the device.
 * 
 * @author Tim Huetz
 */
public class BatteryStateDisplayActivity extends Activity {

	private static final String PRIVATE_PREF = "com.halcyonwaves.apps.energize";
	private static final String VERSION_KEY = "version_number";

	private void init() {
		SharedPreferences sharedPref = getSharedPreferences( PRIVATE_PREF, Context.MODE_PRIVATE );
		int currentVersionNumber = 0;

		int savedVersionNumber = sharedPref.getInt( VERSION_KEY, 0 );

		try {
			PackageInfo pi = getPackageManager().getPackageInfo( getPackageName(), 0 );
			currentVersionNumber = pi.versionCode;
		} catch( Exception e ) {
		}

		if( currentVersionNumber > savedVersionNumber ) {
			showWhatsNewDialog();

			Editor editor = sharedPref.edit();

			editor.putInt( VERSION_KEY, currentVersionNumber );
			editor.commit();
		}
	}

	private void showWhatsNewDialog() {
		LayoutInflater inflater = LayoutInflater.from( this );

		View view = inflater.inflate( R.layout.dialog_whatsnew, null );

		AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setView( view ).setTitle( R.string.dialog_title_whatsnew ).setPositiveButton( android.R.string.ok, new OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		this.setTheme( ApplicationCore.getSelectedThemeId( this.getApplicationContext() ) );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// check if the service is running, if not start it
		if( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		//
		this.init();
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
