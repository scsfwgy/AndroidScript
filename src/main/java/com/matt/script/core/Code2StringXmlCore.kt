package com.matt.script.core

import com.matt.script.utils.RegexUtilsWrapper
import java.io.File

object Code2StringXmlCore {
//    fun line2SortMap(
//        file: File,
//        line: String,
//        valueRegex: String,
//        map: MutableMap<String, String>,
//    ): MutableMap<String, String> {
//        RegexUtilsWrapper.line2FormatLine(line,)
//    }

    fun value2StringKeyWrapper(file: File, value: String, map: MutableMap<String, String>): String {
        val key = value2StringKey(file, value, map)
        return """MyContextUtils.getString(R.string.${key})"""
    }

    fun value2StringKey(file: File, value: String, map: MutableMap<String, String>): String {
        val key = map.filterValues { it == value }.keys.firstOrNull()
        return if (key == null) {
            val generateNewKey = generateNewKey(file, map)
            map[generateNewKey] = value
            generateNewKey
        } else {
            key
        }
    }

    fun generateNewKey(file: File, map: MutableMap<String, String>): String {
        val key = file.name + "_" + System.currentTimeMillis()
        if (map[key] != null) {
            throw IllegalAccessException("新生成的key已存在！！！")
        }
        return key
    }
}