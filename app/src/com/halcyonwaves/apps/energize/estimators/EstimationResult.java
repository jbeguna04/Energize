package com.halcyonwaves.apps.energize.estimators;

import android.os.Bundle;

public final class EstimationResult {

	public static EstimationResult fromBundle( final Bundle from ) {
		final boolean charging = from.getBoolean( "charging" );
		final boolean valid = from.getBoolean( "isValid" );
		final int lvl = from.getInt( "level", 0 );
		final int min = from.getInt( "minutes", 0 );
		if( valid ) {
			return new EstimationResult( min, lvl, charging );
		} else {
			return new EstimationResult();
		}
	}

	public final boolean charging;
	public final boolean isValid;
	public final int level;

	public final int minutes;

	public EstimationResult() {
		this.minutes = -1;
		this.level = -1;
		this.charging = false;
		this.isValid = false;
	}

	public EstimationResult( final int minutes, final int level, final boolean charging ) {
		this.minutes = minutes;
		this.level = level;
		this.charging = charging;
		this.isValid = true;
	}

	public Bundle toBundle() {
		final Bundle returnBundle = new Bundle();
		returnBundle.putBoolean( "charging", this.charging );
		returnBundle.putBoolean( "isValid", this.isValid );
		returnBundle.putInt( "level", this.level );
		returnBundle.putInt( "minutes", this.minutes );
		return returnBundle;
	}

}
