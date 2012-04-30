/**
 * Energize - An Android battery monitor
 * Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.halcyonwaves.apps.energize.fragments;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.util.Log;

import com.halcyonwaves.apps.energize.R;

public class AboutPreferenceFragment extends PreferenceFragment {

	@Override
	public void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.addPreferencesFromResource( R.xml.pref_about );

		try {
			final Preference applicationVersionPreference = this.findPreference( "about.app_version" );
			applicationVersionPreference.setSummary( this.getActivity().getPackageManager().getPackageInfo( this.getActivity().getPackageName(), 0 ).versionName );
		} catch( final NameNotFoundException e ) {
			Log.e( "AboutPreferenceFragment", "Cannot find the preference key for setting up the application version" );
		}
	}
}
