/**
 * Energize - An Android battery monitor Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.halcyonwaves.apps.energize.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.Preference.OnPreferenceClickListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.halcyonwaves.apps.energize.R;

public class AboutPreferenceFragment extends PreferenceFragment {

	private Preference whatsNewPreference = null;
	private Preference showLicencePreference = null;

	private void showWhatsNewDialog() {
		LayoutInflater inflater = LayoutInflater.from( this.getActivity() );

		View view = inflater.inflate( R.layout.dialog_whatsnew, null );

		AlertDialog.Builder builder = new AlertDialog.Builder( this.getActivity() );

		builder.setView( view ).setTitle( R.string.dialog_title_whatsnew ).setPositiveButton( android.R.string.ok, new OnClickListener() {

			public void onClick( DialogInterface dialog, int which ) {
				dialog.dismiss();
			}
		} );

		builder.create().show();
	}

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

		this.showLicencePreference = this.findPreference( "about.licence" );
		this.showLicencePreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				Intent i = new Intent( Intent.ACTION_VIEW );
				i.setData( Uri.parse( "http://www.gnu.org/copyleft/gpl.html" ) );
				startActivity( i );
				return false;
			}
		} );

		this.whatsNewPreference = this.findPreference( "about.whatsnew" );
		this.whatsNewPreference.setOnPreferenceClickListener( new OnPreferenceClickListener() {

			public boolean onPreferenceClick( Preference preference ) {
				AboutPreferenceFragment.this.showWhatsNewDialog();
				return false;
			}
		} );
	}
}
