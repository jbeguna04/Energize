package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.FrameLayout;

import com.halcyonwaves.apps.energize.dialogs.AboutDialog;
import com.halcyonwaves.apps.energize.dialogs.ChangeLogDialog;
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

	private CustomViewPager usedPager = null;

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		// setup the layout of the main activity
		final FrameLayout overlayFramelayout = new FrameLayout( this.getApplicationContext() );
		View overlayView = this.getLayoutInflater().inflate( R.layout.activity_batterystatedisplay, overlayFramelayout, false );
		overlayFramelayout.addView( overlayView );

		//
		View overlayViewHelp = this.getLayoutInflater().inflate( R.layout.overlay_help_mainscreen, overlayFramelayout, false );
		overlayFramelayout.addView( overlayViewHelp );

		//
		this.setContentView( overlayFramelayout );

		// check if the service is running, if not start it
		if( !ApplicationCore.isServiceRunning( this, MonitorBatteryStateService.class.getName() ) ) {
			Log.v( "BatteryStateDisplayActivity", "Monitoring service is not running, starting it..." );
			this.getApplicationContext().startService( new Intent( this.getApplicationContext(), MonitorBatteryStateService.class ) );
		}

		// set the pager with an adapter (not available in the tablet layout)
		this.usedPager = (CustomViewPager) this.findViewById( R.id.vp_fragment_pager );
		if( null != this.usedPager ) {
			this.usedPager.setAdapter( new MainFragmentPagerAdapter( this.getApplicationContext(), this.getSupportFragmentManager() ) );

			// bind the title indicator to the adapter
			final TitlePageIndicator titleIndicator = (TitlePageIndicator) this.findViewById( R.id.vp_fragment_titles );
			titleIndicator.setViewPager( this.usedPager );
		}

		// show the changelog dialog
		ChangeLogDialog changeDlg = new ChangeLogDialog( this );
		changeDlg.show();
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
