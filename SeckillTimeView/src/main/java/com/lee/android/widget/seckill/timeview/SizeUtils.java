package com.lee.android.widget.seckill.timeview;

import android.content.Context;
import android.util.TypedValue;

public class SizeUtils {

    public static int dp2px(Context context, float dip) {
        if (context == null) {
            return 0;
        }
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, context.getResources().getDisplayMetrics()) + 0.5f);
    }

    public static int px2sp(Context context, int px) {
        if (context == null) {
            return 0;
        }
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, context.getResources().getDisplayMetrics());
    }
}
