package com.cryptaur.lottery.view;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;

public class DetectVisibleConstraintLayout extends ConstraintLayout {

    private VisibilityAggregatedListener visibilityAggregatedListener;

    public DetectVisibleConstraintLayout(Context context) {
        super(context);
    }

    public DetectVisibleConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DetectVisibleConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onVisibilityAggregated(boolean isVisible) {
        super.onVisibilityAggregated(isVisible);
        if (visibilityAggregatedListener != null)
            visibilityAggregatedListener.onVisibilityAggregated(isVisible);
    }

    public void setVisibilityAggregatedListener(VisibilityAggregatedListener visibilityAggregatedListener) {
        this.visibilityAggregatedListener = visibilityAggregatedListener;
    }

    public interface VisibilityAggregatedListener {
        void onVisibilityAggregated(boolean isVisible);
    }
}
