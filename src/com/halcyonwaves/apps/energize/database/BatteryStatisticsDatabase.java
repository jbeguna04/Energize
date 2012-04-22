package com.halcyonwaves.apps.energize.database;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressWarnings( "unused" )
public class BatteryStatisticsDatabase extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "batteryStatistics.db";
	private static final int DATABASE_VERSION = 1;

	public BatteryStatisticsDatabase( final Context context ) {
		super( context, BatteryStatisticsDatabase.DATABASE_NAME, null, BatteryStatisticsDatabase.DATABASE_VERSION );
	}

	@Override
	public void onCreate( final SQLiteDatabase database ) {
		RawBatteryStatisicsTable.onCreate( database );

	}

	@Override
	public void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		RawBatteryStatisicsTable.onUpgrade( db, oldVersion, newVersion );

	}

	public void storeBatteryLevelChange( final int rawBatteryLevel, final int batteryScale, final int batteryLevel ) {
		//
		final SimpleDateFormat s = new SimpleDateFormat( "yyyyMMddhhmmss" );
		final String format = s.format( new Date() );

		//
		final ContentValues valuesToInsert = new ContentValues();
		valuesToInsert.put( RawBatteryStatisicsTable.COLUMN_CHARGING_PCT, batteryLevel );
		valuesToInsert.put( RawBatteryStatisicsTable.COLUMN_EVENT_TIME, format );

		//
		this.getWritableDatabase().insert( RawBatteryStatisicsTable.TABLE_NAME, null, valuesToInsert );
	}
}
