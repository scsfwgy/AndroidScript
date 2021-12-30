package com.matt.script.log

import java.lang.IllegalArgumentException

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：11/20/20 11:10 AM
 * 描 述 ：
 * ============================================================
 **/
object LogUtils {
    fun throwWrapper(msg: String? = null) {
        throwWrapper(IllegalArgumentException(msg ?: ""))
    }

    fun throwWrapper(throwable: Throwable? = null) {
        throw throwable ?: IllegalArgumentException("默认抛出的错误")
    }
}