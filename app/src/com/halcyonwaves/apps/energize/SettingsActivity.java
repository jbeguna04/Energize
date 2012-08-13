/**
 * Energize - An Android battery monitor Copyright (C) 2012 Tim Huetz
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.halcyonwaves.apps.energize;

import com.halcyonwaves.apps.energize.fragments.UnifiedPreferenceFragment;

import android.app.Activity;
import android.os.Bundle;

public class SettingsActivity extends Activity {

	@Override
	protected void onCreate( final Bundle savedInstanceState ) {
		super.onCreate( savedInstanceState );
		this.setTheme( ApplicationCore.getSelectedThemeId( this.getApplicationContext() ) );
		this.getFragmentManager().beginTransaction().replace( android.R.id.content, new UnifiedPreferenceFragment() ).commit();
	}
}
