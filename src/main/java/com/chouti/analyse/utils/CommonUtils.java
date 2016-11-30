package com.chouti.analyse.utils;

/*******************************************************************************
 * Copyright (c) 2005-2016 Gozap, Inc.
 * Contributors:
 * xiaoming  on 16-11-30.
 *******************************************************************************/
public class CommonUtils {

    /** 计算科技分类新闻热度分值 */
    public static Double calculateScoreForNews(Long createTime, Long votes) {
        /** 获取basetime基准时间 */
        long basetime = 1133999203000l;// 2005-12-08 07:46:43
        Double v = null;
        if (null == votes || votes < 0) {
            v = 0d;
        } else {
            v = votes / 1d;
        }
        if (v.equals(0d)) {
            // 清零时以当前时间为基准时间
            basetime = System.currentTimeMillis();
            double t = (createTime - basetime) / (45000d * 1000);
            return t;
        }

        double t = (createTime - basetime) / (45000d * 1000);
        // t = t + Math.log10(upsWithWeight);
        t = t + calculateMathLog(v, 4);
        return t;
    }

    /**
     * 计算log的算法
     *
     * @param value
     * @param base
     * @return
     */
    private static Double calculateMathLog(double value, double base) {
        return Math.log(value) / Math.log(base);
    }
}
