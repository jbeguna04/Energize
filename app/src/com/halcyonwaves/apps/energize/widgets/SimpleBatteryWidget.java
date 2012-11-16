package com.halcyonwaves.apps.energize.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.RemoteViews;

import com.halcyonwaves.apps.energize.BatteryStateDisplayActivity;
import com.halcyonwaves.apps.energize.R;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class SimpleBatteryWidget extends AppWidgetProvider {

	private final static String TAG = "SimpleBatteryWidget";

	@Override
	public void onUpdate( final Context context, final AppWidgetManager appWidgetManager, final int[] appWidgetIds ) {
		final int N = appWidgetIds.length;

		// try to get a connection to the service
		IBinder service = this.peekService( context.getApplicationContext(), new Intent( context.getApplicationContext(), MonitorBatteryStateService.class ) );
		if( null != service ) {
			try {
				// get a messenger to the service and prepare the update request message
				Messenger serviceMessenger = new Messenger( service );
				Message msg = Message.obtain( null, MonitorBatteryStateService.MSG_UPDATE_WIDGETS );

				// send the update request to the service
				serviceMessenger.send( msg );
			} catch( RemoteException e ) {
				Log.e( SimpleBatteryWidget.TAG, "Failed to ask the service to update all widgets!" );
			}
		}

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
