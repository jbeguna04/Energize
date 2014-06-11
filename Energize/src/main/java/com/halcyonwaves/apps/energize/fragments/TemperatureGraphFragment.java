package com.halcyonwaves.apps.energize.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.jjoe64.graphview.GraphViewSeries.GraphViewSeriesStyle;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TemperatureGraphFragment extends Fragment {

	private static final String TAG = "TemperatureGraphFragment";
	private SharedPreferences sharedPref = null;
	private LineGraphView graphView = null;
	private boolean seriesSet = false;

	private Pair<GraphViewSeries, Long> getBatteryStatisticData() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getActivity().getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[]{ RawBatteryStatisicsTable.COLUMN_EVENT_TIME, RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		final ArrayList<GraphViewData> graphViewData = new ArrayList<GraphViewData>();

		//
		final int columnIndexEventTime = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME );
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE );

		//
		TemperatureUnit usedUnit = TemperatureUnit.TemperatureUnitCelsius;
		final String prefUsedUnit = TemperatureGraphFragment.this.sharedPref.getString( "display.temperature_unit", "Celsius" );
		if ( prefUsedUnit.compareToIgnoreCase( "fahrenheit" ) == 0 ) {
			usedUnit = TemperatureUnit.TemperatureUnitFahrenheit;
		} else if ( prefUsedUnit.compareToIgnoreCase( "kelvin" ) == 0 ) {
			usedUnit = TemperatureUnit.TemperatureUnitKelvin;
		}

		//
		lastEntryMadeCursor.moveToFirst();
		Long oldtestTime = Long.MAX_VALUE;
		while ( !lastEntryMadeCursor.isAfterLast() ) {
			Log.v( TemperatureGraphFragment.TAG, "Found a stored temperature: " + lastEntryMadeCursor.getInt( columnIndexChargingLevel ) );
			final int currentTime = lastEntryMadeCursor.getInt( columnIndexEventTime );
			if ( currentTime < oldtestTime ) {
				oldtestTime = (long) currentTime;
			}
			switch ( usedUnit ) {
				case TemperatureUnitCelsius:
					graphViewData.add( new GraphViewData( currentTime, lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f ) );
					break;
				case TemperatureUnitFahrenheit:
					graphViewData.add( new GraphViewData( currentTime, ( ( lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f ) * 1.8f ) + 32.0f ) );
					break;
				case TemperatureUnitKelvin:
					graphViewData.add( new GraphViewData( currentTime, ( lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f ) + 273.15f ) );
					break;
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
		if ( graphViewData.size() == 0 ) {
			graphViewData.add( new GraphViewData( 0.0, 0.0 ) );
		}
		final GraphViewData convertedDataset[] = new GraphViewData[ graphViewData.size() ];
		graphViewData.toArray( convertedDataset );
		return new Pair<GraphViewSeries, Long>( new GraphViewSeries( "", new GraphViewSeriesStyle( Color.rgb( 255, 0, 0 ), 3 ), convertedDataset ), oldtestTime );
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		this.sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getActivity().getApplicationContext() );
		final View inflatedView = inflater.inflate( R.layout.fragment_temperaturegraph, container, false );

		this.graphView = new LineGraphView( this.getActivity().getApplicationContext(), "" ) {

			@Override
			protected String formatLabel( final double value, final boolean isValueX ) {
				if ( isValueX ) {
					final SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
					return dateFormat.format( new Date( (long) value * 1000 ) );
				} else {
					//
					TemperatureUnit usedUnit = TemperatureUnit.TemperatureUnitCelsius;
					final String prefUsedUnit = TemperatureGraphFragment.this.sharedPref.getString( "display.temperature_unit", "Celsius" );
					if ( prefUsedUnit.compareToIgnoreCase( "fahrenheit" ) == 0 ) {
						usedUnit = TemperatureUnit.TemperatureUnitFahrenheit;
					} else if ( prefUsedUnit.compareToIgnoreCase( "kelvin" ) == 0 ) {
						usedUnit = TemperatureUnit.TemperatureUnitKelvin;
					}

					//
					switch ( usedUnit ) {
						case TemperatureUnitCelsius:
							return TemperatureGraphFragment.this.getString( R.string.textview_text_temperature_celsius, value );
						case TemperatureUnitFahrenheit:
							return TemperatureGraphFragment.this.getString( R.string.textview_text_temperature_fahrenheit, value );
						case TemperatureUnitKelvin:
							return TemperatureGraphFragment.this.getString( R.string.textview_text_temperature_kelvin, value );
					}

					//
					return "N/A";
				}
			}
		};
		this.graphView.setScrollable( true );
		this.graphView.setScalable( true );
		this.graphView.setDrawBackground( false );
		GraphViewStyle gws = this.graphView.getGraphViewStyle();
		gws.setHorizontalLabelsColor( Color.BLACK );
		gws.setVerticalLabelsColor( Color.BLACK );
		this.graphView.setGraphViewStyle( gws );
		this.updateGraph();
		final LinearLayout layout = (LinearLayout) inflatedView.findViewById( R.id.layout_graph_view_temperature );
		layout.addView( this.graphView );

		return inflatedView;
	}

	@Override
	public void onResume() {
		super.onResume();
		this.updateGraph();
	}

	private void updateGraph() {
		final Pair<GraphViewSeries, Long> dataSet = this.getBatteryStatisticData();
		if ( this.seriesSet ) {
			//TODO: this.graphView.removeSeries( 0 );
		}
		final Long currentTime = System.currentTimeMillis() / 1000L;
		this.graphView.addSeries( dataSet.first );
		if ( ( dataSet.second + 86400L ) < currentTime ) {
			this.graphView.setViewPort( ( currentTime - 86400 ), 86400 );
		}
		this.seriesSet = true;
	}

	private enum TemperatureUnit {
		TemperatureUnitCelsius,
		TemperatureUnitFahrenheit,
		TemperatureUnitKelvin
	}
}
