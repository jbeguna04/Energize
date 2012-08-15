package com.halcyonwaves.apps.energize.fragments;

import com.halcyonwaves.apps.energize.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AboutFragment extends Fragment {

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState ) {
		View inflatedView = inflater.inflate( R.layout.fragment_about, container, false );
		return inflatedView;
	}
}
