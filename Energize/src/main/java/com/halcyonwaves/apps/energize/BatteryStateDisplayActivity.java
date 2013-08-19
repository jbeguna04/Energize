package com.halcyonwaves.apps.energize;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.halcyonwaves.apps.energize.R;

public class BatteryStateDisplayActivity extends FragmentActivity {
	private int selection = 0;
	private int oldSelection = -1;
	private String[] names = null;
	private String[] classes = null;
	private ActionBarDrawerToggle drawerToggle = null;
	private DrawerLayout drawer = null;
	private ListView navList = null;

	@Override
	protected void onCreate( Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		setContentView( R.layout.activity_batterystatedisplay );
		names = getResources().getStringArray( R.array.navigation_titles );
		classes = getResources().getStringArray( R.array.navigation_classes );
		ArrayAdapter<String> adapter = new ArrayAdapter<String>( getActionBar().getThemedContext(), android.R.layout.simple_list_item_1, names );

		drawer = (DrawerLayout) findViewById( R.id.drawer_layout );
		navList = (ListView) findViewById( R.id.drawer );
		navList.setAdapter( adapter );
		drawerToggle = new ActionBarDrawerToggle( this, drawer, R.drawable.ic_drawer, R.string.open, R.string.close );
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

	private void updateContent() {
		getActionBar().setTitle( names[ selection ] );
		if ( selection != oldSelection ) {
			FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
			tx.replace( R.id.main, Fragment.instantiate( BatteryStateDisplayActivity.this, classes[ selection ] ) );
			tx.commit();
			oldSelection = selection;
		}
	}
}
