package com.halcyonwaves.apps.energize.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.halcyonwaves.apps.energize.BatteryStateDisplayActivity;
import com.halcyonwaves.apps.energize.R;

public class SimpleBatteryWidget extends AppWidgetProvider {

	@Override
	public void onUpdate( final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds ) {
		final int N = appWidgetIds.length;

		// Perform this loop procedure for each App Widget that belongs to this provider
		for( int i = 0; i < N; i++ ) {
			final int appWidgetId = appWidgetIds[ i ];

			//
			final Intent intent = new Intent( context, BatteryStateDisplayActivity.class );

			//
			final PendingIntent pendingIntent = PendingIntent.getActivity( context, 0, intent, 0 );

			// Get the layout for the App Widget and attach an on-click listener to the button
			final RemoteViews views = new RemoteViews( context.getPackageName(), R.layout.widget_simplebattery );
			views.setOnClickPendingIntent( R.id.widget_simplewidget_layout, pendingIntent );

			// Tell the AppWidgetManager to perform an update on the current App Widget
			appWidgetManager.updateAppWidget( appWidgetId, views );
		}

		//
		super.onUpdate( context, appWidgetManager, appWidgetIds );
	}
}
