package com.matt.script.test

import java.text.MessageFormat

/**
 * ============================================================
 * 作 者 :    wgyscsf@163.com
 * 创建日期 ：2021/8/27 2:55 下午
 * 描 述 ：
 * ============================================================
 **/
object StringWrapper {
    @JvmStatic
    fun format(format: String, vararg args: Any?): String {
        return try {
            val map = args.map { it.toString() }.toTypedArray()
            MessageFormat.format(format, *map)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * https://blog.csdn.net/wxy318/article/details/52757292
     * OKHttp java.lang.IllegalArgumentException: Unexpected char
     */
    fun encodeHeadInfo(headInfo: String): String {
        val stringBuffer = StringBuffer()
        var i = 0
        val length = headInfo.length
        while (i < length) {
            val c = headInfo[i]
            if (c <= '\u001f' || c >= '\u007f') {
                stringBuffer.append(String.format("\\u%04x", c.toInt()))
            } else {
                stringBuffer.append(c)
            }
            i++
        }
        return stringBuffer.toString()
    }
}

fun String.formatWrapper(vararg args: Any?): String {
    return StringWrapper.format(this, *args)
}