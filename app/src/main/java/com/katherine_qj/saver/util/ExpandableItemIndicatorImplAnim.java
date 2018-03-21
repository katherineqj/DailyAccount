package com.katherine_qj.saver.util;

/**
 * Created by katherineqj on 2017/11/12.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.katherine_qj.saver.R;

import com.wnafee.vector.MorphButton;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class ExpandableItemIndicatorImplAnim extends ExpandableItemIndicator.Impl {
    private MorphButton mMorphButton;

    @Override
    public void onInit(Context context, AttributeSet attrs, int defStyleAttr, ExpandableItemIndicator thiz) {
        View v = LayoutInflater.from(context).inflate(R.layout.widget_expandable_item_indicator_anim, thiz, true);
        mMorphButton = (MorphButton) v.findViewById(R.id.morph_button);
    }

    @Override
    public void setExpandedState(boolean isExpanded, boolean animate) {
        MorphButton.MorphState indicatorState = (isExpanded) ? MorphButton.MorphState.START : MorphButton.MorphState.END;

        if (mMorphButton.getState() != indicatorState) {
            mMorphButton.setState(indicatorState, animate);
        }
    }
}
