package com.halcyonwaves.apps.energize.dialogs;

import android.app.DialogFragment;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.halcyonwaves.apps.energize.R;

public class AboutDialog extends DialogFragment {

	private final static String TAG = "AboutDialog";

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		final View inflatedView = inflater.inflate( R.layout.fragment_about, container, false );

		// set the application version for the about screen
		try {
			final TextView appVersionTextView = (TextView) inflatedView.findViewById( R.id.tv_about_application_version );
			appVersionTextView.setText( this.getString( R.string.textview_app_version, this.getActivity().getPackageManager().getPackageInfo( this.getActivity().getPackageName(), 0 ).versionName ) );
		} catch( final NameNotFoundException e ) {
			Log.e( AboutDialog.TAG, "Cannot query the application version for setting it in the about screen." );
		}

		// ensure that a button click will open the play store for rating the app
		final Button ratingButton = (Button) inflatedView.findViewById( R.id.btn_rate_app );
		ratingButton.setOnClickListener( new OnClickListener() {

			public void onClick( final View v ) {
				try {
					final Intent intent = new Intent( Intent.ACTION_VIEW );
					intent.setData( Uri.parse( "market://details?id=com.halcyonwaves.apps.energize" ) );
					AboutDialog.this.startActivity( intent );
				} catch( final Exception e ) {
					Log.e( AboutDialog.TAG, "Failed to open the Google Play store to rate the application!" );
				}
			}
		} );

		// ensure that the close button will close the dialog
		final Button closeButton = (Button) inflatedView.findViewById( R.id.btn_close_about_dialog );
		closeButton.setOnClickListener( new OnClickListener() {

			public void onClick( View v ) {
				AboutDialog.this.getDialog().dismiss();

			}
		} );

		// set the dialog title
		this.getDialog().setTitle( R.string.fragment_title_about );

		// return the created view
		return inflatedView;
	}
}
