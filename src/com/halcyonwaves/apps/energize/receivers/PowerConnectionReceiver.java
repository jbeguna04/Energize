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

package com.halcyonwaves.apps.energize.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * This class is used as a receiver for broadcasts depending the change of the
 * power connections. It gets called if the user changes the way the device is
 * powered (e.g. the user plugs or unplugs the device from a USB or charging
 * device.
 * 
 * @author Tim Huetz
 */
public class PowerConnectionReceiver extends BroadcastReceiver {

	@Override
	public void onReceive( final Context arg0, final Intent arg1 ) {
		// TODO: implement this
	}
}
