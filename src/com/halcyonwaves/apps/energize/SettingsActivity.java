package com.halcyonwaves.apps.energize;

import java.util.List;

import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	@Override
	public void onBuildHeaders( List< Header > target ) {
		this.loadHeadersFromResource( R.xml.pref_headers, target );
	}
}
