package com.matt.script.config

import org.apache.log4j.Logger

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：11/20/20 11:10 AM
 * 描 述 ：
 * ============================================================
 **/
object LogWrapper {
    fun loggerWrapper(any: Any): Logger {
        return loggerWrapper(any.javaClass)
    }

    fun loggerWrapper(clazz: Class<*>): Logger {
        return loggerWrapper(clazz.simpleName)
    }

    fun loggerWrapper(tag: String): Logger {
        return Logger.getLogger(tag)
    }
}