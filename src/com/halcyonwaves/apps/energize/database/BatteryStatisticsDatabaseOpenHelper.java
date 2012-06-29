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

package com.halcyonwaves.apps.energize.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatteryStatisticsDatabaseOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "batteryUsageStatistics.db";
	public static final int DATABASE_VERSION = 1;

	public BatteryStatisticsDatabaseOpenHelper( Context context ) {
		super( context, BatteryStatisticsDatabaseOpenHelper.DATABASE_NAME, null, BatteryStatisticsDatabaseOpenHelper.DATABASE_VERSION );
	}

	@Override
	public void onCreate( SQLiteDatabase db ) {
		RawBatteryStatisicsTable.onCreate( db );
	}

	@Override
	public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		RawBatteryStatisicsTable.onUpgrade( db, oldVersion, newVersion );
	}

}
