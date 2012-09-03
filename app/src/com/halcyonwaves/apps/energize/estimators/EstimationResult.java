package com.halcyonwaves.apps.energize.estimators;

public final class EstimationResult {

	public final int minutes;
	public final int level;
	public final boolean charging;
	public final boolean isValid;

	public EstimationResult() {
		this.minutes = -1;
		this.level = -1;
		this.charging = false;
		this.isValid = false;
	}
	
	public EstimationResult( int minutes, int level, boolean charging ) {
		this.minutes = minutes;
		this.level = level;
		this.charging = charging;
		this.isValid = true;
	}

}
