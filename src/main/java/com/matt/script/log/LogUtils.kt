package com.matt.script.log

import org.apache.log4j.Logger

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：11/20/20 11:10 AM
 * 描 述 ：
 * ============================================================
 **/
object LogUtils {
    fun loggerWrapper(clazz: Class<*>): Logger {
        return Logger.getLogger(clazz)
    }
}