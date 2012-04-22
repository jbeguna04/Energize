package com.halcyonwaves.apps.energize.fragments;

import com.halcyonwaves.apps.energize.R;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;


public class AboutPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_about );
		
		try {
			Preference applicationVersionPreference = this.findPreference( "about.app_version" );
			applicationVersionPreference.setSummary( this.getActivity().getPackageManager().getPackageInfo(this.getActivity().getPackageName(), 0).versionName );
		} catch( NameNotFoundException e ) {
			Log.e( "AboutPreferenceFragment", "Cannot find the preference key for setting up the application version" );
		}
	}
}
