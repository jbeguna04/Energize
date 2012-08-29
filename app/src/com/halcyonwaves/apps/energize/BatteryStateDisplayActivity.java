package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

	private void init() {
		final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences( this.getApplicationContext() );
		int currentVersionNumber = 0;

		final int savedVersionNumber = sharedPref.getInt( BatteryStateDisplayActivity.VERSION_KEY, 0 );

		try {
			final PackageInfo pi = this.getPackageManager().getPackageInfo( this.getPackageName(), 0 );
			currentVersionNumber = pi.versionCode;
		} catch( final Exception e ) {
		}

		if( currentVersionNumber > savedVersionNumber ) {
			this.showWhatsNewDialog();

			final Editor editor = sharedPref.edit();

			editor.putInt( BatteryStateDisplayActivity.VERSION_KEY, currentVersionNumber );
			editor.commit();
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
		final ViewPager pager = (ViewPager) this.findViewById( R.id.vp_fragment_pager );
		if( null != pager ) {
			pager.setAdapter( new MainFragmentPagerAdapter( this.getApplicationContext(), this.getSupportFragmentManager() ) );

			// bind the title indicator to the adapter
			final TitlePageIndicator titleIndicator = (TitlePageIndicator) this.findViewById( R.id.vp_fragment_titles );
			titleIndicator.setViewPager( pager );
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
			case R.id.menu_showapplicense:
				final Intent i = new Intent( Intent.ACTION_VIEW );
				i.setData( Uri.parse( "http://www.gnu.org/copyleft/gpl.html" ) );
				this.startActivity( i );
				return true;
			case R.id.menu_show3rdpartylicense:
				this.showAppIconLicensePreference();
				return true;
			default:
				return false;
		}
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

		final AlertDialog.Builder builder = new AlertDialog.Builder( this );

		builder.setView( view ).setTitle( R.string.dialog_title_whatsnew ).setPositiveButton( android.R.string.ok, new OnClickListener() {

			public void onClick( final DialogInterface dialog, final int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}
}
