package com.veloroute.duffylamb.veloroute;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

/**
 * https://stackoverflow.com/questions/27124412/android-no-default-constructor-in-view
 */

public class MaxSizeScrollView extends ScrollView {

	private Context context;

	public MaxSizeScrollView(Context context) {

		super(context);
		this.context = context;
	}

	public MaxSizeScrollView(Context context, AttributeSet attrs) {

		super(context, attrs);
		this.context = context;
	}

	public MaxSizeScrollView(Context context, AttributeSet attrs, int defStyleAttr) {

		super(context, attrs, defStyleAttr);
		this.context = context;
	}

	public MaxSizeScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {

		super(context, attrs, defStyleAttr, defStyleRes);
		this.context = context;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int maxHeight = DptoPx(getResources().getInteger(R.integer.route_container_max_size));

		if (MeasureSpec.getSize(heightMeasureSpec) > maxHeight) {
			heightMeasureSpec = MeasureSpec.makeMeasureSpec(maxHeight, MeasureSpec.AT_MOST);
		}
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	/**
	 * https://developer.android.com/guide/practices/screens_support.html#dips-pels
	 *
	 * @param dp
	 *
	 * @return
	 */
	private int DptoPx(int dp) {

		Log.e("DPtoPx", "DP:" + dp);

		float scale = getResources().getDisplayMetrics().density;
		return (int) (dp * scale + 0.5f);
	}
}
