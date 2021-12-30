package com.matt.script.utils.blankj;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2021/7/7 7:21 PM
 * 描 述 ：
 * ============================================================
 **/
public final class TimeConstants {

    public static final int MSEC = 1;
    public static final int SEC  = 1000;
    public static final int MIN  = 60000;
    public static final int HOUR = 3600000;
    public static final int DAY  = 86400000;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Unit {
    }
}
