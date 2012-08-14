package com.halcyonwaves.apps.energize;

import com.halcyonwaves.apps.energize.fragments.GraphFragment;
import com.halcyonwaves.apps.energize.fragments.OverviewFragment;

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
				return new GraphFragment();
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
			default:
				Log.e( MainFragmentPagerAdapter.TAG, "Application requested an non-existing fragment title: " + position );
				return "";
		}
	}

	@Override
	public int getCount() {
		return 2;
	}

}
