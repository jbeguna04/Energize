package com.halcyonwaves.apps.energize;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.halcyonwaves.apps.energize.fragments.BatteryCapacityGraphFragment;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;
import com.halcyonwaves.apps.energize.fragments.TemperatureGraphFragment;

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

	private static final String TAG = "MainFragmentPagerAdapter";
	private Context appContext = null;

	public MainFragmentPagerAdapter( final Context context, final FragmentManager fm ) {
		super( fm );
		this.appContext = context;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public Fragment getItem( final int position ) {
		switch ( position ) {
			case 0:
				return new OverviewFragment();
			case 1:
				return new BatteryCapacityGraphFragment();
			case 2:
				return new TemperatureGraphFragment();
			default:
				Log.e( MainFragmentPagerAdapter.TAG, "Application requested an non-existing fragment: " + position );
				return null;
		}
	}

	@Override
	public CharSequence getPageTitle( final int position ) {
		switch ( position ) {
			case 0:
				return this.appContext.getString( R.string.fragment_title_overview );
			case 1:
				return this.appContext.getString( R.string.fragment_title_batterygraph );
			case 2:
				return this.appContext.getString( R.string.fragment_title_temperaturegraph );
			default:
				Log.e( MainFragmentPagerAdapter.TAG, "Application requested an non-existing fragment title: " + position );
				return "";
		}
	}

}
