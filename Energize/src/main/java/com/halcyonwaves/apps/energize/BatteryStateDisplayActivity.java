package com.halcyonwaves.apps.energize;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.halcyonwaves.apps.energize.dialogs.ChangeLogDialog;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class BatteryStateDisplayActivity extends FragmentActivity {
	private static final String OPENED_KEY = "OPENED_KEY";
	private int selection = 0;
	private int oldSelection = -1;
	private String[] names = null;
	private String[] classes = null;
	private ActionBarDrawerToggle drawerToggle = null;
	private DrawerLayout drawer = null;
	private ListView navList = null;
	private SharedPreferences prefs = null;
	private Boolean opened = null;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );

		// set the default preferences
		PreferenceManager.setDefaultValues( this, R.xml.pref_unified, false );

		setContentView( R.layout.activity_batterystatedisplay );
		names = getResources().getStringArray( R.array.navigation_titles );
		classes = getResources().getStringArray( R.array.navigation_classes );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, names );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( adapter );
		drawerToggle = new ActionBarDrawerToggle( this, drawer, R.drawable.ic_drawer, R.string.open, R.string.close ) {
			@Override
			public void onDrawerClosed( View drawerView ) {
				super.onDrawerClosed( drawerView );
				updateContent();
				invalidateOptionsMenu();
				if ( opened != null && opened == false ) {
					opened = true;
					if ( prefs != null ) {
						Editor editor = prefs.edit();
						editor.putBoolean( OPENED_KEY, true );
						editor.apply();
					}
				}
			}

			@Override
			public void onDrawerOpened( View drawerView ) {
				super.onDrawerOpened( drawerView );
				getActionBar().setTitle( R.string.app_name );
				invalidateOptionsMenu();
			}
		};
		drawer.setDrawerListener( drawerToggle );

		navList.setOnItemClickListener( new OnItemClickListener() {

			@Override
			public void onItemClick( AdapterView<?> parent, View view, final int pos, long id ) {
				selection = pos;
				drawer.closeDrawer( navList );
			}
		} );

		updateContent();
		getActionBar().setDisplayHomeAsUpEnabled( true );
		getActionBar().setHomeButtonEnabled( true );

		new Thread( new Runnable() {

			@Override
			public void run() {
				prefs = getPreferences( MODE_PRIVATE );
				opened = prefs.getBoolean( OPENED_KEY, false );
				if ( opened == false ) {
					drawer.openDrawer( navList );
				}
			}

		} ).start();

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
	protected void onPostCreate( Bundle savedInstanceState ) {
		super.onPostCreate( savedInstanceState );
		drawerToggle.syncState();
	}

	@Override
	public boolean onOptionsItemSelected( MenuItem item ) {
		if ( drawerToggle.onOptionsItemSelected( item ) ) {
			return true;
		}
		return super.onOptionsItemSelected( item );
	}

	@Override
	public boolean onCreateOptionsMenu( Menu menu ) {
		this.getMenuInflater().inflate( R.menu.menu_main, menu );
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu( Menu menu ) {
		if ( drawer != null && navList != null ) {
			MenuItem item = null; //menu.findItem(R.id.add);
			if ( item != null ) {
				item.setVisible( !drawer.isDrawerOpen( navList ) );
			}
		}
		return super.onPrepareOptionsMenu( menu );
	}

	private void updateContent() {
		getActionBar().setTitle( names[ selection ] );
		if ( selection != oldSelection ) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace( R.id.main, Fragment.instantiate( BatteryStateDisplayActivity.this, classes[ selection ] ) );
			tx.commit();
			oldSelection = selection;
		}
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