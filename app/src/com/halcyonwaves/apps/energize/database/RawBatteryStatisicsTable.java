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

import android.database.sqlite.SQLiteDatabase;

public final class RawBatteryStatisicsTable {

	public static final String COLUMN_CHARGING_LEVEL = "chargingLevel";
	public static final String COLUMN_CHARGING_SCALE = "chargingScale";
	public static final String COLUMN_EVENT_TIME = "eventTime";
	public static final String COLUMN_CHARGING_STATE = "chargingState";
	public static final String COLUMN_BATTERY_TEMPRATURE = "batteryTemprature";
	public static final String COLUMN_ID = "_id";
	private static final String TABLE_CREATE = "CREATE TABLE " + RawBatteryStatisicsTable.TABLE_NAME + "( " + RawBatteryStatisicsTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " INTEGER, " + RawBatteryStatisicsTable.COLUMN_CHARGING_STATE + " INTEGER, " + RawBatteryStatisicsTable.COLUMN_CHARGING_LEVEL + " INTEGER, " + RawBatteryStatisicsTable.COLUMN_CHARGING_SCALE + " INTEGER, " + RawBatteryStatisicsTable.COLUMN_BATTERY_TEMPRATURE + " INTEGER );";
	private static final String TABLE_DROP = "DROP TABLE " + RawBatteryStatisicsTable.TABLE_NAME + ";";
	
	public static final int CHARGING_STATE_DISCHARGING = 0;
	public static final int CHARGING_STATE_CHARGING_AC = 1;
	public static final int CHARGING_STATE_CHARGING_USB = 2;

	public static final String TABLE_NAME = "rawBatteryStats";

	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( RawBatteryStatisicsTable.TABLE_CREATE );
	}

	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( RawBatteryStatisicsTable.TABLE_DROP );
		
		// ... and to create a new one
		db.execSQL( RawBatteryStatisicsTable.TABLE_CREATE );
	}
}
