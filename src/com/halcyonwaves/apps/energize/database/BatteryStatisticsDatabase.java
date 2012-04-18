package com.halcyonwaves.apps.energize.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatteryStatisticsDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "batteryStatistics.db";
	private static final int DATABASE_VERSION = 1;

	public BatteryStatisticsDatabase( final Context context ) {
		super( context, BatteryStatisticsDatabase.DATABASE_NAME, null, BatteryStatisticsDatabase.DATABASE_VERSION );
	}

	@Override
	public void onCreate( final SQLiteDatabase arg0 ) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// TODO Auto-generated method stub

	}

}
