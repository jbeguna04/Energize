package com.halcyonwaves.apps.energize.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.halcyonwaves.apps.energize.R;

public class DisplayPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_display );
	}
}
