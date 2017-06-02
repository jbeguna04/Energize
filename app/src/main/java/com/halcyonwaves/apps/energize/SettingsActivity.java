package com.halcyonwaves.apps.energize;

import android.app.Activity;
import android.os.Bundle;
import com.halcyonwaves.apps.energize.fragments.UnifiedPreferenceFragment;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.getFragmentManager().beginTransaction().replace(android.R.id.content, new UnifiedPreferenceFragment()).commit();
	}
}
