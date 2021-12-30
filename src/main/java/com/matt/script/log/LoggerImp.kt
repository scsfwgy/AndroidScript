package com.matt.script.log

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2020/11/10 11:04 AM
 * 描 述 ：
 * ============================================================
 **/
class LoggerImp : Logger {
    companion object {
        val logger: Logger by lazy {
            LoggerImp()
        }
    }

    override fun d(TAG: String?, msg: String?, debug: Boolean) {
        if (debug) {
            println(msg)
        }
    }

    override fun e(TAG: String?, msg: String?, debug: Boolean) {
        if (debug) {
            println(msg)
        }
    }
}