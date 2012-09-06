package com.halcyonwaves.apps.energize.estimators;

public final class EstimationResult {

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

}
