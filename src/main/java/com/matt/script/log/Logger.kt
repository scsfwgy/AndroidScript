package com.matt.script.log

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2020/11/10 11:04 AM
 * 描 述 ：
 * ============================================================
 **/
interface Logger {
    fun d(TAG: String?, msg: String?, debug: Boolean = true)
    fun e(TAG: String?, msg: String?, debug: Boolean = true)
}