package com.matt.script.core.interfaces

import java.io.File

/**
 * 文件单行Hook
 */
interface LinePretreatmentHook {
    /**
     * @return first:是否拦截 second:拦截处理后的行。注意：不管是否拦截，后续处理都按照这个拦截后的行进行处理。
     */
    fun line2NewLineHook(file: File, line: String): Pair<Boolean, String>
}