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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.LineGraphView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * This class defines the behavior of the first activity the user sees after he
 * or she started the application through the launcher entry or by clicking the
 * item in the status bar of the device.
 * 
 * @author Tim Huetz
 */
public class BatteryStateDisplayActivity extends Activity {

	private static final String VERSION_KEY = "version_number";
	private TextView textViewCurrentLoadingLevel = null;
	private TextView textViewCurrentChargingState = null;

	private void init() {
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getApplicationContext() );
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

		// get the handles to some important controls
		this.textViewCurrentLoadingLevel = (TextView)this.findViewById( R.id.textview_text_current_charginglvl );
		this.textViewCurrentChargingState = (TextView)this.findViewById( R.id.textview_text_current_chargingstate );
		this.textViewCurrentChargingState.setText( "AHAHAH" ); // TODO
		this.textViewCurrentLoadingLevel.setText( "AHAHAH" ); // TODO
		
		// check if the service is running, if not start it
		if( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		// render the battery graph and initialize the rest of the application
		this.showBatteryGraph();
		this.init();
	}

	private GraphViewSeries getBatteryStatisticData() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_EVENT_TIME, RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		ArrayList< GraphViewData > graphViewData = new ArrayList< GraphView.GraphViewData >();

		//
		final int columnIndexEventTime = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME );
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL );

		//
		lastEntryMadeCursor.moveToFirst();
		while( !lastEntryMadeCursor.isAfterLast() ) {
			graphViewData.add( new GraphViewData( lastEntryMadeCursor.getInt( columnIndexEventTime ), lastEntryMadeCursor.getInt( columnIndexChargingLevel ) ) );
			lastEntryMadeCursor.moveToNext();
		}

		// close our connection to the database
		lastEntryMadeCursor.close();
		lastEntryMadeCursor = null;
		batteryDbOpenHelper.close();
		batteryStatisticsDatabase = null;
		batteryDbOpenHelper = null;

		// convert the array to an array and return the view series
		if( graphViewData.size() == 0 ) {
			graphViewData.add( new GraphViewData( 0.0, 0.0 ) );
		}
		GraphViewData convertedDataset[] = new GraphViewData[ graphViewData.size() ];
		graphViewData.toArray( convertedDataset );
		return new GraphViewSeries( convertedDataset );
	}

	private String[] getBatteryStatisticVerticalLabels() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		//
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL );
		int maxLevel = 0;
		int minLevel = 100;

		//
		lastEntryMadeCursor.moveToFirst();
		while( !lastEntryMadeCursor.isAfterLast() ) {
			if( lastEntryMadeCursor.getInt( columnIndexChargingLevel ) > maxLevel ) {
				maxLevel = lastEntryMadeCursor.getInt( columnIndexChargingLevel );
			}
			if( lastEntryMadeCursor.getInt( columnIndexChargingLevel ) < minLevel ) {
				minLevel = lastEntryMadeCursor.getInt( columnIndexChargingLevel );
			}
			lastEntryMadeCursor.moveToNext();
		}

		// close our connection to the database
		lastEntryMadeCursor.close();
		lastEntryMadeCursor = null;
		batteryDbOpenHelper.close();
		batteryStatisticsDatabase = null;
		batteryDbOpenHelper = null;

		// convert the array to an array and return the view series
		return new String[] { maxLevel + "%", ((int) Math.round( maxLevel / 2.0 )) + "%", minLevel + "%" };
	}

	private void showBatteryGraph() {
		GraphView graphView = new LineGraphView( this, this.getString( R.string.graph_title_batterystatistics ) ) {

			@Override
			protected String formatLabel( double value, boolean isValueX ) {
				if( isValueX ) {
					SimpleDateFormat dateFormat = new SimpleDateFormat( "dd.MM, H:mm" );
					return dateFormat.format( new Date( (long) value * 1000 ) );
				} else
					return super.formatLabel( value, isValueX ); // let the y-value be normal-formatted
			}
		};
		graphView.addSeries( this.getBatteryStatisticData() );
		graphView.setVerticalLabels( this.getBatteryStatisticVerticalLabels() );
		graphView.setScrollable( true );
		graphView.setScalable( true );
		//graphView.setViewPort( ((int) (System.currentTimeMillis() / 1000L) - 86400), (int) (System.currentTimeMillis() / 1000L) );
		LinearLayout layout = (LinearLayout) findViewById( R.id.layout_graph_view );
		layout.addView( graphView );
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
