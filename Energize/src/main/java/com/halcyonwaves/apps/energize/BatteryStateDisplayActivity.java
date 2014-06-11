package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.halcyonwaves.apps.energize.dialogs.ChangeLogDialog;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class BatteryStateDisplayActivity extends FragmentActivity {
	private static final String OPENED_KEY = "OPENED_KEY";
	private int selection = 0;
	private int oldSelection = -1;
	private String[] names = null;
	private String[] classes = null;
	private SharedPreferences prefs = null;
	private Boolean opened = null;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// start the first fragment we want to see after the application has started
		FragmentTransaction transaction = this.getFragmentManager().beginTransaction();
		OverviewFragment newFragment = new OverviewFragment();
		transaction.replace( R.id.main, newFragment );
		transaction.addToBackStack( null );
		transaction.commit();

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		// enable the action bar button for navigation
		this.getActionBar().setDisplayHomeAsUpEnabled( true );
		this.getActionBar().setHomeButtonEnabled( true );

		// check if the service is running, if not start it
		if ( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		// show the changelog dialog
		ChangeLogDialog changeDlg = new ChangeLogDialog( this );
		changeDlg.show();
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		switch ( item.getItemId() ) {
			case R.id.menu_preferences:
				Intent settingsIntent = new Intent( this, SettingsActivity.class );
				this.startActivity( settingsIntent );
				return true;
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		this.getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	private void showWhatsNewDialog() {
		final LayoutInflater inflater = LayoutInflater.from( this );

		final View view = inflater.inflate( R.layout.dialog_whatsnew, null );

		final WebView changelogWebview = (WebView) view.findViewById( R.id.webview_whatsnew );
		changelogWebview.loadUrl( "file:///android_asset/html/changelog.html" );

		final AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setView( view ).setTitle( R.string.dialog_title_whatsnew ).setPositiveButton( android.R.string.ok, new DialogInterface.OnClickListener() {

			public void onClick( final DialogInterface dialog, final int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}
}