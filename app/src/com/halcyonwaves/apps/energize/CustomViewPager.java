package com.halcyonwaves.apps.energize;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 
 * @author Santiago Valdarrama
 *
 */
public class CustomViewPager extends ViewPager {

	private boolean enabled;

	public CustomViewPager( final Context context, final AttributeSet attrs ) {
		super( context, attrs );
		this.enabled = true;
	}

	@Override
	public boolean onInterceptTouchEvent( final MotionEvent event ) {
		if( this.enabled ) {
			return super.onInterceptTouchEvent( event );
		}

		return false;
	}

	@Override
	public boolean onTouchEvent( final MotionEvent event ) {
		if( this.enabled ) {
			return super.onTouchEvent( event );
		}

		return false;
	}

	public void setPagingEnabled( final boolean enabled ) {
		this.enabled = enabled;
	}

	public void togglePagingEnabled() {
		this.enabled = !this.enabled;

	}
}
