package com.example.z2wenfa.spinneredittextdemo;


import android.content.Context;

/**
 * 调用系统应用工具类
 * Created by z2wenfa on 2016/3/23.
 */
public class SystemUtil {

    /**
     * 根据手机的分辨率从 DP 的单位 转成为PX(像素)
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
