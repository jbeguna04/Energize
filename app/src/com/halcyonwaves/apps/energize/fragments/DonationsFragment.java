package com.halcyonwaves.apps.energize.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.halcyonwaves.apps.energize.R;

public class DonationsFragment extends Fragment {

	@Override
	public View onCreateView( final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState ) {
		// inflate the static part of the view
		final View inflatedView = inflater.inflate( R.layout.fragment_donations, container, false );

		// return the inflated view
		return inflatedView;
	}

}
