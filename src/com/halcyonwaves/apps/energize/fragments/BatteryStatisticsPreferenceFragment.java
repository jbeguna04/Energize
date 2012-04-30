package com.halcyonwaves.apps.energize.fragments;

import com.halcyonwaves.apps.energize.R;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class BatteryStatisticsPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_batterystatistics );
	}
}
