package com.halcyonwaves.apps.energize.fragments;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.jjoe64.graphview.LineGraphView;

public class BatteryCapacityGraphFragment extends Fragment {

	private Pair< GraphViewSeries, Long > getBatteryStatisticData() {
		BatteryStatisticsDatabaseOpenHelper batteryDbOpenHelper = new BatteryStatisticsDatabaseOpenHelper( this.getActivity().getApplicationContext() );
		SQLiteDatabase batteryStatisticsDatabase = batteryDbOpenHelper.getReadableDatabase();
		Cursor lastEntryMadeCursor = batteryStatisticsDatabase.query( RawBatteryStatisicsTable.TABLE_NAME, new String[] { RawBatteryStatisicsTable.COLUMN_EVENT_TIME, RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL }, null, null, null, null, RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " ASC" );

		final ArrayList< GraphViewData > graphViewData = new ArrayList< GraphViewData >();

		//
		final int columnIndexEventTime = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_EVENT_TIME );
		final int columnIndexChargingLevel = lastEntryMadeCursor.getColumnIndex( RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL );

		//
		lastEntryMadeCursor.moveToFirst();
		Long oldtestTime = Long.MAX_VALUE;
		while( !lastEntryMadeCursor.isAfterLast() ) {
			final int currentTime = lastEntryMadeCursor.getInt( columnIndexEventTime );
			if( currentTime < oldtestTime ) {
				oldtestTime = (long) currentTime;
			}
			graphViewData.add( new GraphViewData( currentTime, lastEntryMadeCursor.getInt( columnIndexChargingLevel ) ) );
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
		return new Pair< GraphViewSeries, Long >( new GraphViewSeries( convertedDataset ), oldtestTime );
	}

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		final View inflatedView = inflater.inflate( R.layout.fragment_batterycapacitygraph, container, false );

		final LineGraphView graphView = new LineGraphView( this.getActivity().getApplicationContext(), "" ) {

			@Override
			protected String formatLabel( final double value, final boolean isValueX ) {
				if( isValueX ) {
					final SimpleDateFormat dateFormat = new SimpleDateFormat( "HH:mm" );
					return dateFormat.format( new Date( (long) value * 1000 ) );
				} else {
					return super.formatLabel( value, isValueX ); // let the y-value be normal-formatted
				}
			}
		};
		final Pair< GraphViewSeries, Long > dataSet = this.getBatteryStatisticData();
		final Long currentTime = System.currentTimeMillis() / 1000L;
		graphView.addSeries( dataSet.first );
		graphView.setVerticalLabels( new String[] { "100%", "90%", "80%", "70%", "60%", "50%", "40%", "30%", "20%", "10%", "0%" } );
		graphView.setScrollable( true );
		graphView.setScalable( true );
		graphView.setManualYAxis( true );
		graphView.setDrawBackground( false );
		graphView.setManualYAxisBounds( 100.0, 0.0 );
		if( (dataSet.second + 86400L) < currentTime ) {
			graphView.setViewPort( (currentTime - 86400), 86400 );
		}
		final LinearLayout layout = (LinearLayout) inflatedView.findViewById( R.id.layout_graph_view );
		layout.addView( graphView );

		return inflatedView;
	}
}
