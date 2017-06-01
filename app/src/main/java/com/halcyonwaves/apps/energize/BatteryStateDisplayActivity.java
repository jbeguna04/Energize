package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.halcyonwaves.apps.energize.dialogs.ChangeLogDialog;
import com.halcyonwaves.apps.energize.fragments.BatteryCapacityGraphFragment;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;
import com.halcyonwaves.apps.energize.fragments.TemperatureGraphFragment;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class BatteryStateDisplayActivity extends FragmentActivity {
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] mAppCategories;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.activity_batterystatedisplay );

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		//
		this.mTitle = this.mDrawerTitle = this.getTitle();
		this.mAppCategories = this.getResources().getStringArray( R.array.app_categories_array );
		this.mDrawerLayout = (DrawerLayout) this.findViewById( R.id.drawer_layout );
		this.mDrawerList = (ListView) this.findViewById( R.id.left_drawer );

		// set a custom shadow that overlays the main content when the drawer opens
		this.mDrawerLayout.setDrawerShadow( R.drawable.drawer_shadow, GravityCompat.START );

		// set up the drawer's list view with items and click listener
		this.mDrawerList.setAdapter( new ArrayAdapter<String>( this, R.layout.drawer_list_item, this.mAppCategories ) );
		this.mDrawerList.setOnItemClickListener( new DrawerItemClickListener() );

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		this.mDrawerToggle = new ActionBarDrawerToggle( this, this.mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close ) {
			public void onDrawerClosed( View view ) {
				BatteryStateDisplayActivity.this.getActionBar().setTitle( BatteryStateDisplayActivity.this.mTitle );
				BatteryStateDisplayActivity.this.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}

			public void onDrawerOpened( View drawerView ) {
				BatteryStateDisplayActivity.this.getActionBar().setTitle( BatteryStateDisplayActivity.this.mDrawerTitle );
				BatteryStateDisplayActivity.this.invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
			}
		};
		this.mDrawerLayout.setDrawerListener( this.mDrawerToggle );

		// however, if we're being restored from a previous state, then we don't need to do anything and should return or else
		// we could end up with overlapping fragments
		if ( savedInstanceState == null ) {
			this.selectItem( 0 );
		}

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
	public boolean onPrepareOptionsMenu( Menu menu ) {
		final boolean drawerOpen = this.mDrawerLayout.isDrawerOpen( this.mDrawerList );
		return super.onPrepareOptionsMenu( menu );
	}

	@Override
	protected void onPostCreate( Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		this.mDrawerToggle.syncState(); // sync. the toggle state after onRestoreInstanceState has occurred.
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig ) {
		super.onConfigurationChanged( newConfig );
		this.mDrawerToggle.onConfigurationChanged( newConfig ); // pass any configuration change to the drawer toggles
	}

	@Override
	public void setTitle( CharSequence title ) {
		this.mTitle = title;
		this.getActionBar().setTitle( this.mTitle );
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		// the action bar home/up action should open or close the drawer. ActionBarDrawerToggle will take care of this.
		if ( this.mDrawerToggle.onOptionsItemSelected( item ) ) {
			return true;
		}
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
		this.getMenuInflater().inflate( R.menu.activity_main_menu, menu );
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

	private void selectItem( int position ) {
		// check that the activity is using the layout version with the fragment_container FrameLayout (the one-pane layout)
		if ( this.findViewById( R.id.fragment_container ) != null ) {

			// create a new Fragment to be placed in the activity layout
			Fragment firstFragment = null;
			switch ( position ) {
				case 1:
					firstFragment = new BatteryCapacityGraphFragment();
					break;
				case 2:
					firstFragment = new TemperatureGraphFragment();
					break;
				case 0:
				default:
					firstFragment = new OverviewFragment();
					break;
			}

			// in case this activity was started with special instructions from an  Intent, pass the Intent's extras to the fragment as arguments
			firstFragment.setArguments( this.getIntent().getExtras() );

			// add the fragment to the 'fragment_container' FrameLayout
			this.getFragmentManager().beginTransaction().replace( R.id.fragment_container, firstFragment ).commit();
		}

		// update selected item and title, then close the drawer
		this.mDrawerList.setItemChecked( position, true );
		this.setTitle( this.mAppCategories[ position ] );
		this.mDrawerLayout.closeDrawer( mDrawerList );
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick( AdapterView<?> parent, View view, int position, long id ) {
			BatteryStateDisplayActivity.this.selectItem( position );
		}
	}
}