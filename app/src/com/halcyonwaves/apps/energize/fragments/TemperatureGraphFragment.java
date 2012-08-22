package com.halcyonwaves.apps.energize.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.database.BatteryStatisticsDatabaseOpenHelper;
import com.halcyonwaves.apps.energize.database.RawBatteryStatisicsTable;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphView.GraphViewStyle;
import com.jjoe64.graphview.GraphView.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class TemperatureGraphFragment extends Fragment {

	private static final String TAG = "TemperatureGraphFragment";
	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View inflatedView = inflater.inflate( R.layout.fragment_temperaturegraph, container, false );

		LineGraphView graphView = new LineGraphView( this.getActivity().getApplicationContext(), "" ) {

			@Override
			protected String formatLabel( double value, boolean isValueX ) {
				if( isValueX ) {
					SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
					return dateFormat.format( new Date( (long) value * 1000 ) );
				} else {
					return String.format( "%.1f C", value );
				}
			}
		};
		graphView.addSeries( this.getBatteryStatisticData() );
		graphView.setScrollable( true );
		graphView.setScalable( true );
		graphView.setDrawBackground( false );
		// graphView.setViewPort( ((int) (System.currentTimeMillis() / 1000L) - 86400), (int) (System.currentTimeMillis() / 1000L) );
		LinearLayout layout = (LinearLayout) inflatedView.findViewById( R.id.layout_graph_view_temperature );
		layout.addView( graphView );

		return inflatedView;
	}

	private GraphViewSeries getBatteryStatisticData() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getActivity().getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_EVENT_TIME, RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		ArrayList< GraphViewData > graphViewData = new ArrayList< GraphViewData >();

		//
		final int columnIndexEventTime = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME );
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE );

		//
		lastEntryMadeCursor.moveToFirst();
		while( !lastEntryMadeCursor.isAfterLast() ) {
			Log.v( TemperatureGraphFragment.TAG, "Found a stored temperature: " + lastEntryMadeCursor.getInt( columnIndexChargingLevel ) );
			graphViewData.add( new GraphViewData( lastEntryMadeCursor.getInt( columnIndexEventTime ), lastEntryMadeCursor.getInt( columnIndexChargingLevel ) / 10.0f ) );
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
		return new GraphViewSeries( "", new GraphViewStyle( Color.rgb( 255, 0, 0 ), 3 ), convertedDataset );
	}
}
