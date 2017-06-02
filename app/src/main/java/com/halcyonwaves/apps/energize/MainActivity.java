package com.halcyonwaves.apps.energize;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.halcyonwaves.apps.energize.dialogs.ChangeLogDialog;
import com.halcyonwaves.apps.energize.fragments.BatteryCapacityGraphFragment;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;
import com.halcyonwaves.apps.energize.fragments.TemperatureGraphFragment;
import com.halcyonwaves.apps.energize.services.MonitorBatteryStateService;

public class MainActivity extends AppCompatActivity
		implements NavigationView.OnNavigationItemSelectedListener {

	private static final String TAG = "MainActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
				this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
		drawer.setDrawerListener(toggle);
		toggle.syncState();

		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		// check if the service is running, if not start it
		if (!ApplicationCore.isServiceRunning(this, MonitorBatteryStateService.class.getName())) {
			Log.v(MainActivity.TAG, "Monitoring service is not running, starting it...");
			this.getApplicationContext().startService(new Intent(this.getApplicationContext(), MonitorBatteryStateService.class));
		}

		// show the changelog dialog
		ChangeLogDialog changeDlg = new ChangeLogDialog(this);
		changeDlg.show();

		// ensure the first item will be displayed
		selectItem(0);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public boolean onNavigationItemSelected(@NonNull MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();

		if (id == R.id.nav_overview) {
			selectItem(0);
		} else if (id == R.id.nav_battery_graph) {
			selectItem(1);
		} else if (id == R.id.nav_temperature_graph) {
			selectItem(2);
		} else if (id == R.id.nav_settings) {
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			startActivity(settingsIntent);
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		return true;
	}

	private void selectItem(int position) {
		// check that the activity is using the layout version with the fragment_container FrameLayout (the one-pane layout)
		if (this.findViewById(R.id.fragment_container) != null) {

			// create a new Fragment to be placed in the activity layout
			Fragment firstFragment = null;
			switch (position) {
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
			firstFragment.setArguments(this.getIntent().getExtras());

			// add the fragment to the 'fragment_container' FrameLayout
			this.getFragmentManager().beginTransaction().replace(R.id.fragment_container, firstFragment).commit();
		}
	}
}
