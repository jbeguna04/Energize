package com.halcyonwaves.apps.energize.database;

import android.database.sqlite.SQLiteDatabase;

public final class RawBatteryStatisicsTable {

	public static final String COLUMN_CHARGING_PCT = "chargePct";
	public static final String COLUMN_EVENT_TIME = "eventTime";
	public static final String COLUMN_ID = "_id";
	private static final String TABLE_CREATE = "CREATE TABLE " + RawBatteryStatisicsTable.TABLE_NAME + "( " + RawBatteryStatisicsTable.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + RawBatteryStatisicsTable.COLUMN_EVENT_TIME + " INTEGER, " + RawBatteryStatisicsTable.COLUMN_CHARGING_PCT + " INTEGER );";

	public static final String TABLE_NAME = "rawBatteryStats";

	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( RawBatteryStatisicsTable.TABLE_CREATE );
	}

	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// currently there is just one version, so we don't have to upgrade the
		// database
	}
}
