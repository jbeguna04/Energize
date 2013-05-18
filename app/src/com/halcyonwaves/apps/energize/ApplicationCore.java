package com.halcyonwaves.apps.energize;

import java.util.Iterator;
import java.util.List;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.util.Log;

@ReportsCrashes(
		formKey = "",
		mailTo = "energize@halcyonwaves.com",
		customReportContent = {
				ReportField.ANDROID_VERSION,
				ReportField.APP_VERSION_CODE,
				ReportField.APP_VERSION_NAME,
				ReportField.APPLICATION_LOG,
				ReportField.AVAILABLE_MEM_SIZE,
				ReportField.BRAND,
				ReportField.BUILD,
				ReportField.CRASH_CONFIGURATION,
				ReportField.CUSTOM_DATA,
				ReportField.DEVICE_FEATURES,
				// ReportField.DEVICE_ID,
				ReportField.DISPLAY,
				// ReportField.DROPBOX,
				// ReportField.DUMPSYS_MEMINFO,
				ReportField.ENVIRONMENT,
				ReportField.EVENTSLOG,
				ReportField.FILE_PATH,
				ReportField.INITIAL_CONFIGURATION,
				// ReportField.INSTALLATION_ID,
				ReportField.IS_SILENT,
				ReportField.LOGCAT,
				ReportField.MEDIA_CODEC_LIST,
				ReportField.PACKAGE_NAME,
				ReportField.PHONE_MODEL,
				ReportField.PRODUCT,
				ReportField.RADIOLOG,
				ReportField.REPORT_ID,
				// ReportField.SETTINGS_SECURE,
				// ReportField.SETTINGS_SYSTEM,
				ReportField.SHARED_PREFERENCES,
				ReportField.STACK_TRACE,
				ReportField.THREAD_DETAILS,
				ReportField.TOTAL_MEM_SIZE,
				ReportField.USER_APP_START_DATE,
				ReportField.USER_COMMENT,
				ReportField.USER_CRASH_DATE,
		// ReportField.USER_EMAIL
		},
		mode = ReportingInteractionMode.DIALOG,
		sendReportsInDevMode = false,
		resDialogTitle = R.string.dlg_title_appcrash,
		resDialogText = R.string.dlg_msg_appcrash )
public class ApplicationCore extends Application {

	private static final String TAG = "ApplicationCore";
	
	@Override
	public void onCreate() {
		super.onCreate();
		ACRA.init( this );
	}

	public static boolean isServiceRunning( final Context ctx, final String serviceName ) {
		Log.v( ApplicationCore.TAG, "Checking if the monitoring service is running or not..." );
		boolean serviceRunning = false;
		final ActivityManager am = (ActivityManager) ctx.getSystemService( Context.ACTIVITY_SERVICE );
		final List< ActivityManager.RunningServiceInfo > l = am.getRunningServices( 50 );
		final Iterator< ActivityManager.RunningServiceInfo > i = l.iterator();
		while( i.hasNext() ) {
			final ActivityManager.RunningServiceInfo runningServiceInfo = i.next();

			if( runningServiceInfo.service.getClassName().equals( serviceName ) && runningServiceInfo.started ) {
				serviceRunning = true;
			}
		}
		return serviceRunning;
	}
}
