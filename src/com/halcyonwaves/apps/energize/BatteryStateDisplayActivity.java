package com.halcyonwaves.apps.energize;

import android.app.Activity;
import android.os.Bundle;

/**
 * This class defines the behavior of the first activity the user sees after he
 * or she started the application through the launcher entry or by clicking the
 * item in the status bar of the device.
 * 
 * @author Tim Hütz
 */
public class BatteryStateDisplayActivity extends Activity {

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setContentView( R.layout.activity_batterystatedisplay );
	}
}
