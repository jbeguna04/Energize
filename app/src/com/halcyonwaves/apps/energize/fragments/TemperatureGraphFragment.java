package com.halcyonwaves.apps.energize.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewSeries.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

public class TemperatureGraphFragment extends Fragment {

	private static final String TAG = "TemperatureGraphFragment";
	private SharedPreferences sharedPref = null;

	private Pair< GraphViewSeries, Long > getBatteryStatisticData() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getActivity().getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_EVENT_TIME, RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		final ArrayList< GraphViewData > graphViewData = new ArrayList< GraphViewData >();

		//
		final int columnIndexEventTime = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME );
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE );

		//
		boolean fahrenheitInsteadOfCelsius = false;
		final String prefUsedUnit = TemperatureGraphFragment.this.sharedPref.getString( "display.temperature_unit", "Celsius" );
		if( prefUsedUnit.compareToIgnoreCase( "fahrenheit" ) == 0 ) {
			fahrenheitInsteadOfCelsius = true;
		}

		//
		lastEntryMadeCursor.moveToFirst();
		Long oldtestTime = Long.MAX_VALUE;
		while( !lastEntryMadeCursor.isAfterLast() ) {
			Log.v( TemperatureGraphFragment.TAG, "Found a stored temperature: " + lastEntryMadeCursor.getInt( columnIndexChargingLevel ) );
			final int currentTime = lastEntryMadeCursor.getInt( columnIndexEventTime );
			if( currentTime < oldtestTime ) {
				oldtestTime = (long) currentTime;
			}
			if( !fahrenheitInsteadOfCelsius ) {
				graphViewData.add( new GraphViewData( currentTime, lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f ) );
			} else {
				graphViewData.add( new GraphViewData( currentTime, ((lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f) * 1.8f) + 32.0f ) );
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
		if( graphViewData.size() == 0 ) {
			graphViewData.add( new GraphViewData( 0.0, 0.0 ) );
		}
		final GraphViewData convertedDataset[] = new GraphViewData[ graphViewData.size() ];
		graphViewData.toArray( convertedDataset );
		return new Pair< GraphViewSeries, Long >( new GraphViewSeries( "", new GraphViewStyle( Color.rgb( 255, 0, 0 ), 3 ), convertedDataset ), oldtestTime );
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getActivity().getApplicationContext() );
		final View inflatedView = inflater.inflate( R.layout.fragment_temperaturegraph, container, false );

		final LineGraphView graphView = new LineGraphView( this.getActivity().getApplicationContext(), "" ) {

			@Override
			protected String formatLabel( final double value, final boolean isValueX ) {
				if( isValueX ) {
					final SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
					return dateFormat.format( new Date( (long) value * 1000 ) );
				} else {
					final String prefUsedUnit = TemperatureGraphFragment.this.sharedPref.getString( "display.temperature_unit", "Celsius" );
					if( prefUsedUnit.compareToIgnoreCase( "celsius" ) == 0 ) {
						return TemperatureGraphFragment.this.getString( R.string.textview_text_temperature_celsius, value );
					} else if( prefUsedUnit.compareToIgnoreCase( "fahrenheit" ) == 0 ) {
						return TemperatureGraphFragment.this.getString( R.string.textview_text_temperature_fahrenheit, value );
					} else {
						return "N/A";
					}
				}
			}
		};
		final Pair< GraphViewSeries, Long > dataSet = this.getBatteryStatisticData();
		final Long currentTime = System.currentTimeMillis() / 1000L;
		graphView.addSeries( dataSet.first );
		graphView.setScrollable( true );
		graphView.setScalable( true );
		graphView.setDrawBackground( false );
		if( (dataSet.second + 86400L) < currentTime ) {
			graphView.setViewPort( (currentTime - 86400), 86400 );
		}
		final LinearLayout layout = (LinearLayout) inflatedView.findViewById( R.id.layout_graph_view_temperature );
		layout.addView( graphView );

		return inflatedView;
	}
}
