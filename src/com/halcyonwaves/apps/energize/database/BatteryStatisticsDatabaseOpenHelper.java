package com.halcyonwaves.apps.energize.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BatteryStatisticsDatabaseOpenHelper extends SQLiteOpenHelper {
	
	public static final String DATABASE_NAME = "_id";
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
