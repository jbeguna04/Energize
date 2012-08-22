package com.halcyonwaves.apps.energize;

import com.halcyonwaves.apps.energize.fragments.AboutFragment;
import com.halcyonwaves.apps.energize.fragments.BatteryCapacityGraphFragment;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;
import com.halcyonwaves.apps.energize.fragments.TemperatureGraphFragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;


public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
	
	private static final String TAG = "MainFragmentPagerAdapter";
	private Context appContext = null;

	public MainFragmentPagerAdapter( Context context, FragmentManager fm ) {
		super( fm );
		this.appContext = context;
	}

	@Override
	public Fragment getItem( int position ) {
		switch( position ) {
			case 0:
				return new OverviewFragment();
			case 1:
				return new BatteryCapacityGraphFragment();
			case 2:
				return new TemperatureGraphFragment();
			case 3:
				return new AboutFragment();
			default:
				Log.e( MainFragmentPagerAdapter.TAG, "Application requested an non-existing fragment: " + position );
				return null;
		}
	}
	
	@Override
	public CharSequence getPageTitle( int position ) {
		switch( position ) {
			case 0:
				return this.appContext.getString( R.string.fragment_title_overview );
			case 1:
				return this.appContext.getString( R.string.fragment_title_batterygraph );
			case 2:
				return this.appContext.getString( R.string.fragment_title_temperaturegraph );
			case 3:
				return this.appContext.getString( R.string.fragment_title_about );
			default:
				Log.e( MainFragmentPagerAdapter.TAG, "Application requested an non-existing fragment title: " + position );
				return "";
		}
	}

	@Override
	public int getCount() {
		return 4;
	}

}
