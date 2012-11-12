package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import com.halcyonwaves.apps.energize.dialogs.AboutDialog;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;
import com.viewpagerindicator.TitlePageIndicator;

/**
 * This class defines the behavior of the first activity the user sees after he
 * or she started the application through the launcher entry or by clicking the
 * item in the status bar of the device.
 * 
 * @author Tim Huetz
 */
public class BatteryStateDisplayActivity extends FragmentActivity {

	private static final String VERSION_KEY = "version_number";

	private CustomViewPager usedPager = null;

	private void init() {
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getApplicationContext() );
		int currentVersionNumber = 0;

		// get the version number on which the "What's new" dialog was show the last time
		final int savedVersionNumber = sharedPref.getInt( BatteryStateDisplayActivity.VERSION_KEY, 0 );

		// get the current version number of the app
		try {
			final PackageInfo pi = this.getPackageManager().getPackageInfo( this.getPackageName(), 0 );
			currentVersionNumber = pi.versionCode;
		} catch( final Exception e ) {
		}

		// if the app was updated, show the "What's new" dialog
		if( currentVersionNumber > savedVersionNumber ) {
			// show the dialog
			this.showWhatsNewDialog();

			// get write access to the application preferences
			Editor editor = sharedPref.edit();

			// update the default estimation algorithm if a version below 0.8.3 was used:
			if( savedVersionNumber < 83 ) {
				editor.putString( "batstatistics.usedestimator", "LastNChangeEstimate" );
			}

			// update the field which stores the last time the dialog was shown
			editor.putInt( BatteryStateDisplayActivity.VERSION_KEY, currentVersionNumber );

			// commit all changes and close the editor again
			editor.commit();
			editor = null;
		}
	}

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		// set the theme of the activity and setup its layout
		this.setTheme( ApplicationCore.getSelectedThemeId( this.getApplicationContext() ) );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// check if the service is running, if not start it
		if( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		// set the pager with an adapter (not availbale in the tablet layout)
		this.usedPager = (CustomViewPager) this.findViewById( R.id.vp_fragment_pager );
		if( null != this.usedPager ) {
			this.usedPager.setAdapter( new MainFragmentPagerAdapter( this.getApplicationContext(), this.getSupportFragmentManager() ) );

			// bind the title indicator to the adapter
			final TitlePageIndicator titleIndicator = (TitlePageIndicator) this.findViewById( R.id.vp_fragment_titles );
			titleIndicator.setViewPager( this.usedPager );
		}

		// do the rest of the initialization of the main dialog
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
			case R.id.menu_about:
				this.showAboutDialog();
				return true;
			case R.id.menu_show3rdpartylicense:
				this.showAppIconLicensePreference();
				return true;
			case R.id.menu_lockscreen:
				this.usedPager.togglePagingEnabled();
				return true;
			default:
				return false;
		}
	}

	private void showAboutDialog() {
		final AboutDialog aboutDialog = new AboutDialog();
		aboutDialog.show( this.getFragmentManager(), "fragment_about" );
	}

	private void showAppIconLicensePreference() {
		final LayoutInflater inflater = LayoutInflater.from( this );

		final View view = inflater.inflate( R.layout.dialog_license_appicon, null );

		final AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setView( view ).setTitle( R.string.dialog_title_appiconlicense ).setPositiveButton( android.R.string.ok, new OnClickListener() {

			public void onClick( final DialogInterface dialog, final int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}

	private void showWhatsNewDialog() {
		final LayoutInflater inflater = LayoutInflater.from( this );

		final View view = inflater.inflate( R.layout.dialog_whatsnew, null );

		final WebView changelogWebview = (WebView) view.findViewById( R.id.webview_whatsnew );
		changelogWebview.loadUrl( "file:///android_asset/html/changelog.html" );

		final AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setView( view ).setTitle( R.string.dialog_title_whatsnew ).setPositiveButton( android.R.string.ok, new OnClickListener() {

			public void onClick( final DialogInterface dialog, final int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}
}
